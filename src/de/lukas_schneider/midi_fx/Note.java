package de.lukas_schneider.midi_fx;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

public class Note {
  private final long startTick;
  private final long endTick;
  private final int key;
  private final int trackId;
  private final int channelId;

  private long startTime;
  private long endTime;

  public Note(MidiEvent noteOn, MidiEvent noteOff, int trackId) {
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

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }
}
