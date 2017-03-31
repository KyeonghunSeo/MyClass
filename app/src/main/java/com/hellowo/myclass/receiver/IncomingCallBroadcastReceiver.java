package com.hellowo.myclass.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hellowo.myclass.service.CallingService;
import com.pixplicity.easyprefs.library.Prefs;

public class IncomingCallBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "PHONE STATE";
    public static final String KEY_PHONE_LAST_STATE = "KEY_PHONE_LAST_STATE";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG,"onReceive()");

        /**
         * http://mmarvick.github.io/blog/blog/lollipop-multiple-broadcastreceiver-call-state/
         * 2번 호출되는 문제 해결
         */
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (!state.equals(Prefs.getString(KEY_PHONE_LAST_STATE, ""))) {
            Prefs.putString(KEY_PHONE_LAST_STATE, state);

            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                final String phone_number = PhoneNumberUtils.formatNumber(incomingNumber);

                Intent serviceIntent = new Intent(context, CallingService.class);
                serviceIntent.putExtra(CallingService.EXTRA_CALL_NUMBER, phone_number);
                context.startService(serviceIntent);
            }

        }
    }
}