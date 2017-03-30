package com.hellowo.myclass.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.hellowo.myclass.AppDateFormat;
import com.hellowo.myclass.AppScreen;
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
        initMemo();
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

            event = realm.copyFromRealm(
                    realm.where(Event.class)
                            .equalTo(Event.KEY_ID, eventId)
                            .findFirst());

        }else {

            event = Event.creatNewEvent();

            String studentId = getIntent().getStringExtra(INTENT_KEY_STUDENT_ID);

            if(studentId != null) {

                Student student = realm.where(Student.class)
                        .equalTo(Student.KEY_ID, studentId)
                        .findFirst();

                event.students.add(student);

            }

        }
    }

    private void initType() {
        setTypeData();

        binding.typeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectEventTypeDialog dialog = new SelectEventTypeDialog(
                        EventActivity.this,
                        new SelectEventTypeDialog.SelectEventTypeInterface() {
                            @Override
                            public void onSelected(int type) {
                                event.type = type;
                                setTypeData();
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

    private void setTypeData() {
        binding.typeText.setText(event.getTypeTitle());
        binding.typeImage.setImageResource(event.getTypeIconId());
    }

    private void initMemo() {
        if(!TextUtils.isEmpty(event.description)) {
            binding.editText.setText(event.description);
            binding.editText.setSelection(event.description.length());
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

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEvent();
                finish();
            }
        });

        binding.timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeText = AppDateFormat.time.format(new Date());
                binding.editText.setText(
                        binding.editText.getText().toString() + timeText
                );
                binding.editText.setSelection(binding.editText.getText().toString().length());
            }
        });
    }

    private void initStudentChipView() {
        binding.studentChipView.setMaxWidth(AppScreen.dpToPx(250));
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

                                setAddStudentButton();
                            }
                        });
                dialog.show();
            }
        });
    }

    private void setAddStudentButton() {
        if(event.students.size() > 0) {
            binding.studentChipView.makeStudentChips(event.students);
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

    private void deleteEvent() {
        final Event deleteEvent = realm.where(Event.class)
                .equalTo(Event.KEY_ID, event.eventId)
                .findFirst();

        if(deleteEvent != null) {

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    deleteEvent.deleteFromRealm();
                }
            });

        }
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
