package com.hellowo.myclass.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hellowo.myclass.R;
import com.hellowo.myclass.adapter.EventListAdapter;
import com.hellowo.myclass.databinding.ActivityStudentBinding;
import com.hellowo.myclass.model.Event;
import com.hellowo.myclass.model.Student;
import com.hellowo.myclass.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import gun0912.tedbottompicker.TedBottomPicker;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.hellowo.myclass.AppConst.INTENT_KEY_EVENT_TYPE;
import static com.hellowo.myclass.AppConst.INTENT_KEY_MY_CLASS_ID;
import static com.hellowo.myclass.AppConst.INTENT_KEY_STUDENT_ID;

public class StudentActivity extends AppCompatActivity {
    private ActivityStudentBinding binding;
    private Realm realm;
    private Student student;
    private RealmResults<Event> eventRealmResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_student);
        realm = Realm.getDefaultInstance();

        initLayout();
        initData();
        initTitle();
        initPhoneNumber();
        initAddress();
        initBirth();
        initClassImageButton();
        initEditButton();
        initEventRecyclerView();
        initEventCountText();
        initEvent();
    }

    private void initLayout() {}

    private void initData() {
        String studentId = getIntent().getStringExtra(INTENT_KEY_STUDENT_ID);
        student = realm.where(Student.class)
                .equalTo(Student.KEY_ID, studentId)
                .findFirst();

        eventRealmResults = realm.where(Event.class)
                .equalTo(Event.KEY_STUDENT_ID, studentId)
                .findAllSorted(Event.KEY_DT_START, Sort.DESCENDING);

        eventRealmResults.addChangeListener(new RealmChangeListener<RealmResults<Event>>() {
            @Override
            public void onChange(RealmResults<Event> element) {
                initEventCountText();
            }
        });
    }

    private void initTitle() {
        binding.topTitleText.setText(student.name);
    }

    private void initPhoneNumber() {
        binding.phoneText.setText(student.phoneNumber);

        binding.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(student.phoneNumber)) {

                    if (ActivityCompat.checkSelfPermission(StudentActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + student.phoneNumber));
                    startActivity(intent);
                }
            }
        });
    }

    private void initAddress() {
        binding.addressText.setText(student.address);

        binding.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(!TextUtils.isEmpty(student.address)) {
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + student.address);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                }catch (Exception ignore){}
            }
        });
    }

    private void initBirth() {
        binding.birthText.setText(DateFormat.getLongDateFormat(this).format(new Date(student.birth)));
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            showPhotoPicker();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {}
    };

    private void showPhotoPicker() {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(StudentActivity.this)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(final Uri uri) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                student.profileImageUri = FileUtil.getPath(getBaseContext(), uri);
                                setStudentImage();
                            }
                        });
                    }
                }).create();
        bottomSheetDialogFragment.show(getSupportFragmentManager());
    }

    private void initClassImageButton() {
        binding.studentImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TedPermission(StudentActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setRationaleMessage(getString(R.string.ask_storage_permission))
                        .setDeniedMessage(getString(R.string.denied_permission))
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });
        setStudentImage();
    }

    private void setStudentImage() {
        if(!TextUtils.isEmpty(student.profileImageUri)) { // 이미지 경로가 있으면 로드함

            Glide.with(this)
                    .load(new File(student.profileImageUri))
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(binding.studentImageButton);

        }else {

            Glide.with(this).load(R.drawable.default_profile_circle)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(binding.studentImageButton);

        }
    }

    private void initEditButton() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentActivity.this, EventActivity.class);
                intent.putExtra(INTENT_KEY_EVENT_TYPE, Event.TYPE_COMMENT);
                intent.putExtra(INTENT_KEY_STUDENT_ID, student.studentId);
                intent.putExtra(INTENT_KEY_MY_CLASS_ID, student.myClass.classId);
                startActivity(intent);
            }
        });
    }

    private void initEventRecyclerView() {

        binding.recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        getBaseContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );

        EventListAdapter eventListAdapter = new EventListAdapter(
                StudentActivity.this,
                eventRealmResults,
                student.myClass.classId);

        binding.recyclerView.setAdapter(eventListAdapter);
    }

    private void initEventCountText() {
        long absent_count = eventRealmResults
                .where()
                .equalTo(Event.KEY_TYPE, Event.TYPE_ABSENT)
                .or()
                .equalTo(Event.KEY_TYPE, Event.TYPE_SICK)
                .or()
                .equalTo(Event.KEY_TYPE, Event.TYPE_EARLY)
                .count();

        long announcement_count = eventRealmResults
                .where()
                .equalTo(Event.KEY_TYPE, Event.TYPE_ANNOUNCEMENT)
                .count();

        long consulting_count = eventRealmResults
                .where()
                .equalTo(Event.KEY_TYPE, Event.TYPE_CONSULTING)
                .count();

        long thumbs_up_count = eventRealmResults
                .where()
                .equalTo(Event.KEY_TYPE, Event.TYPE_THUMBS_UP)
                .count();

        long thumbs_down_count = eventRealmResults
                .where()
                .equalTo(Event.KEY_TYPE, Event.TYPE_THUMBS_DOWN)
                .count();

        binding.absentNumText.setText(String.valueOf(absent_count));
        binding.announcementNumText.setText(String.valueOf(announcement_count));
        binding.consultingNumText.setText(String.valueOf(consulting_count));
        binding.thumbsUpNumText.setText(String.valueOf(thumbs_up_count));
        binding.thumbsDownNumText.setText(String.valueOf(thumbs_down_count));
    }

    private void initEvent() {
        binding.topBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventRealmResults.removeAllChangeListeners();
        realm.close();
    }
}
