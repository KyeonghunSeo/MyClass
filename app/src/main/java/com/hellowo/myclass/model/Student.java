package com.hellowo.myclass.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Student extends RealmObject {
    @PrimaryKey
    public String id;
    public String name;
    public String profileImageUri;
    public long birth;
    public long lastUpdated;
}
