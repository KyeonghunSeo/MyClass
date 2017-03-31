package com.hellowo.myclass.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hellowo.myclass.R;
import com.hellowo.myclass.model.Event;
import com.hellowo.myclass.model.Student;

import java.io.File;

import io.realm.Realm;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CallingService extends Service {

    public static final String EXTRA_CALL_NUMBER = "call_number";
    protected View rootView;
    protected ImageView studentImage;
    protected TextView nameText;
    protected TextView callNumberText;

    String call_number;

    WindowManager.LayoutParams params;
    private WindowManager windowManager;

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();

        int width = (int) (display.getWidth() * 0.9); //Display 사이즈의 90%

        params = new WindowManager.LayoutParams(
                width,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.TRANSLUCENT);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        rootView = layoutInflater.inflate(R.layout.window_calling_popup, null);
        studentImage = (ImageView) rootView.findViewById(R.id.studentImage);
        nameText = (TextView) rootView.findViewById(R.id.nameText);
        callNumberText = (TextView) rootView.findViewById(R.id.callNumberText);

        rootView.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePopup();
            }
        });
        setDraggable();
    }

    private void setDraggable() {
        rootView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        if (rootView != null)
                            windowManager.updateViewLayout(rootView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{ //다른 앱 위에 그릴수 있는 권한이 필요함

            if (!TextUtils.isEmpty(call_number)) { // 전화번호가 있을때

                Realm realm = Realm.getDefaultInstance();
                try {
                    Student student = realm.where(Student.class)
                            .equalTo(Student.KEY_PHONE_NUMBER, call_number)
                            .or()
                            .equalTo(Student.KEY_PARENT_PHONE_NUMBER, call_number)
                            .findFirst();

                    if(student != null) {

                        windowManager.addView(rootView, params);
                        setExtra(intent);

                        nameText.setText(student.name);

                        if(!TextUtils.isEmpty(student.profileImageUri)) { // 이미지 경로가 있으면 로드함

                            Glide.with(this)
                                    .load(new File(student.profileImageUri))
                                    .into(studentImage);

                        }

                        if(call_number.equals(student.phoneNumber)) {
                            callNumberText.setText(R.string.student_phone_number);
                        }else{
                            callNumberText.setText(R.string.parent_phone_number);
                        }

                    }

                } finally {
                    realm.close();
                }

            }

        }catch (Exception ignore){}

        return START_REDELIVER_INTENT;
    }


    private void setExtra(Intent intent) {
        if (intent == null) {
            removePopup();
            return;
        }
        call_number = intent.getStringExtra(EXTRA_CALL_NUMBER);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        removePopup();
    }

    public void removePopup() {
        if (rootView != null && windowManager != null) windowManager.removeView(rootView);
    }
}