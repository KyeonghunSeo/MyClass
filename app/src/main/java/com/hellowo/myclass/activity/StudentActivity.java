package com.hellowo.myclass.activity;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hellowo.myclass.AppFont;
import com.hellowo.myclass.R;
import com.hellowo.myclass.databinding.ActivityStudentBinding;
import com.hellowo.myclass.model.Student;
import com.hellowo.myclass.utils.FileUtil;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import gun0912.tedbottompicker.TedBottomPicker;
import io.realm.Realm;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

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

        initLayout();
        initStudent();
        initTitle();
        initPhoneNumber();
        initAddress();
        initBirth();
        initClassImageButton();
    }

    private void initLayout() {
        BarChart mBarChart = binding.barchart;

        mBarChart.addBar(new BarModel(2.3f, 0xFF123456));
        mBarChart.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart.addBar(new BarModel(3.3f, 0xFF563456));
        mBarChart.addBar(new BarModel(1.1f, 0xFF873F56));
        mBarChart.addBar(new BarModel(2.7f, 0xFF56B7F1));
        mBarChart.addBar(new BarModel(2.f,  0xFF343456));
        mBarChart.addBar(new BarModel(0.4f, 0xFF1FF4AC));
        mBarChart.addBar(new BarModel(4.f,  0xFF1BA4E6));

        mBarChart.startAnimation();
    }

    private void initStudent() {
        String studentId = getIntent().getStringExtra(INTENT_KEY_STUDENT_ID);
        student = realm.where(Student.class)
                .equalTo(Student.KEY_ID, studentId)
                .findFirst();
    }

    private void initTitle() {
        binding.topTitleText.setTypeface(AppFont.mainConceptBold);
        binding.topTitleText.setText(student.name);
    }

    private void initPhoneNumber() {
        binding.phoneText.setTypeface(AppFont.mainConceptBold);
        binding.phoneText.setText(student.phoneNumber);
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

        setStudentImage();
    }

    private void setStudentImage() {
        if(!TextUtils.isEmpty(student.profileImageUri)){ // 이미지 경로가 있으면 로드함
            Glide.with(this)
                    .load(new File(student.profileImageUri))
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(binding.studentImageButton);

            Glide.with(this).load(new File(student.profileImageUri))
                    .bitmapTransform(new BlurTransformation(this))
                    .into(binding.classImage);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
