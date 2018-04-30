package de.lukasschneider.midifx;

import java.util.*;

class Player {
  private final NoteSequence sequence;

  private TreeMap<Long, ArrayList<Note>> notes_startTime;
  private TreeMap<Long, ArrayList<Note>> notes_endTime;

  private TreeSet<Long> bars;

  Player(NoteSequence sequence) {
    this.sequence = sequence;

    notes_startTime = new TreeMap<>();
    notes_endTime = new TreeMap<>();
    bars = new TreeSet<>();

    for (Note note : sequence.getNotes()) {
      note.setStartTime(getNanoTime(note.getStartTick()));
      note.setEndTime(getNanoTime(note.getEndTick()));
      addNote(note);
    }

    long lastTick = notes_endTime.lastEntry().getValue().get(0).getEndTick();
    long increment = sequence.getResolution() * Tempo.BEATS_PER_BAR;
    for (long tick = 0; tick <= lastTick; tick += increment) {
      bars.add(getNanoTime(tick));
    }


  }

  private void addNote(Note note) {
    long startKey = note.getStartTime();
    if (notes_startTime.containsKey(startKey)) {
      notes_startTime.get(startKey).add(note);
    } else {
      ArrayList<Note> entry = new ArrayList<>();
      entry.add(note);
      notes_startTime.put(startKey, entry);
    }

    long endKey = note.getEndTime();
    if (notes_endTime.containsKey(endKey)) {
      notes_endTime.get(endKey).add(note);
    } else {
      ArrayList<Note> entry = new ArrayList<>();
      entry.add(note);
      notes_endTime.put(endKey, entry);
    }
  }

  public Set<Note> getNotesAt(long time) {
    return getNotesIn(time, time);
  }

  public SortedSet<Note> getNotesIn(long time1, long time2) {
    Collection<ArrayList<Note>> lower = notes_startTime.headMap(time2, true).values();
    Collection<ArrayList<Note>> higher = notes_endTime.tailMap(time1, true).values();

    SortedSet<Note> lowerSet = new TreeSet<>();
    SortedSet<Note> higherSet = new TreeSet<>();
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

  public Set<Long> getBarsIn(long time1, long time2) {
    return bars.subSet(time1, true, time2, true);
  }

  private long getNanoTime(long tick) {
    var iterator = sequence.getTempoChanges_tick().headMap(tick, true).entrySet().iterator();
    long tickDelta, time = 0;

    Map.Entry<Long, Tempo> last = iterator.next();

    while (iterator.hasNext()) {
      Map.Entry<Long, Tempo> entry = iterator.next();
      tickDelta = entry.getKey() - last.getKey();
      time += tickDelta * last.getValue().getNsPerTick();
      last = entry;
    }

    tickDelta = tick - last.getKey();
    time += tickDelta * last.getValue().getNsPerTick();

    return time;
  }

}
