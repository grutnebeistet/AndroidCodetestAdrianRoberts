package com.roberts.adrian.androidcodetestadrianroberts;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import static com.roberts.adrian.androidcodetestadrianroberts.ContactDetailsFragment.EXTRA_CONTACT_URI;


public class ContactDetailsActivity extends AppCompatActivity {
    private static final String TAG = ContactDetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() == null) finish();

        String name = getIntent().getStringExtra(ContactsFragment.EXTRA_CONTACT_NAME);
        setTitle(name);

        final Uri uri = getIntent().getData();

        if (savedInstanceState == null) {
            setContentView(R.layout.activity_contact_details);
            ContactDetailsFragment details = new ContactDetailsFragment();
            Bundle args = new Bundle();
            args.putParcelable(EXTRA_CONTACT_URI, uri);
            details.setArguments(args);

            getFragmentManager().beginTransaction().
                    add(R.id.details_fragment_container, details).
                    commit();
        }

    }
}


