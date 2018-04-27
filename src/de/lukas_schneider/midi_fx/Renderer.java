package de.lukas_schneider.midi_fx;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import jogamp.opengl.GLAutoDrawableBase;

import java.util.ArrayList;
import java.util.List;


class Renderer implements GLEventListener {
  static final long FRAMES_PER_SECOND = 60;
  static final long INITIAL_TICK = -4000;

  private Recorder recorder;
  private final long ticksPerFrame;
  private List<Drawable> components = new ArrayList<>();

  private boolean playing = false;
  private boolean recording = false;
  private boolean idle = false;

  private int frame = 0;
  private long tick = 0;

  Renderer(GLAutoDrawableBase drawable, long ticksPerSecond) {
    this.ticksPerFrame = ticksPerSecond / FRAMES_PER_SECOND;
    drawable.addGLEventListener(this);
  }

  @Override
  public void init(GLAutoDrawable glAutoDrawable) {
    idle = true;
    frame = 0;
  }

  @Override
  public void dispose(GLAutoDrawable glAutoDrawable) {

  }

  @Override
  public void display(GLAutoDrawable glAutoDrawable) {
    final GL2 gl = glAutoDrawable.getGL().getGL2();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);

    if (idle) {
      components.forEach((component) -> component.drawIdle(gl));
    }

    if (playing) {
      components.forEach((component) -> component.draw(gl, frame, tick));
      drawPlay(gl);
      tick += ticksPerFrame;
      frame++;
    }

    if (recording) {
      components.forEach((component) -> component.draw(gl, frame, tick));
      recorder.addFrame(glAutoDrawable, frame);
      drawRec(gl);
      tick += ticksPerFrame;
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
    gl.glScaled(2.0 / 1920.0, -2.0 / 1080.0, 1);
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
    drawCircle(gl, 1920 - 60, 60, 25);
  }

  private void drawPlay(GL2 gl) {

    double x = 1920 - 60;
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

    frame = 0;
    tick = INITIAL_TICK;
  }

  void record() {
    if (!idle) return;
    idle = false;
    recording = true;
    frame = 0;
    tick = INITIAL_TICK;
    recorder = new Recorder();
    System.out.println("recording...");
  }

  void stop() {
    idle = true;
    recording = false;
    playing = false;
  }
}
