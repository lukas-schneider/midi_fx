package de.lukas_schneider.midi_fx;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

class NoteSequence {
  private ArrayList<Note> notes;
  private TreeMap<Long, Tempo> tempoChanges_tick;

  private float divisionType;
  private int resolution;

  NoteSequence(Sequence seq) throws Exception {
    divisionType = seq.getDivisionType();
    resolution = seq.getResolution();

    if (divisionType != Sequence.PPQ)
      throw new Exception("unsupported MIDI format");

    notes = new ArrayList<>();
    tempoChanges_tick = new TreeMap<>();
    tempoChanges_tick.put(0L, new Tempo(120.0, resolution));

    Track[] tracks = seq.getTracks();
    for (int i = 0; i < tracks.length; i++) {
      System.out.println("Track " + i + ": " + tracks[i].size() + " Elements");
      addTrack(tracks[i], i);
    }
  }

  private void addTrack(Track track, int trackId) {
    MidiEvent event;
    MidiMessage message;
    ShortMessage shortMsg;
    MetaMessage metaMsg;

    ChannelBuffer[] buffer = new ChannelBuffer[16];

    for (int i = 0; i < track.size(); i++) {
      event = track.get(i);
      message = event.getMessage();

      if ((message instanceof ShortMessage)) {
        shortMsg = (ShortMessage) message;

        if (buffer[shortMsg.getChannel()] == null)
          buffer[shortMsg.getChannel()] = new ChannelBuffer(trackId);

        if (shortMsg.getCommand() == ShortMessage.NOTE_ON)
          buffer[shortMsg.getChannel()].addNoteOn(event);

        if (shortMsg.getCommand() == ShortMessage.NOTE_OFF)
          buffer[shortMsg.getChannel()].addNoteOff(event);

      } else if (message instanceof MetaMessage) {
        metaMsg = (MetaMessage) message;

        if (metaMsg.getType() == 0x51)
          tempoChanges_tick.put(event.getTick(), new Tempo(metaMsg, resolution));
      }
    }

    for (ChannelBuffer b : buffer) {
      if (b == null) continue;
      notes.addAll(Arrays.asList(b.toArray()));
    }
  }

  public ArrayList<Note> getNotes() {
    return notes;
  }

  public TreeMap<Long, Tempo> getTempoChanges_tick() {
    return tempoChanges_tick;
  }

  public int getResolution() {
    return resolution;
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
