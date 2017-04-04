package com.hellowo.myclass.activity;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hellowo.myclass.AppColor;
import com.hellowo.myclass.AppDateFormat;
import com.hellowo.myclass.AppScreen;
import com.hellowo.myclass.R;
import com.hellowo.myclass.databinding.ActivityEventBinding;
import com.hellowo.myclass.dialog.SelectEventTypeDialog;
import com.hellowo.myclass.dialog.SelectStudentDialog;
import com.hellowo.myclass.model.Event;
import com.hellowo.myclass.model.Student;
import com.hellowo.myclass.utils.DialogUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.realm.Realm;

import static com.hellowo.myclass.AppConst.INTENT_KEY_EVENT_ID;
import static com.hellowo.myclass.AppConst.INTENT_KEY_EVENT_TYPE;
import static com.hellowo.myclass.AppConst.INTENT_KEY_MY_CLASS_ID;
import static com.hellowo.myclass.AppConst.INTENT_KEY_STUDENT_ID;

public class EventActivity extends AppCompatActivity {
    private ActivityEventBinding binding;
    private Realm realm;
    private Event originalEvent;
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
        initDone();
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

            originalEvent = realm.where(Event.class)
                    .equalTo(Event.KEY_ID, eventId)
                    .findFirst();

            event = realm.copyFromRealm(originalEvent);

        }else {

            String id = UUID.randomUUID().toString();

            int eventType = getIntent().getIntExtra(INTENT_KEY_EVENT_TYPE, 0);
            long time = System.currentTimeMillis();

            originalEvent = Event.creatNewEvent(eventType, time);
            event = Event.creatNewEvent(eventType, time);

            originalEvent.eventId = id;
            event.eventId = id;

            String studentId = getIntent().getStringExtra(INTENT_KEY_STUDENT_ID);

            if(studentId != null) {

                Student student = realm.where(Student.class)
                        .equalTo(Student.KEY_ID, studentId)
                        .findFirst();

                originalEvent.students.add(student);
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
                        },
                        event.type);
                dialog.show();
            }
        });
    }

    private void initDate() {
        setDateText();

        binding.startDateText.setOnClickListener(new View.OnClickListener() {
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
                                if(event.dtStart > event.dtEnd) {
                                    event.dtEnd = calendar.getTimeInMillis();
                                }
                                setDateText();
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dpd.setAccentColor(getResources().getColor(R.color.primary));
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        binding.endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                final Calendar minCal = Calendar.getInstance();
                minCal.setTimeInMillis(event.dtStart);
                calendar.setTimeInMillis(event.dtEnd);

                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                calendar.set(year, monthOfYear, dayOfMonth);
                                event.dtEnd = calendar.getTimeInMillis();
                                setDateText();
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dpd.setMinDate(minCal);
                dpd.setAccentColor(getResources().getColor(R.color.primary));
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    private void setDateText() {
        binding.startDateText.setText(AppDateFormat.mdeDate.format(new Date(event.dtStart)));
        binding.endDateText.setText(AppDateFormat.mdeDate.format(new Date(event.dtEnd)));
    }

    private void setTypeData() {
        binding.typeText.setText(event.getTypeTitle());
        binding.typeImage.setImageResource(event.getTypeIconId());

        if(event.isTodo()) {
            binding.doneLayout.setVisibility(View.VISIBLE);
            event.dtEnd = event.dtStart;
        }else{
            binding.doneLayout.setVisibility(View.GONE);
        }

        if(!event.isEvent()) {
            binding.dateDivideText.setVisibility(View.GONE);
            binding.endDateText.setVisibility(View.GONE);
        }
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
                onBackPressed();
            }
        });

        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.description = binding.editText.getText().toString();
                saveEvent();
                finish();
            }
        });

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askDeleteEvent();
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
        if(event.isTeachersEvent()) {

            binding.addStudentButton.setVisibility(View.GONE);

        }else {

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
    }

    private void setAddStudentButton() {
        if(event.students.size() > 0) {
            binding.studentChipView.makeStudentChips(event.students);
            binding.addStudentText.setVisibility(View.GONE);
        }else{
            binding.addStudentText.setVisibility(View.VISIBLE);
        }
    }

    private void initDone() {
        if(event.dtDone > 0) {

            binding.editText.setPaintFlags(
                    binding.editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.editText.setTextColor(AppColor.disableText);
            binding.doneCheck.setChecked(event.dtDone > 0);

        }

        binding.doneCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                if(check) {
                    event.dtDone = System.currentTimeMillis();
                }else {
                    event.dtDone = 0;
                }
                setDoneText();
            }
        });
    }

    private void setDoneText() {
        if(event.dtDone > 0) {
            binding.editText.setPaintFlags(
                    binding.editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.editText.setTextColor(AppColor.disableText);
        }else {
            binding.editText.setPaintFlags(
                    binding.editText.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
            binding.editText.setTextColor(AppColor.primaryText);
        }
    }

    private void saveEvent() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                event.lastUpdated = System.currentTimeMillis();
                realm.copyToRealmOrUpdate(event);

                Toast.makeText(EventActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void askDeleteEvent() {

        DialogUtil.showAlertDialog(this,
                getString(R.string.ask_delete),
                getString(R.string.ask_delete_sub),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteEvent();
                        finish();
                    }
                },
                null,
                R.drawable.ic_delete_forever_black_48dp);

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
                    Toast.makeText(EventActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public void onBackPressed() {

        event.description = binding.editText.getText().toString();

        if(!originalEvent.toString().equals(event.toString())) {

            DialogUtil.showAlertDialog(this,
                    getString(R.string.changed_data),
                    getString(R.string.changed_data_sub),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saveEvent();
                            finish();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    },
                    R.drawable.ic_assignment_late_black_48dp);

            return;

        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
