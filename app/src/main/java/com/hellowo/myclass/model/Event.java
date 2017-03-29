package com.hellowo.myclass.model;

import com.hellowo.myclass.App;
import com.hellowo.myclass.R;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject {
    public static final String KEY_ID = "eventId";
    public static final String KEY_STUDENT_ID = "students.studentId";

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
    public RealmList<Student> students;
    public int type;
    public long dtStart;
    public long dtEnd;
    public long dtDone;
    public long lastUpdated;

    public static Event creatNewEvent() {
        Event event = new Event();
        event.students = new RealmList<>();
        event.type = TYPE_NORMAL_EVENT;
        event.dtStart = System.currentTimeMillis();
        event.dtEnd = System.currentTimeMillis();
        event.dtDone = 0;
        event.lastUpdated = System.currentTimeMillis();
        event.eventId = UUID.randomUUID().toString();
        return event;
    }

    public String getTypeTitle() {
        switch (type) {
            case TYPE_ABSENT:
                return App.baseContext.getString(R.string.absent);
            case TYPE_SICK:
                return App.baseContext.getString(R.string.absent_sick);
            case TYPE_EARLY:
                return App.baseContext.getString(R.string.absent_early);
            case TYPE_ANNOUNCEMENT:
                return App.baseContext.getString(R.string.announcement);
            case TYPE_CONSULTING:
                return App.baseContext.getString(R.string.consulting);
            case TYPE_THUMBS_UP:
                return App.baseContext.getString(R.string.thumbs_up);
            case TYPE_THUMBS_DOWN:
                return App.baseContext.getString(R.string.thumbs_down);
            case TYPE_NORMAL_EVENT:
                return App.baseContext.getString(R.string.event);
            default:
                return App.baseContext.getString(R.string.app_name);
        }
    }
}
