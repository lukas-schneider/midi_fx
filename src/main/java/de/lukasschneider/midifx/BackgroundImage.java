package de.lukasschneider.midifx;

import com.jogamp.opengl.GL2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;


public class BackgroundImage implements Drawable {

  static int BYTES_PER_PIXEL = 4;

  private ByteBuffer buffer;
  private int width;
  private int height;

  private int[] textureIndices = new int[1];

  public BackgroundImage(File file) throws IOException {
    BufferedImage image = ImageIO.read(file);

    int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

    buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);
    width = image.getWidth();
    height = image.getHeight();

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int pixel = pixels[y * width + x];
        buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
        buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
        buffer.put((byte) (pixel & 0xFF));               // Blue component
        buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
      }
    }

  }

  public void init(GL2 gl) {

    gl.glGenTextures(1, textureIndices, 0);

    gl.glBindTexture(GL2.GL_TEXTURE_2D, textureIndices[0]);

    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_BASE_LEVEL, 0);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL, 0);

    buffer.clear();

    gl.glTexImage2D(
        GL2.GL_TEXTURE_2D,
        0,
        GL2.GL_RGBA,
        width,
        height,
        0,
        GL2.GL_RGBA,
        GL2.GL_UNSIGNED_BYTE,
        buffer
    );

    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);

  }

  @Override
  public void draw(GL2 gl, long nanoTime) {
    drawStatic(gl);
  }

  @Override
  public void drawStatic(GL2 gl) {
    gl.glEnable(GL2.GL_TEXTURE_2D);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, textureIndices[0]);

    Colors.set(gl, Colors.WHITE);
    gl.glBegin(GL2.GL_QUADS);

    gl.glTexCoord2f(0.0f, 0.0f);
    gl.glVertex2d(0.0, 0.0);

    gl.glTexCoord2f(1.0f, 0.0f);
    gl.glVertex2d(NoteArea.WIDTH, 0.0);

    gl.glTexCoord2f(1.0f, 1.0f);
    gl.glVertex2d(NoteArea.WIDTH, NoteArea.HEIGHT);

    gl.glTexCoord2f(0.0f, 1.0f);
    gl.glVertex2d(0.0, NoteArea.HEIGHT);

    gl.glEnd();
    gl.glDisable(GL2.GL_TEXTURE_2D);
  }
}