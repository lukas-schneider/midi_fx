package de.lukas_schneider.midi_fx;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class Recorder {
  private final static File OUTPUT_DIR = Main.OUTPUT_DIR;
  private final static String chars = "abcdefghijklmnopqrstuvwxyz0123456789";

  private static String makeId(int length) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int rand = (int) (Math.random() * chars.length());
      sb.append(chars.charAt(rand));
    }
    return sb.toString();
  }

  private File recordDir;

  Recorder() {
    String recordId = makeId(8);
    try {
      recordDir = new File(OUTPUT_DIR, recordId);
      if (!recordDir.mkdir()) throw new Exception("failed to create dir");
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  void write(BufferedImage img, int frame) {
    try {
      File file = new File(recordDir, String.format("%06d", frame) + ".png");
      ImageIO.write(img, "png", file);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  void addFrame(GLAutoDrawable glAutoDrawable, int frame) {
    AWTGLReadBufferUtil glReadBufferUtil = new AWTGLReadBufferUtil(glAutoDrawable.getGLProfile(), false);
    write(glReadBufferUtil.readPixelsToBufferedImage(glAutoDrawable.getGL(), true), frame);
  }
}
