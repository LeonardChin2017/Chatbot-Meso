package com.robocon.leonardchin.mesov3;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by adnansahinovic on 9/26/17.
 */
public class FirebaseApp extends android.app.Application {

  @Override
  public void onCreate() {
    super.onCreate();
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
  }
}
