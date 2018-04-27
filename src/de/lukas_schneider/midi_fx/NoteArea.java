package de.lukas_schneider.midi_fx;

import com.jogamp.opengl.GL2;

import java.util.Set;

class NoteArea implements Drawable {

  static final double WIDTH = Claviature.WIDTH;
  static final double HEIGHT = Claviature.TOP_OFFSET;
  static final double BLACK_WIDTH = Claviature.BLACK_KEY_WIDTH;
  static final double WHITE_WIDTH = Claviature.WHITE_KEY_WIDTH;


  static final long TICK_RANGE = 4000;

  static final double PIXEL_PER_TICK = HEIGHT / TICK_RANGE;


  static double getTickPosition(long relTick) {
    return HEIGHT - relTick * PIXEL_PER_TICK;
  }

  private NoteSequence sequence;

  NoteArea(NoteSequence sequence) {
    this.sequence = sequence;
  }

  private void drawOctaveLines(GL2 gl) {
    gl.glColor3d(0.3, 0.3, 0.3);
    gl.glLineWidth(1.0f);
    for (int i = Claviature.LOWEST_C; i <= Claviature.HIGHEST_KEY; i += Claviature.KEYS_PER_OCTAVE) {
      double x = Claviature.getPosition(i);
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex2d(x, 0);
      gl.glVertex2d(x, HEIGHT);
      gl.glEnd();
    }
  }

  private void drawBarLines(GL2 gl, long tick) {
    gl.glColor3d(0.3, 0.3, 0.3);
    gl.glLineWidth(1.0f);
    for (long t = Math.max(0, (tick - tick % sequence.getTicksPerBar())); t < tick + TICK_RANGE; t += sequence.getTicksPerBar()) {
      double y = getTickPosition(t - tick);
      if (y > HEIGHT || y < 0.0) continue;

      gl.glBegin(GL2.GL_LINES);
      gl.glVertex2d(0, y);
      gl.glVertex2d(WIDTH, y);
      gl.glEnd();
    }
  }

  private void drawNote(GL2 gl, Note note, long tick) {
    double xStart = Claviature.getPosition(note.getKey());
    double xEnd = xStart + (Claviature.isBlack(note.getKey()) ? BLACK_WIDTH : WHITE_WIDTH);

    double yStart = Math.max(0.0, getTickPosition(note.getEndTick() - tick));
    double yEnd = Math.min(HEIGHT, getTickPosition(note.getStartTick() - tick));

    Colors.set(gl, Colors.getTrackColor(note.getTrackId()));

    gl.glRectd(xStart, yStart, xEnd, yEnd);
  }

  private void drawNotes(GL2 gl, long tick) {
    Set<Note> colored = sequence.getNotesIn(tick, tick + TICK_RANGE);

    colored.forEach((note) -> drawNote(gl, note, tick));
  }

  @Override
  public void draw(GL2 gl, int frameCount, long tick) {
    drawOctaveLines(gl);
    drawBarLines(gl, tick);
    drawNotes(gl, tick);
  }

  @Override
  public void drawIdle(GL2 gl) {
    drawOctaveLines(gl);
  }
}
