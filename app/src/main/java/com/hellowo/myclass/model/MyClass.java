package com.hellowo.myclass.model;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class MyClass extends RealmObject {
    public static final String KEY_ID = "id";

    @PrimaryKey
    public String id;

    public String schoolName;
    public String grade;
    public String classNumber;
    public String classImageUri;
    public int classYear;
    public long lastUpdated;

    @Override
    public String toString() {
        return "MyClass{" +
                "id='" + id + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", grade='" + grade + '\'' +
                ", classNumber='" + classNumber + '\'' +
                ", classImageUri='" + classImageUri + '\'' +
                ", classYear=" + classYear +
                ", lastUpdated=" + DateFormat.getDateTimeInstance().format(new Date(lastUpdated)) +
                '}';
    }
}
