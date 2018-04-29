package de.lukas_schneider.midi_fx;

import com.jogamp.opengl.GL2;

import java.util.Set;

public class NoteArea implements Drawable {

  static final double WIDTH = Renderer.WIDTH;
  static final double HEIGHT = 850.0;

  static final int CORNER_RESOLUTION = 40;
  static final int CORNER_RADIUS = 3;

  private final Player player;

  private long timeRange;
  private long timePerPixel;

  public NoteArea(Player player) {
    this.player = player;
    setTimeRange(2);
  }

  public void setTimeRange(int seconds) {
    timeRange = seconds * (1000 * 1000 * 1000);
    timePerPixel = (long) (timeRange / HEIGHT);
  }

  double getYPosition(long timeDelta) {
    return HEIGHT - timeDelta / timePerPixel;
  }

  private void drawOctaveLines(GL2 gl) {
    gl.glColor3d(0.3, 0.3, 0.3);
    gl.glLineWidth(1.0f);
    for (int i = Claviature.LOWEST_C; i <= Claviature.HIGHEST_KEY; i += Claviature.KEYS_PER_OCTAVE) {
      double x = Claviature.getXPosition(i);
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex2d(x, 0);
      gl.glVertex2d(x, HEIGHT);
      gl.glEnd();
    }
  }

  private void drawBarLines(GL2 gl, long nanoTime) {
    gl.glColor3d(0.3, 0.3, 0.3);
    gl.glLineWidth(1.0f);

    Set<Long> bars = player.getBarsIn(nanoTime, nanoTime + timeRange);
    double y;
    for (Long bar : bars) {
      y = getYPosition(bar - nanoTime);
      gl.glBegin(GL2.GL_LINES);
      gl.glVertex2d(0, y);
      gl.glVertex2d(WIDTH, y);
      gl.glEnd();
    }
  }

  private void drawCorner(GL2 gl, double x, double y, double r, int quadrant) {
    int start = quadrant * CORNER_RESOLUTION;
    int stop = (quadrant + 1) * CORNER_RESOLUTION;

    for (int i = start; i <= stop; i++) {
      double degInRad = (i * Math.PI) / (CORNER_RESOLUTION * 2);
      gl.glVertex2d(x + Math.cos(degInRad) * r, y + Math.sin(degInRad) * r);
    }
  }

  private void drawNoteRect(GL2 gl, double x0, double y0, double x1, double y1) {
    double r = (x1 - x0) / CORNER_RADIUS;
    gl.glBegin(GL2.GL_POLYGON);
    drawCorner(gl, x1 - r, y1 - r, r, 0);
    drawCorner(gl, x0 + r, y1 - r, r, 1);
    drawCorner(gl, x0 + r, y0 + r, r, 2);
    drawCorner(gl, x1 - r, y0 + r, r, 3);
    gl.glEnd();
  }

  private void drawNote(GL2 gl, Note note, long nanoTime) {
    double xStart = Claviature.getXPosition(note.getKey());
    double xEnd = xStart + Claviature.getWidth(note.getKey());

    double yStart = getYPosition(note.getEndTime() - nanoTime);
    double yEnd = getYPosition(note.getStartTime() - nanoTime);

    Colors.set(gl, Colors.getTrackColor(note.getTrackId()));

    drawNoteRect(gl, xStart, yStart, xEnd, yEnd);
  }

  private void drawNotes(GL2 gl, long nanoTime) {
    Set<Note> colored = player.getNotesIn(nanoTime, nanoTime + timeRange);

    colored.forEach((note) -> drawNote(gl, note, nanoTime));
  }

  @Override
  public void draw(GL2 gl, long nanoTime) {
    drawOctaveLines(gl);
    drawBarLines(gl, nanoTime);
    drawNotes(gl, nanoTime);
  }

  @Override
  public void drawStatic(GL2 gl) {
    drawOctaveLines(gl);
  }
}
