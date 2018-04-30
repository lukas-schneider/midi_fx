package de.lukasschneider.midifx;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static de.lukasschneider.midifx.Renderer.FRAMES_PER_SECOND;

public class Recorder {
  private final static String EXT = "mp4";
  private final static PixelFormat.Type PIXEL_FORMAT = PixelFormat.Type.PIX_FMT_YUV420P;

  private MediaPictureConverter converter;
  private Muxer muxer;
  private Encoder encoder;
  private MediaPacket packet;
  private MediaPicture picture;

  public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
    BufferedImage image;

    if (sourceImage.getType() == targetType)
      image = sourceImage;
    else {
      image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
      image.getGraphics().drawImage(sourceImage, 0, 0, null);
    }

    return image;
  }

  public Recorder(int width, int height) throws InterruptedException, IOException {
    final String fileName = new Date() + "." + EXT;
    final Rational frameRate = Rational.make(1, FRAMES_PER_SECOND);

    muxer = Muxer.make(fileName, null, EXT);

    final MuxerFormat format = muxer.getFormat();

    final Codec codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());

    encoder = Encoder.make(codec);

    encoder.setWidth(width);
    encoder.setHeight(height);

    encoder.setPixelFormat(PIXEL_FORMAT);
    encoder.setTimeBase(frameRate);

    if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
      encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

    encoder.open(null, null);

    muxer.addNewStream(encoder);

    picture = MediaPicture.make(
        encoder.getWidth(),
        encoder.getHeight(),
        PIXEL_FORMAT);

    picture.setTimeBase(frameRate);

    muxer.open(null, null);

    packet = MediaPacket.make();

    System.out.println("started recording");
  }

  public void addFrame(GLAutoDrawable glAutoDrawable, int frame, long nanoTime) {
    final AWTGLReadBufferUtil glReadBufferUtil = new AWTGLReadBufferUtil(glAutoDrawable.getGLProfile(), true);
    final BufferedImage image = glReadBufferUtil.readPixelsToBufferedImage(glAutoDrawable.getGL(), true);

    System.out.println(image.getWidth() + " " + image.getHeight());

    System.out.println("added frame " + frame + " at " + nanoTime);

    final BufferedImage converted = convertToType(image, BufferedImage.TYPE_3BYTE_BGR);

    if (converter == null)
      converter = MediaPictureConverterFactory.createConverter(converted, picture);

    converter.toPicture(picture, converted, frame);

    do {
      encoder.encode(packet, picture);
      if (packet.isComplete())
        muxer.write(packet, false);
    } while (packet.isComplete());
  }

  public void close() {
    do {
      encoder.encode(packet, null);
      if (packet.isComplete())
        muxer.write(packet,  false);
    } while (packet.isComplete());

    muxer.close();

    System.out.println("stopped recording");
  }
}
