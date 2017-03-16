package com.hellowo.myclass.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hellowo.myclass.R;
import com.hellowo.myclass.databinding.ActivityChooseClassBinding;

import io.realm.Realm;

public class BaseActivity extends AppCompatActivity {
    private ActivityChooseClassBinding binding;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_class);
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
