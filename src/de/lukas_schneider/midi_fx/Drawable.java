package de.lukas_schneider.midi_fx;

import com.jogamp.opengl.GL2;

interface Drawable {
  void draw(GL2 gl, int frameCount, long tick);

  void drawIdle(GL2 gl);
}
