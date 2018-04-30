package de.lukasschneider.midifx;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import java.awt.*;

public class Note implements Comparable<Note> {
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

  public Color getColor() {
    if (Claviature.isBlack(key))
      return Colors.getTrackColorBlack(trackId);
    else
      return Colors.getTrackColorWhite(trackId);
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

  @Override
  public int compareTo(Note o) {
    int cmp = Long.compare(this.startTick, o.startTick);

    if (cmp != 0) return cmp;

    return Integer.compare(this.key, o.key);
  }
}
