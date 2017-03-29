package com.hellowo.myclass.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hellowo.myclass.AppColor;
import com.hellowo.myclass.AppFont;
import com.hellowo.myclass.R;
import com.hellowo.myclass.databinding.ActivityStudentBinding;
import com.hellowo.myclass.model.Event;
import com.hellowo.myclass.model.MyClass;
import com.hellowo.myclass.model.Student;
import com.hellowo.myclass.utils.ActivityUtil;
import com.hellowo.myclass.utils.FileUtil;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import gun0912.tedbottompicker.TedBottomPicker;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

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
        ActivityUtil.setStatusBarOverlay(this);
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
        setStudentImage();
    }

    private void initLayout() {
    }

    private void initData() {
        String studentId = getIntent().getStringExtra(INTENT_KEY_STUDENT_ID);
        student = realm.where(Student.class)
                .equalTo(Student.KEY_ID, studentId)
                .findFirst();

        eventRealmResults = realm.where(Event.class)
                .equalTo(Event.KEY_STUDENT_ID, studentId)
                .findAll();

        Log.i("aaa", eventRealmResults.size()+"");
    }

    private void initTitle() {
        binding.topTitleText.setTypeface(AppFont.mainConceptBold);
        binding.topTitleText.setText(student.name);
    }

    private void initPhoneNumber() {
        binding.phoneText.setTypeface(AppFont.mainConceptBold);
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
        binding.addressText.setTypeface(AppFont.mainConceptBold);
        binding.addressText.setText(student.address);
    }

    private void initBirth() {
        binding.birthText.setTypeface(AppFont.mainConceptBold);
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
                intent.putExtra(INTENT_KEY_STUDENT_ID, student.studentId);
                intent.putExtra(INTENT_KEY_MY_CLASS_ID, student.myClass.classId);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
