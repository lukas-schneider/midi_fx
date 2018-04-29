package de.lukas_schneider.midi_fx;

import com.jogamp.opengl.GL2;

interface Drawable {
  void draw(GL2 gl, long nanoTime);

  void drawStatic(GL2 gl);

  default void init(GL2 gl) {
  }
}
