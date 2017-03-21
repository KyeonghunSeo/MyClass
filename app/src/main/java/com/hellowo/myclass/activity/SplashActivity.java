package com.hellowo.myclass.activity;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hellowo.myclass.AppConst;
import com.hellowo.myclass.R;
import com.hellowo.myclass.databinding.ActivitySplashBinding;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;

import static com.hellowo.myclass.AppConst.INTENT_KEY_MY_CLASS_ID;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        checkPhonePermissions();
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            startLastMyClassActivity();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {}
    };

    private void startLastMyClassActivity() {
        String classId = Prefs.getString(AppConst.KEY_LAST_MY_CLASS_ID, null);
        if(classId != null){
            Intent intent = new Intent(SplashActivity.this, HomeMyClassActivity.class);
            intent.putExtra(INTENT_KEY_MY_CLASS_ID, classId);
            startActivity(intent);
        }else{
            Intent intent = new Intent(SplashActivity.this, ChooseMyClassActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private void checkPhonePermissions() {
        new TedPermission(SplashActivity.this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage(getString(R.string.ask_phone_permission))
                .setDeniedMessage(getString(R.string.denied_permission))
                .setPermissions(new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW
                }).check();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
