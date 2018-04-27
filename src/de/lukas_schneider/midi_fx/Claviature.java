package de.lukas_schneider.midi_fx;

import com.jogamp.opengl.GL2;

import java.awt.*;
import java.util.Set;

class Claviature implements Drawable {

  static final int MAX_KEYS = 128;

  // standard 88 key configuration
  static final int LOWEST_KEY = 21;     // A0
  static final int LOWEST_C = 24;     // C0
  static final int HIGHEST_KEY = 108;    // C8
  static final int WHITE_KEYS = 52;
  static final int KEYS_PER_OCTAVE = 12;
  static final int WHITE_KEYS_PER_OCTAVE = 7;

  static final double WIDTH = 1920.0;
  static final double HEIGHT = 1080.0;

  static final double LEFT_OFFSET = 0.0;
  static final double RIGHT_OFFSET = 0.0;
  static final double TOP_OFFSET = 858.5;
  static final double BOTTOM_OFFSET = 0.0;

  // black key to white key height ratio
  static final double BLACK_KEY_HEIGHT_RATIO = 0.65;


  static final double WHITE_KEY_WIDTH = ((WIDTH - LEFT_OFFSET - RIGHT_OFFSET) / WHITE_KEYS);
  static final double BLACK_KEY_WIDTH = (WHITE_KEY_WIDTH * 0.5);

  static final double OCTAVE_WIDTH = (WHITE_KEYS_PER_OCTAVE * WHITE_KEY_WIDTH);

  static final double WHITE_KEY_HEIGHT = HEIGHT - TOP_OFFSET - BOTTOM_OFFSET;
  static final double BLACK_KEY_HEIGHT = WHITE_KEY_HEIGHT * BLACK_KEY_HEIGHT_RATIO;

  //position relative to octave start
  static final double[] KEY_POSITION = {
      (0.0),                                              // C
      (1.0 * WHITE_KEY_WIDTH) - (BLACK_KEY_WIDTH / 2.0),  // CIS
      (1.0 * WHITE_KEY_WIDTH),                            // D
      (2.0 * WHITE_KEY_WIDTH) - (BLACK_KEY_WIDTH / 2.0),  // DIS
      (2.0 * WHITE_KEY_WIDTH),                            // E
      (3.0 * WHITE_KEY_WIDTH),                            // F
      (4.0 * WHITE_KEY_WIDTH) - (BLACK_KEY_WIDTH / 2.0),  // FIS
      (4.0 * WHITE_KEY_WIDTH),                            // G
      (5.0 * WHITE_KEY_WIDTH) - (BLACK_KEY_WIDTH / 2.0),  // GIS
      (5.0 * WHITE_KEY_WIDTH),                            // A
      (6.0 * WHITE_KEY_WIDTH) - (BLACK_KEY_WIDTH / 2.0),  // AIS
      (6.0 * WHITE_KEY_WIDTH)                             // H
  };

  static final double FIRST_C_POSITION =
      OCTAVE_WIDTH - KEY_POSITION[
          (LOWEST_KEY + KEYS_PER_OCTAVE - LOWEST_C) % KEYS_PER_OCTAVE
          ];
  private NoteSequence sequence;


  // LOWEST_C starts octave 0, LOWEST_KEY is in octave -1
  static int getOctave(int key) {
    int index = key - LOWEST_C;
    return index >= 0 ? (index / KEYS_PER_OCTAVE) : -1;
  }

  // C=0 to B=11
  static int getIndexInOctave(int key) {
    return (key + KEYS_PER_OCTAVE - LOWEST_C) % KEYS_PER_OCTAVE;
  }

  static double getPositionInOctave(int key) {
    return KEY_POSITION[getIndexInOctave(key)];
  }

  static double getPosition(int key) {
    return FIRST_C_POSITION + (getOctave(key) * OCTAVE_WIDTH) + getPositionInOctave(key);
  }

  static boolean isBlack(int key) {
    int index = getIndexInOctave(key);
    return (index == 1 || index == 3 || index == 6 || index == 8 || index == 10);
  }

  static boolean isNextBlack(int key) {
    return isBlack(key + 1) && key != HIGHEST_KEY;
  }

  static boolean isPrevBlack(int key) {
    return isBlack(key - 1) && key != LOWEST_KEY;
  }

  static double getWidth(int key) {
    return isBlack(key) ? BLACK_KEY_WIDTH : WHITE_KEY_WIDTH;
  }

