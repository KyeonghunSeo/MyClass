package com.hellowo.myclass.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hellowo.myclass.R;
import com.hellowo.myclass.databinding.ActivityChooseClassBinding;
import com.hellowo.myclass.databinding.ActivityEventBinding;
import com.hellowo.myclass.dialog.ChooseStudentDialog;
import com.hellowo.myclass.model.Event;
import com.hellowo.myclass.model.Student;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

import static com.hellowo.myclass.AppConst.INTENT_KEY_EVENT_ID;
import static com.hellowo.myclass.AppConst.INTENT_KEY_MY_CLASS_ID;
import static com.hellowo.myclass.AppConst.INTENT_KEY_STUDENT_ID;

public class EventActivity extends AppCompatActivity {
    private ActivityEventBinding binding;
    private Realm realm;
    private Event event;
    private String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event);
        realm = Realm.getDefaultInstance();

        initData();
        initEvent();
        initChooseStudentView();
    }

    private void initData() {
        classId = getIntent().getStringExtra(INTENT_KEY_MY_CLASS_ID);

        if(classId == null) {
            finish();
            return;
        }

        String eventId = getIntent().getStringExtra(INTENT_KEY_EVENT_ID);

        if(eventId != null) {

            event = realm.where(Event.class)
                    .equalTo(Event.KEY_ID, eventId)
                    .findFirst();

        }else {

            event = Event.creatNewEvent();

            String studentId = getIntent().getStringExtra(INTENT_KEY_STUDENT_ID);

            if(studentId != null) {

            }

        }
    }

    private void initEvent() {
        binding.topBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEvent();
                finish();
            }
        });
    }

    private void initChooseStudentView() {
        binding.chooseStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseStudentDialog dialog = new ChooseStudentDialog(
                        EventActivity.this,
                        classId,
                        new HashMap<String, Student>(),
                        new ChooseStudentDialog.ChooseStudentInterface() {
                            @Override
                            public void onSelected(Map<String, Student> selectedMap) {
                                for(String id : selectedMap.keySet()){
                                    Log.i("aaa", selectedMap.get(id).name);
                                }
                            }
                        });
                dialog.show();
            }
        });
    }

    private void saveEvent() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
