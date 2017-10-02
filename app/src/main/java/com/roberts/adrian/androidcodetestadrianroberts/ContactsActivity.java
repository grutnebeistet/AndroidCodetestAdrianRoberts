package com.roberts.adrian.androidcodetestadrianroberts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static com.roberts.adrian.androidcodetestadrianroberts.ContactsFragment.EXTRA_CONTACT_NAME;

public class ContactsActivity extends AppCompatActivity
implements ContactsFragment.OnContactsInteractionListener{
    private static final String TAG = ContactsActivity.class.getSimpleName();
    final int MY_CONTACT_REQ = 111;
    private boolean mIsTwoPane;

    private ContactDetailsFragment mContactDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_CONTACT_REQ);
            // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, MY_CONTACT_REQ);
        }

        setContentView(R.layout.activity_main);
        mIsTwoPane = getResources().getBoolean(R.bool.has_two_panes);

        if (mIsTwoPane) {
            mContactDetailFragment = (ContactDetailsFragment)
                    getFragmentManager().findFragmentById(R.id.contact_details);
        }

    }

    @Override
    public void onContactSelected(Uri contactUri, String name) {
        if (mIsTwoPane && mContactDetailFragment != null) {
            // If two pane layout then update the detail fragment to show the selected contact
            mContactDetailFragment.setContact(contactUri, name);
        } else {
            // Otherwise single pane layout, start a new ContactDetailActivity with
            // the contact Uri
            Intent intent = new Intent(this, ContactDetailsActivity.class);
            intent.setData(contactUri);
            intent.putExtra(EXTRA_CONTACT_NAME, name);
            startActivity(intent);
        }
    }

    @Override
    public void onSelectionCleared() {
        Log.i(TAG, "onSelectionClearred");
        if (mIsTwoPane && mContactDetailFragment != null) {
            mContactDetailFragment.setContact(null, null);
        }
    }
}
