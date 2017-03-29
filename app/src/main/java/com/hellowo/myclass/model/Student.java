package com.hellowo.myclass.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Student extends RealmObject {
    public static final String KEY_ID = "studentId";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_MY_CLASS_ID = "myClass.classId";

    @PrimaryKey
    public String studentId;

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
                "id='" + studentId + '\'' +
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

    public String getNumberName() {
        return number + ". " + name;
    }
}
