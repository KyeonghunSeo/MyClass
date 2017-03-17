package com.hellowo.myclass.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Student extends RealmObject {
    public static final String KEY_ID = "id";

    @PrimaryKey
    public String id;

    public MyClass myClass;
    public String name;
    public String profileImageUri;
    public String address;
    public String phoneNumber;
    public String parentPhoneNumber;
    public int number;
    public int position;
    public long birth;
    public long lastUpdated;

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", profileImageUri='" + profileImageUri + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", parentPhoneNumber='" + parentPhoneNumber + '\'' +
                ", number=" + number +
                ", position=" + position +
                ", birth=" + birth +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
