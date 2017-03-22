package com.hellowo.myclass.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject {
    public static final int TYPE_ATTENDANCE = 0;
    public static final int TYPE_ANNOUNCEMENT = 1;
    public static final int TYPE_CONSULTING = 2;
    public static final int TYPE_THUMBS_UP = 3;
    public static final int TYPE_THUMBS_DOWN = 4;

    public static final int ATTENDANCE_NORMAL = 0;
    public static final int ATTENDANCE_SICK = 1;
    public static final int ATTENDANCE_EARLY = 2;

    @PrimaryKey
    public String eventId;

    public String description;
    public int type;
    public int attendaceType;
    public int color;
    public long dtStart;
    public long dtEnd;
    public long dtDone;
    public long lastUpdated;
}
