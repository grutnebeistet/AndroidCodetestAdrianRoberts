package com.roberts.adrian.androidcodetestadrianroberts;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


public class ContactDetailsActivity extends AppCompatActivity {
    private static final String TAG = ContactDetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() == null) finish();

        String name = getIntent().getStringExtra(ContactsFragment.EXTRA_CONTACT_NAME);
        setTitle(name);

        if (getIntent() != null) {
            final Uri uri = getIntent().getData();
            // Checks to see if fragment has already been added, otherwise adds a new
            // ContactDetailFragment with the Uri provided in the intent
            if (getFragmentManager().findFragmentByTag(TAG) == null) {
                final android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();

                // Adds a newly created ContactDetailFragment that is instantiated with the
                // data Uri
                ft.add(android.R.id.content, ContactDetailsFragment.newInstance(uri, name), TAG);
                ft.commit();
            }
        } else {
            // No intent provided, nothing to do so finish()
            finish();

        }

    }
}


