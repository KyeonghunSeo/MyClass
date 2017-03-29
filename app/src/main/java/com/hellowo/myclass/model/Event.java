package com.hellowo.myclass.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject {
    public static final String KEY_ID = "eventId";

    public static final int TYPE_ABSENT = 0;
    public static final int TYPE_SICK = 1;
    public static final int TYPE_EARLY = 2;
    public static final int TYPE_ANNOUNCEMENT = 3;
    public static final int TYPE_CONSULTING = 4;
    public static final int TYPE_THUMBS_UP = 5;
    public static final int TYPE_THUMBS_DOWN = 6;
    public static final int TYPE_NORMAL_EVENT = 7;
    public static final int TYPE_LONG_TERM_EVENT = 8;

    @PrimaryKey
    public String eventId;

    public String title;
    public String description;
    public int type;
    public long dtStart;
    public long dtEnd;
    public long dtDone;
    public long lastUpdated;

    public static Event creatNewEvent() {
        Event event = new Event();
        event.type = TYPE_NORMAL_EVENT;
        event.dtStart = System.currentTimeMillis();
        event.dtEnd = System.currentTimeMillis();
        event.dtDone = 0;
        event.lastUpdated = System.currentTimeMillis();
        event.eventId = UUID.randomUUID().toString();
        return event;
    }
}
