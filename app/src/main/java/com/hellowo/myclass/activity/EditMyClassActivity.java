package com.hellowo.myclass.activity;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hellowo.myclass.R;
import com.hellowo.myclass.databinding.ActivityEditClassBinding;
import com.hellowo.myclass.model.MyClass;
import com.hellowo.myclass.utils.FileUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import gun0912.tedbottompicker.TedBottomPicker;
import io.realm.Realm;

public class EditMyClassActivity extends AppCompatActivity {
    public final static String INTENT_KEY_MY_CLASS_ID = "INTENT_KEY_MY_CLASS_ID";
    private ActivityEditClassBinding binding;
    private Realm realm;
    private MyClass myClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_class);
        realm = Realm.getDefaultInstance();

        initMyClass();
        initClassYearTextButton();
        initClassImageButton();
        initSaveButton();
    }

    private void initMyClass() {
        String myClassId = getIntent().getStringExtra(INTENT_KEY_MY_CLASS_ID);
        if(TextUtils.isEmpty(myClassId)){
            myClass = new MyClass();
        }else{
            myClass = realm.where(MyClass.class)
                    .equalTo(MyClass.KEY_ID, myClassId)
                    .findFirst();
        }
    }

    private void initClassYearTextButton() {
        binding.classYearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showClassYearPicker();
            }
        });
    }

    private void showClassYearPicker() {
        final Calendar calendar = Calendar.getInstance();
        if(myClass.classYear > 0){
            calendar.set(Calendar.YEAR, myClass.classYear);
        }

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myClass.classYear = year;
                        binding.classYearTextButton.setText(String.valueOf(myClass.classYear));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dpd.showYearPickerFirst(true);
        dpd.setAccentColor(getResources().getColor(R.color.highlight));
        dpd.show(getFragmentManager(), "Datepickerdialog");
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
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(EditMyClassActivity.this)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        myClass.classImageUri = FileUtil.getPath(getBaseContext(), uri);
                        Glide.with(EditMyClassActivity.this)
                                .load(uri)
                                .into(binding.classImageButton);
                    }
                })
                .setMaxCount(100)
                .create();
        bottomSheetDialogFragment.show(getSupportFragmentManager());
    }

    private void initClassImageButton() {
        binding.classImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TedPermission(EditMyClassActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setRationaleMessage(getString(R.string.ask_storage_permission))
                        .setDeniedMessage(getString(R.string.denied_storage_permission))
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });
    }

    private void initSaveButton() {
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMyClassData();
                finish();
            }
        });
    }

    private void saveMyClassData() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if(TextUtils.isEmpty(myClass.classId)){
                    myClass.classId = UUID.randomUUID().toString();
                }
                myClass.classYear = Integer.valueOf(binding.classYearTextButton.getText().toString());
                myClass.schoolName = binding.schoolNameEditText.getText().toString().trim();
                myClass.grade = binding.gradeEditText.getText().toString().trim();
                myClass.classNumber = binding.classNumberEditText.getText().toString().trim();
                myClass.lastUpdated = System.currentTimeMillis();
                realm.copyToRealmOrUpdate(myClass);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
