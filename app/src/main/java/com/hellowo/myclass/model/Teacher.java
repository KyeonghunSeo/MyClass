package com.hellowo.myclass.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Teacher extends RealmObject {

    @PrimaryKey
    public String teacherId;

    public RealmList<Event> events;
    public String name;
    public String profileImageUri;
    public long birth;
    public long lastUpdated;
}
