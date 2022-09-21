package com.gatonimo.gatonimosrecall;

import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

class EndCallListener extends PhoneStateListener {
    private static final String LOG_TAG = null;
    private boolean isPhoneCalling = false;
    private MainActivity m;
	public EndCallListener(MainActivity mainActivity) {
		m=mainActivity;
	}
	@Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if(TelephonyManager.CALL_STATE_RINGING == state) {
            Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
        }
        if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
            //wait for phone to go offhook (probably set a boolean flag) so you know your app initiated the call.
        	isPhoneCalling = true;
        	Log.i(LOG_TAG, "OFFHOOK");
        }
        if(TelephonyManager.CALL_STATE_IDLE == state) {
            //when this state occurs, and your flag is set, restart your app
            Log.i(LOG_TAG, "IDLE");
            if (isPhoneCalling) {
            	m.guardaEstado(isPhoneCalling);
            	m.Restart();
            	//m.ReCall();
            	isPhoneCalling = false;
            	
            }
        }
    }
}