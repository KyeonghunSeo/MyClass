package com.hellowo.myclass.model;

import com.hellowo.myclass.App;
import com.hellowo.myclass.AppDateFormat;
import com.hellowo.myclass.R;
import com.hellowo.myclass.utils.StringUtil;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject {
    public static final String KEY_ID = "eventId";
    public static final String KEY_DT_START = "dtStart";
    public static final String KEY_DT_END = "dtEnd";
    public static final String KEY_TYPE = "type";
    public static final String KEY_STUDENT_ID = "students.studentId";
    public static final String KEY_DT_DONE = "dtDone";

    public static final int TYPE_ABSENT = 0;
    public static final int TYPE_SICK = 1;
    public static final int TYPE_EARLY = 2;
    public static final int TYPE_ANNOUNCEMENT = 3;
    public static final int TYPE_CONSULTING = 4;
    public static final int TYPE_THUMBS_UP = 5;
    public static final int TYPE_THUMBS_DOWN = 6;
    public static final int TYPE_COMMENT = 7;
    public static final int TYPE_EVENT = 8;
    public static final int TYPE_TODO = 9;

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

    public static Event creatNewEvent(int type, long time) {
        Event event = new Event();
        event.students = new RealmList<>();
        event.type = type;
        event.dtStart = time;
        event.dtEnd = time;
        event.dtDone = 0;
        event.description = "";
        event.lastUpdated = time;
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
            case TYPE_COMMENT:
                return App.baseContext.getString(R.string.comment);
            case TYPE_EVENT:
                return App.baseContext.getString(R.string.event);
            case TYPE_TODO:
                return App.baseContext.getString(R.string.todo);
            default:
                return App.baseContext.getString(R.string.app_name);
        }
    }

    public int getTypeIconId() {
        switch (type) {
            case TYPE_ABSENT:
                return R.drawable.ic_home_black_48dp;
            case TYPE_SICK:
                return R.drawable.ic_local_hotel_black_48dp;
            case TYPE_EARLY:
                return R.drawable.ic_hourglass_empty_black_48dp;
            case TYPE_ANNOUNCEMENT:
                return R.drawable.ic_pan_tool_black_48dp;
            case TYPE_CONSULTING:
                return R.drawable.ic_supervisor_account_black_48dp;
            case TYPE_THUMBS_UP:
                return R.drawable.ic_thumb_up_black_48dp;
            case TYPE_THUMBS_DOWN:
                return R.drawable.ic_thumb_down_black_48dp;
            case TYPE_COMMENT:
                return R.drawable.ic_format_quote_black_48dp;
            case TYPE_EVENT:
                return R.drawable.ic_date_range_black_48dp;
            case TYPE_TODO:
                return R.drawable.ic_assignment_turned_in_black_48dp;
            default:
                return R.drawable.ic_assignment_late_black_48dp;
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", students=" + StringUtil.ListToString(students) +
                ", type=" + type +
                ", dtStart=" + dtStart +
                ", dtEnd=" + dtEnd +
                ", dtDone=" + dtDone +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    public boolean isStudentsEvent() {
        return type < TYPE_EVENT;
    }

    public boolean isTeachersEvent() {
        return type >= TYPE_EVENT;
    }

    public boolean isTodo() {
        return type == TYPE_TODO;
    }

    public boolean isEvent() {
        return type == TYPE_EVENT;
    }

    public String getDateText() {
        if(isEvent()) {
            return AppDateFormat.smallmdeDate.format(new Date(dtStart))
                    + " ~ "
                    + AppDateFormat.smallmdeDate.format(new Date(dtEnd));
        }else {
            return AppDateFormat.smallmdeDate.format(new Date(dtStart));
        }
    }
}
