package de.lukasschneider.midifx;

import javax.sound.midi.MetaMessage;

public class Tempo {
  static final int BEATS_PER_BAR = 4;

  private final long nsPerBeat;
  private final long nsPerBar;

  private final long nsPerTick;
  private final double ticksPerS;
  private final double beatsPerS;
  private final double beatsPerM;

  private static long parseMessage(MetaMessage msg) {
    int index = msg.getData().length;
    long tempo = 0; // microseconds per (quarter) beat
    for (byte b : msg.getData()) {
      index--;
      tempo += ((int) b & 0xFF) << (index * 8);
    }
    return tempo;
  }

  Tempo(MetaMessage msg, int resolution) {
    this(parseMessage(msg), resolution);
  }

  Tempo(double bpm, int resolution) {
    this((long) ((1000.0 * 1000.0 * 60.0) / bpm), resolution);
  }

  private Tempo(long tempo, int resolution) {
    nsPerBeat = tempo * 1000;
    nsPerBar = nsPerBeat * BEATS_PER_BAR;
    nsPerTick = nsPerBeat / resolution;
    ticksPerS = (1000.0 * 1000.0 * 1000.0) / nsPerTick;
    beatsPerS = (1000.0 * 1000.0) / tempo;
    beatsPerM = beatsPerS * 60.0;
  }

  public long getNsPerBeat() {
    return nsPerBeat;
  }

  public long getNsPerBar() {
    return nsPerBar;
  }

  public long getNsPerTick() {
    return nsPerTick;
  }

  public double getTicksPerS() {
    return ticksPerS;
  }

  public double getBeatsPerS() {
    return beatsPerS;
  }

  public double getBeatsPerM() {
    return beatsPerM;
  }
}
