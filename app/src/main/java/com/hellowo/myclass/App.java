package com.hellowo.myclass;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.pixplicity.easyprefs.library.Prefs;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application{

    public static App baseContext;

    @Override
    public void onCreate() {
        super.onCreate();
        baseContext = this;
        initRealm();
        initPrefs();
        AppScreen.init(this);
        AppFont.init(this);
        AppColor.init(this);
        AppDateFormat.init(this);
    }

    /**
     * Realm 데이터베이스 초기화
     * Context.getFilesDir()에 "default.realm"란 이름으로 Realm 파일이 위치한다
     */
    private void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
    }

    private void initPrefs() {
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
