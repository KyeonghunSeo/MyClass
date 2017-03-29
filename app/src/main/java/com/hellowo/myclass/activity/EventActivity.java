package com.hellowo.myclass.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.hellowo.myclass.AppDateFormat;
import com.hellowo.myclass.R;
import com.hellowo.myclass.databinding.ActivityEventBinding;
import com.hellowo.myclass.dialog.SelectEventTypeDialog;
import com.hellowo.myclass.dialog.SelectStudentDialog;
import com.hellowo.myclass.model.Event;
import com.hellowo.myclass.model.Student;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        initDate();
        initType();
        initStudentChipView();
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

                Student student = realm.where(Student.class)
                        .equalTo(Student.KEY_ID, studentId)
                        .findFirst();

                Map<String, Student> studentMap = new HashMap<>();
                studentMap.put(student.studentId, student);

                event.students.add(student);

                binding.studentChipView.makeStudentChips(studentMap);

            }

        }
    }

    private void initType() {
        binding.typeText.setText(event.getTypeTitle());

        binding.typeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectEventTypeDialog dialog = new SelectEventTypeDialog(
                        EventActivity.this,
                        new SelectEventTypeDialog.SelectEventTypeInterface() {
                            @Override
                            public void onSelected(int type) {
                                event.type = type;
                                binding.typeText.setText(event.getTypeTitle());
                            }
                        });
                dialog.show();
            }
        });
    }

    private void initDate() {
        binding.dateText.setText(AppDateFormat.mdeDate.format(new Date(event.dtStart)));

        binding.dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(event.dtStart);

                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                calendar.set(year, monthOfYear, dayOfMonth);
                                event.dtStart = calendar.getTimeInMillis();
                                event.dtEnd = calendar.getTimeInMillis();
                                binding.dateText.setText(
                                        AppDateFormat.mdeDate.format(new Date(event.dtStart)));
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dpd.setAccentColor(getResources().getColor(R.color.primary));
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
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

    private void initStudentChipView() {
        setAddStudentButton();

        binding.addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectStudentDialog dialog = new SelectStudentDialog(
                        EventActivity.this,
                        classId,
                        new HashMap<String, Student>(),
                        new SelectStudentDialog.SelectStudentInterface() {
                            @Override
                            public void onSelected(Map<String, Student> selectedMap) {
                                event.students.clear();

                                for(String id : selectedMap.keySet()) {
                                    Student student = selectedMap.get(id);
                                    event.students.add(student);
                                }

                                binding.studentChipView.makeStudentChips(selectedMap);
                                setAddStudentButton();
                            }
                        });
                dialog.show();
            }
        });
    }

    private void setAddStudentButton() {
        if(binding.studentChipView.getChipCount() > 0) {
            binding.addStudentText.setVisibility(View.GONE);
        }else{
            binding.addStudentText.setVisibility(View.VISIBLE);
        }
    }

    private void saveEvent() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                event.description = binding.editText.getText().toString();
                event.lastUpdated = System.currentTimeMillis();

                realm.copyToRealmOrUpdate(event);
            }
        });
    }

    @Override
    public void onBackPressed() {
        saveEvent();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
