package de.lukas_schneider.midi_fx;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;

import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;

class Main {
  static File INPUT_FILE;
  static File OUTPUT_DIR;

  static File BG_IMAGE;

  public static void main(String[] args) {
    try {
      if (args.length < 2) {
        System.out.println("MIDI FX usage:");
        System.out.println("<midi file> <output directory> [background image]");
        System.exit(1);
      }

      INPUT_FILE = new File(args[0]);
      if (!INPUT_FILE.canRead())
        throw new IOException("Failed to read file " + INPUT_FILE.getAbsolutePath());

      OUTPUT_DIR = new File(args[1]);
      if (!OUTPUT_DIR.exists())
        if (!OUTPUT_DIR.mkdirs())
          throw new IOException("Failed to create directory " + OUTPUT_DIR.getAbsolutePath());

      if (!OUTPUT_DIR.isDirectory())
        throw new IOException("Not a directory: " + OUTPUT_DIR.getAbsolutePath());

      if (args.length >= 3) {
        BG_IMAGE = new File(args[2]);
        if (!BG_IMAGE.canRead())
          throw new IOException("Failed to read file " + INPUT_FILE.getAbsolutePath());
      }

      NoteSequence sequence = new NoteSequence(MidiSystem.getSequence(INPUT_FILE));

      Player player = new Player(sequence);

      GLWindow window = initWindow();

      Renderer renderer = new Renderer(window);
      Animator animator = new Animator(window);

      EventHandler handler = new EventHandler(window, renderer);

      if (BG_IMAGE != null) renderer.add(new BackgroundImage(BG_IMAGE));

      renderer.add(new NoteArea(player));
      renderer.add(new Claviature(player));

      animator.start();
    } catch (Exception e) {
      e.printStackTrace();
    }

    //while(true){}
  }

  private static GLWindow initWindow() {
    GLProfile glp = GLProfile.get(GLProfile.GL2);

    GLCapabilities caps = new GLCapabilities(glp);

    GLWindow window = GLWindow.create(caps);
    window.setTitle("midi fx");
    window.setUndecorated(false);
    window.setSize(960, 540);
    window.setFullscreen(false);
    window.setVisible(true);

    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowDestroyed(WindowEvent windowEvent) {
        System.exit(0);
      }
    });

    return window;
  }


}
