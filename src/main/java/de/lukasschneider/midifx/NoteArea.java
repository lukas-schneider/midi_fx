package de.lukasschneider.midifx;

import com.jogamp.opengl.GL2;

import java.util.Set;
import java.util.SortedSet;

public class NoteArea implements Drawable {

  static final double WIDTH = Renderer.WIDTH;
  static final double HEIGHT = 850.0;

  static final int INITIAL_TIME_RANGE = 4;

  static final int CORNER_RESOLUTION = 80;
  static final double CORNER_RADIUS = 8.0;

  static final double SHADOW_OFFSET_X = 5.0;
  static final double SHADOW_OFFSET_Y = 5.0;

  static final double NOTE_PADDING_X = 1.0;
  static final double NOTE_PADDING_Y = 0.0;
  private final Player player;

  private long timeRange;
  private long timePerPixel;

  public NoteArea(Player player) {
    this.player = player;
    setTimeRange(INITIAL_TIME_RANGE);
  }

  public void setTimeRange(int seconds) {
    timeRange = (long) (seconds) * (1000 * 1000 * 1000);
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
    double r = Math.min(CORNER_RADIUS, (y1 - y0) / 2);

    gl.glBegin(GL2.GL_POLYGON);
    drawCorner(gl, x1 - r, y1 - r, r, 0);
    drawCorner(gl, x0 + r, y1 - r, r, 1);
    drawCorner(gl, x0 + r, y0 + r, r, 2);
    drawCorner(gl, x1 - r, y0 + r, r, 3);
    gl.glEnd();
  }

  private void drawNote(GL2 gl, Note note, long nanoTime) {
    double xStart = Claviature.getXPosition(note.getKey()) + NOTE_PADDING_X;
    double xEnd = xStart + Claviature.getWidth(note.getKey()) - NOTE_PADDING_X;

    double yStart = getYPosition(note.getEndTime() - nanoTime) + NOTE_PADDING_Y;
    double yEnd = getYPosition(note.getStartTime() - nanoTime) - NOTE_PADDING_Y;

    Colors.set(gl, note.getColor());
    drawNoteRect(gl, xStart, yStart, xEnd, yEnd);
  }

  private void drawNoteShadow(GL2 gl, Note note, long nanoTime) {
    double xStart = Claviature.getXPosition(note.getKey()) + NOTE_PADDING_X;
    double xEnd = xStart + Claviature.getWidth(note.getKey()) - NOTE_PADDING_X;

    double yStart = getYPosition(note.getEndTime() - nanoTime) + NOTE_PADDING_Y;
    double yEnd = getYPosition(note.getStartTime() - nanoTime) - NOTE_PADDING_Y;

    Colors.set(gl, Colors.SHADOW);

    drawNoteRect(
        gl,
        xStart + SHADOW_OFFSET_X,
        yStart + SHADOW_OFFSET_Y,
        xEnd + SHADOW_OFFSET_X,
        yEnd + SHADOW_OFFSET_Y
    );
  }

  private void drawNotes(GL2 gl, long nanoTime) {
    SortedSet<Note> colored = player.getNotesIn(nanoTime, nanoTime + timeRange);
    colored.forEach((note) -> drawNoteShadow(gl, note, nanoTime));

    gl.glEnable(GL2.GL_BLEND);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    gl.glEnable(GL2.GL_POLYGON_SMOOTH);

    colored.forEach((note) -> drawNote(gl, note, nanoTime));

    gl.glDisable(GL2.GL_BLEND);
    gl.glDisable(GL2.GL_POLYGON_SMOOTH);

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
