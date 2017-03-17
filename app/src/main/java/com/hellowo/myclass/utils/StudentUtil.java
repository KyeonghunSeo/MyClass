package com.hellowo.myclass.utils;

import android.app.Activity;
import android.content.DialogInterface;

import com.hellowo.myclass.R;
import com.hellowo.myclass.activity.HomeMyClassActivity;
import com.hellowo.myclass.model.MyClass;
import com.hellowo.myclass.model.Student;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import io.realm.Realm;

public class StudentUtil {

    public static void insertDataToRealmFromCsvFile(Activity activity, File file, Realm realm,
                                                    MyClass myClass){
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String[] object;
            long currentTime = System.currentTimeMillis();
            int number = 0;

            realm.beginTransaction();

            while ((object = reader.readNext()) != null) {
                Student student = new Student();
                student.id = UUID.randomUUID().toString();
                student.number = ++number;
                student.name = object[0];
                student.phoneNumber = object[1];
                student.address = object[2];
                if(object.length > 3){
                    student.parentPhoneNumber = object[3];
                }
                student.myClass = myClass;
                student.lastUpdated = currentTime;
                realm.copyToRealmOrUpdate(student);
            }

            realm.commitTransaction();
        } catch (NoClassDefFoundError | Exception e) {
            DialogUtil.showAlertDialog(
                    activity,
                    activity.getString(R.string.error_read_csv_title),
                    activity.getString(R.string.error_read_csv_message),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }, null /* 네거티브 리스너 */, 0 /* 아이콘 아이디 */
            );
        }
    }

}
