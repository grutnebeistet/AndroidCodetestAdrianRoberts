package com.roberts.adrian.androidcodetestadrianroberts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class ContactsActivity extends AppCompatActivity{
    private static final String TAG = ContactsActivity.class.getSimpleName();

    private ContactsFragment mContactsFragment;
    private boolean mInSearchMode;
    final int MY_CONTACT_REQ = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_CONTACT_REQ);
            // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, MY_CONTACT_REQ);
        }

        setContentView(R.layout.activity_main);

    }

}
