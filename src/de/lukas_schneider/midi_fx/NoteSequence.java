package de.lukas_schneider.midi_fx;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.*;

class NoteSequence {
  private static final int BEATS_PER_BAR = 4;

  private TreeMap<Long, ArrayList<Note>> mapByEndTick;
  private TreeMap<Long, ArrayList<Note>> mapByStartTick;

  private float divisionType;
  private int resolution;

  private long ticksPerBeat;
  private long ticksPerBar;

  private double beatsPerMinute;
  private double beatsPerSecond;

  NoteSequence(Sequence seq, double bpm) {
    mapByEndTick = new TreeMap<>();
    mapByStartTick = new TreeMap<>();

    divisionType = seq.getDivisionType();
    resolution = seq.getResolution();

    beatsPerMinute = bpm;
    beatsPerSecond = beatsPerMinute / 60;

    if (divisionType == Sequence.PPQ) {
      ticksPerBeat = resolution;
    } else {
      ticksPerBeat = (long) (resolution * divisionType / beatsPerSecond);
    }

    ticksPerBar = ticksPerBeat * BEATS_PER_BAR;

    Track[] tracks = seq.getTracks();
    for (int i = 0; i < tracks.length; i++) {
      System.out.println("Track " + i + ": " + tracks[i].size() + " Elements");
      addTrack(tracks[i], i);
    }
  }

  NoteSequence(Sequence seq) {
    this(seq, 240.0);
  }

  private void addTrack(Track track, int trackId) {
    int command, channelId;
    MidiMessage message;

    ChannelBuffer[] buffer = new ChannelBuffer[16];

    for (int i = 0; i < track.size(); i++) {
      message = track.get(i).getMessage();

      if (!(message instanceof ShortMessage)) continue;

      command = ((ShortMessage) message).getCommand();
      channelId = ((ShortMessage) message).getChannel();

      if (buffer[channelId] == null)
        buffer[channelId] = new ChannelBuffer(trackId);

      if (command == ShortMessage.NOTE_ON)
        buffer[channelId].addNoteOn(track.get(i));

      if (command == ShortMessage.NOTE_OFF)
        buffer[channelId].addNoteOff(track.get(i));
    }

    for (ChannelBuffer b : buffer) {
      if (b == null) continue;
      for (Note note : b.toArray()) {
        addNote(note);
      }
    }
  }

  private void addNote(Note note) {
    long startKey = note.getStartTick();
    if (mapByStartTick.containsKey(startKey)) {
      mapByStartTick.get(startKey).add(note);
    } else {
      ArrayList<Note> entry = new ArrayList<>();
      entry.add(note);
      mapByStartTick.put(startKey, entry);
    }

    long endKey = note.getEndTick();
    if (mapByEndTick.containsKey(endKey)) {
      mapByEndTick.get(endKey).add(note);
    } else {
      ArrayList<Note> entry = new ArrayList<>();
      entry.add(note);
      mapByEndTick.put(endKey, entry);
    }
  }

  Set<Note> getNotesAt(long tick) {
    return getNotesIn(tick, tick);
  }

  Set<Note> getNotesIn(long tick1, long tick2) {
    Collection<ArrayList<Note>> lower = mapByStartTick.headMap(tick2, true).values();
    Collection<ArrayList<Note>> higher = mapByEndTick.tailMap(tick1, true).values();

    HashSet<Note> lowerSet = new HashSet<>();
    HashSet<Note> higherSet = new HashSet<>();
    for (ArrayList<Note> list : lower) {
      lowerSet.addAll(list);
    }

    for (ArrayList<Note> list : higher) {
      higherSet.addAll(list);
    }

    // intersection
    lowerSet.retainAll(higherSet);

    return lowerSet;
  }

  public long getTicksPerBar() {
    return ticksPerBar;
  }

  public long getTicksPerBeat() {
    return ticksPerBeat;
  }

  public double getBeatsPerMinute() {
    return beatsPerMinute;
  }

  public double getBeatsPerSecond() {
    return beatsPerSecond;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (divisionType > 0.0) {
      sb.append("Division Type: SMPTE with ")
          .append(divisionType)
          .append(" frames per second\n Resolution: ")
          .append(resolution)
          .append(" ticks per frame");
    } else {
      sb.append("Division Type: PPQ with ")
          .append(resolution)
          .append(" ticks per beat \n");
    }
    return sb.toString();
  }
}
