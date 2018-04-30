package de.lukas_schneider.midi_fx;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import jogamp.opengl.GLAutoDrawableBase;

import java.util.ArrayList;
import java.util.List;


class Renderer implements GLEventListener {
  private static final int FRAMES_PER_SECOND = 60;
  private static final long NANOSECONDS_PER_FRAME = (1000 * 1000 * 1000) / FRAMES_PER_SECOND;

  private static final int START_TIME_S = -5;
  private static final int START_FRAME = START_TIME_S * FRAMES_PER_SECOND;

  public static double WIDTH = 1920;
  public static double HEIGHT = 1080;

  private Recorder recorder;
  private List<Drawable> components = new ArrayList<>();

  private boolean playing = false;
  private boolean recording = false;
  private boolean idle = false;

  private int frame = 0;
  private long nanoTime = 0; // in ns

  Renderer(GLAutoDrawableBase drawable) {
    drawable.addGLEventListener(this);
  }

  @Override
  public void init(GLAutoDrawable glAutoDrawable) {
    final GL2 gl = glAutoDrawable.getGL().getGL2();
    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    components.forEach((component) -> component.init(gl));
    idle = true;
    frame = 0;
  }

  @Override
  public void dispose(GLAutoDrawable glAutoDrawable) {

  }

  @Override
  public void display(GLAutoDrawable glAutoDrawable) {
    final GL2 gl = glAutoDrawable.getGL().getGL2();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL2.GL_STENCIL_BUFFER_BIT);

    if (idle) {
      components.forEach((component) -> component.drawStatic(gl));
    }

    if (playing) {
      nanoTime = frame * NANOSECONDS_PER_FRAME;
      components.forEach((component) -> component.draw(gl, nanoTime));
      drawPlay(gl);
      nanoTime = frame * NANOSECONDS_PER_FRAME;

      frame++;
    }

    if (recording) {
      nanoTime = frame * NANOSECONDS_PER_FRAME;
      components.forEach((component) -> component.draw(gl, nanoTime));
      recorder.addFrame(glAutoDrawable, frame);
      drawRec(gl);

      frame++;
    }
  }

  @Override
  public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {

    final GL2 gl = glAutoDrawable.getGL().getGL2();

    if (height <= 0) height = 1;

    gl.glViewport(0, 0, width, height);

    // viewport (0,0) (1,1)
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
    gl.glTranslated(-1, 1, 0);
    gl.glScaled(2.0 / WIDTH, -2.0 / HEIGHT, 1);
  }

  private void drawCircle(GL2 gl, double x, double y, double radius) {
    gl.glBegin(GL2.GL_POLYGON);

    for (int i = 0; i < 360; i++) {
      double degInRad = i / 360.0 * Math.PI * 2;
      gl.glVertex2d(x + Math.cos(degInRad) * radius, y + Math.sin(degInRad) * radius);
    }

    gl.glEnd();
  }

  private void drawRec(GL2 gl) {
    gl.glColor3d(1.0, 0.0, 0.0);
    drawCircle(gl, WIDTH - 60, 60, 25);
  }

  private void drawPlay(GL2 gl) {

    double x = WIDTH - 60;
    double y = 60;
    double r = 25;

    gl.glColor3d(0.0, 1.0, 0.0);
    gl.glBegin(GL2.GL_POLYGON);

    gl.glVertex2d(x - r, y - r);
    gl.glVertex2d(x - r, y + r);
    gl.glVertex2d(x + r, y);

    gl.glEnd();
  }

  void add(Drawable item) {
    components.add(item);
  }

  void remove(Drawable item) {
    components.remove(item);
  }

  void play() {
    if (!idle) return;
    idle = false;
    playing = true;

    frame = START_FRAME;
  }

  void record() {
    if (!idle) return;
    idle = false;
    recording = true;

    frame = START_FRAME;
    recorder = new Recorder();
    System.out.println("recording...");
  }

  void stop() {
    idle = true;
    recording = false;
    playing = false;
  }
}
