package com.hellowo.myclass.activity;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

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

import static com.hellowo.myclass.AppConst.INTENT_KEY_MY_CLASS_ID;

public class EditMyClassActivity extends AppCompatActivity {
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
            myClass = realm.copyFromRealm(realm.where(MyClass.class)
                            .equalTo(MyClass.KEY_ID, myClassId)
                            .findFirst()
            );
            binding.gradeEditText.setText(myClass.grade);
            binding.classNumberEditText.setText(myClass.classNumber);
            binding.schoolNameEditText.setText(myClass.schoolName);
        }
    }

    private void initClassYearTextButton() {
        binding.classYearTextButton.setText(String.valueOf(myClass.classYear));
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
        dpd.setAccentColor(getResources().getColor(R.color.primary));
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
                        setClassImage();
                    }
                })
                .setMaxCount(100)
                .create();
        bottomSheetDialogFragment.show(getSupportFragmentManager());
    }

    private void initClassImageButton() {
        setClassImage();

        binding.classImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TedPermission(EditMyClassActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setRationaleMessage(getString(R.string.ask_storage_permission))
                        .setDeniedMessage(getString(R.string.denied_permission))
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });
    }

    private void setClassImage() {
        if(!TextUtils.isEmpty(myClass.classImageUri)) {

            Glide.with(EditMyClassActivity.this)
                    .load(new File(myClass.classImageUri))
                    .into(binding.classImageButton);

        }else{

            Glide.with(EditMyClassActivity.this)
                    .load(R.drawable.default_class_img)
                    .into(binding.classImageButton);

        }
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

                Toast.makeText(EditMyClassActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
