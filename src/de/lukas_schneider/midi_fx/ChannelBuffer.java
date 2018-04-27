package de.lukas_schneider.midi_fx;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import java.util.Vector;

class ChannelBuffer {
  private final int trackId;
  private MidiEvent[] buffer = new MidiEvent[128];

  private Vector<Note> notes = new Vector<>();

  ChannelBuffer(int trackId) {
    this.trackId = trackId;
  }

  void addNoteOn(MidiEvent e) {
    int key = ((ShortMessage) e.getMessage()).getData1();

    if (buffer[key] != null) return;

    buffer[key] = e;
  }

  void addNoteOff(MidiEvent e) {
    int key = ((ShortMessage) e.getMessage()).getData1();

    if (buffer[key] == null) return;

    notes.add(new Note(buffer[key], e, trackId));
    buffer[key] = null;
  }

  Note[] toArray() {
    return notes.toArray(new Note[0]);
  }
}
