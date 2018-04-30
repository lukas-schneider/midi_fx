package de.lukasschneider.midifx;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;

class EventHandler implements KeyListener {

  private final GLWindow window;
  private final Renderer renderer;

  EventHandler(GLWindow window, Renderer renderer) {
    this.window = window;
    this.renderer = renderer;
    window.addKeyListener(this);
  }

  private void onFullscreen() {
    if (window.isFullscreen())
      window.setFullscreen(false);
    else
      window.setFullscreen(true);
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {

  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {
    switch (keyEvent.getKeyChar()) {
      case 'f':
      case 'F':
        onFullscreen();
        break;
      case 'p':
      case 'P':
        renderer.play();
        break;
      case 'r':
      case 'R':
        renderer.record();
        break;
      case 's':
      case 'S':
        renderer.stop();
        break;
    }
  }
}