  Claviature(NoteSequence sequence) {
    this.sequence = sequence;
  }

  private void drawKey(GL2 gl, int key, Color c) {
    double xStart = LEFT_OFFSET + getPosition(key);
    double yStart = TOP_OFFSET;

    gl.glColor3ub((byte) c.getRed(), (byte) c.getGreen(), (byte) c.getBlue());

    if (isBlack(key)) {
      gl.glBegin(GL2.GL_POLYGON);
      gl.glVertex2d(xStart, yStart);
      gl.glVertex2d(xStart + BLACK_KEY_WIDTH, yStart);
      gl.glVertex2d(xStart + BLACK_KEY_WIDTH, yStart + BLACK_KEY_HEIGHT);
      gl.glVertex2d(xStart, yStart + BLACK_KEY_HEIGHT);
      gl.glEnd();
      return;
    }

    // key is white

    // draw first half
    if (isPrevBlack(key)) {
      gl.glBegin(GL2.GL_TRIANGLE_FAN); //convex polygon

      gl.glVertex2d(xStart + BLACK_KEY_WIDTH / 2, yStart + BLACK_KEY_HEIGHT);
      gl.glVertex2d(xStart, yStart + BLACK_KEY_HEIGHT);
      gl.glVertex2d(xStart, yStart + WHITE_KEY_HEIGHT);
      gl.glVertex2d(xStart + WHITE_KEY_WIDTH / 2, yStart + WHITE_KEY_HEIGHT);
      gl.glVertex2d(xStart + WHITE_KEY_WIDTH / 2, yStart);
      gl.glVertex2d(xStart + BLACK_KEY_WIDTH / 2, yStart);
      gl.glEnd();
    } else {
      gl.glRectd(xStart, yStart, xStart + WHITE_KEY_WIDTH / 2, yStart + WHITE_KEY_HEIGHT);
    }

    // draw second half
    if (isNextBlack(key)) {
      gl.glBegin(GL2.GL_TRIANGLE_FAN); //convex polygon

      gl.glVertex2d(xStart + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH / 2, yStart + BLACK_KEY_HEIGHT);
      gl.glVertex2d(xStart + WHITE_KEY_WIDTH, yStart + BLACK_KEY_HEIGHT);
      gl.glVertex2d(xStart + WHITE_KEY_WIDTH, yStart + WHITE_KEY_HEIGHT);
      gl.glVertex2d(xStart + WHITE_KEY_WIDTH / 2, yStart + WHITE_KEY_HEIGHT);
      gl.glVertex2d(xStart + WHITE_KEY_WIDTH / 2, yStart);
      gl.glVertex2d(xStart + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH / 2, yStart);
      gl.glEnd();
    } else {
      gl.glRectd(xStart + WHITE_KEY_WIDTH / 2, yStart, xStart + WHITE_KEY_WIDTH, yStart + WHITE_KEY_HEIGHT);
    }

    //draw separating line

    gl.glColor3d(0.0, 0.0, 0.0);
    gl.glLineWidth(2.0f);
    gl.glBegin(GL2.GL_LINES);
    gl.glVertex2d(xStart + WHITE_KEY_WIDTH, yStart + WHITE_KEY_HEIGHT);
    gl.glVertex2d(xStart + WHITE_KEY_WIDTH, yStart + (isNextBlack(key) ? BLACK_KEY_HEIGHT : 0));
    gl.glEnd();
  }

  private void drawKey(GL2 gl, int key) {
    drawKey(gl, key, isBlack(key) ? Colors.BLACK : Colors.WHITE);
  }

  private void drawClaviature(GL2 gl, Color[] colors) {
    for (int i = LOWEST_KEY; i <= HIGHEST_KEY; i++) {
      if (colors[i] == null)
        drawKey(gl, i);
      else
        drawKey(gl, i, colors[i]);
    }
  }

  @Override
  public void draw(GL2 gl, int frameCount, long tick) {
    Set<Note> colored = sequence.getNotesAt(tick);

    Color[] colors = new Color[MAX_KEYS];
    for (Note n : colored) {
      colors[n.getKey()] = Colors.getTrackColor(n.getTrackId());
    }

    drawClaviature(gl, colors);
  }

  @Override
  public void drawIdle(GL2 gl) {
    Color[] colors = new Color[MAX_KEYS];
    drawClaviature(gl, colors);
  }
}
