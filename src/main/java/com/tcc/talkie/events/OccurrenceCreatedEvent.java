package com.tcc.talkie.events;

import com.tcc.talkie.domain.occurrence.Occurrence;

public class OccurrenceCreatedEvent {

    private final Occurrence occurrence;

    public OccurrenceCreatedEvent(Occurrence occurrence) {
        this.occurrence = occurrence;
    }

    public Occurrence getOccurrence() {
        return occurrence;
    }

}
