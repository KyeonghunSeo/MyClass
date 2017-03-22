package com.hellowo.myclass.activity;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hellowo.myclass.R;
import com.hellowo.myclass.databinding.ActivityStudentBinding;
import com.hellowo.myclass.model.Student;
import com.hellowo.myclass.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;

import gun0912.tedbottompicker.TedBottomPicker;
import io.realm.Realm;

import static com.hellowo.myclass.AppConst.INTENT_KEY_STUDENT_ID;

public class StudentActivity extends AppCompatActivity {
    private ActivityStudentBinding binding;
    private Realm realm;
    private Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_student);
        realm = Realm.getDefaultInstance();

        initStudent();
        initClassImageButton();
    }

    private void initStudent() {
        String studentId = getIntent().getStringExtra(INTENT_KEY_STUDENT_ID);
        student = realm.where(Student.class)
                .equalTo(Student.KEY_ID, studentId)
                .findFirst();
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
        if(!TextUtils.isEmpty(student.profileImageUri)){ // 이미지 경로가 있으면 로드함
            Glide.with(StudentActivity.this)
                    .load(new File(student.profileImageUri))
                    .into(binding.studentImageButton);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
