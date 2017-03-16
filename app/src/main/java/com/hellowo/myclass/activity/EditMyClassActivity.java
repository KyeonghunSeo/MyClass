package com.hellowo.myclass.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.hellowo.myclass.R;
import com.hellowo.myclass.databinding.ActivityChooseClassBinding;
import com.hellowo.myclass.databinding.ActivityEditClassBinding;
import com.hellowo.myclass.model.MyClass;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;

public class EditMyClassActivity extends AppCompatActivity {
    private final static String INTENT_KEY_MY_CLASS_ID = "INTENT_KEY_MY_CLASS_ID";
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

    private void initSaveButton() {
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMyClassData();
            }
        });
    }

    private void saveMyClassData() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if(TextUtils.isEmpty(myClass.id)){
                    myClass.id = UUID.randomUUID().toString();
                }
                myClass.schoolName = binding.schoolNameEditText.getText().toString();
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
