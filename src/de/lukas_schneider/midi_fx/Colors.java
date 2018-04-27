package de.lukas_schneider.midi_fx;

import com.jogamp.opengl.GL2;

import java.awt.*;

class Colors {
  static final Color BLACK = Color.BLACK;
  static final Color WHITE = Color.WHITE;
  static final Color[] TRACK_COLORS = {
      new Color(0x0000FF),
      new Color(0x0099FF),
      new Color(0x00FF00),
      new Color(0xFFFF00),
      new Color(0xFF6600),
      new Color(0xFF0000),
      new Color(0xFF00FF),
  };


  static Color getTrackColor(int trackId) {
    if (trackId > 6) return Color.GRAY;
    return TRACK_COLORS[trackId];
  }

  static void set(GL2 gl, Color color) {
    gl.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
  }
}
