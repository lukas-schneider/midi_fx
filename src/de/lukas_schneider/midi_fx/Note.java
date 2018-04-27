package de.lukas_schneider.midi_fx;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

class Note {
  private final long startTick;
  private final long endTick;
  private final int key;
  private final int trackId;
  private final int channelId;

  Note(MidiEvent noteOn, MidiEvent noteOff, int trackId) {
    this.trackId = trackId;
    this.channelId = ((ShortMessage) noteOn.getMessage()).getChannel();
    this.key = ((ShortMessage) noteOn.getMessage()).getData1();
    this.startTick = noteOn.getTick();
    this.endTick = noteOff.getTick();
  }

  public long getStartTick() {
    return startTick;
  }

  public long getEndTick() {
    return endTick;
  }

  public int getKey() {
    return key;
  }

  public int getTrackId() {
    return trackId;
  }

  public int getChannelId() {
    return channelId;
  }
}
