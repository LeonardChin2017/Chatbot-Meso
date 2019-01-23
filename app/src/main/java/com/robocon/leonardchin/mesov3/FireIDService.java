package com.robocon.leonardchin.mesov3;

/**
 * Created by Owner on 3/1/2018.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by francesco on 13/09/16.
 */
public class FireIDService extends FirebaseInstanceIdService {
    public static final String TAG = "Notification";
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token: " + token);

    }
}