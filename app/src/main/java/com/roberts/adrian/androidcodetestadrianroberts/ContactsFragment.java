package com.roberts.adrian.androidcodetestadrianroberts;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roberts.adrian.androidcodetestadrianroberts.adapters.ContactAdapter;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.View.GONE;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactDetailsFragment.INDEX_PHONE_CONTACT_ID;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactDetailsFragment.INDEX_PHONE_NUMBER;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactDetailsFragment.PONE_NUMBER_PROJECTION;


public class ContactsFragment extends Fragment
        implements ContactAdapter.ContactAdapterOnClickHandler {
    final String TAG = ContactsFragment.class.getSimpleName();
    public final static String EXTRA_CONTACT_NAME = "com.roberts.adrian.androidcodetestadrianroberts.EXTRA_CONTACT_NAME";
    private static final String[] CONTACT_AND_PHONE_PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,

    };
    private final String[] CONTACT_EMAIL_PROJECTION = {
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.HAS_PHONE_NUMBER
    };

    // Column indexes
    public static final int INDEX_CONTACT_ID = 0;
    public static final int INDEX_CONTACT_NAME = 1;
    public static final int INDEX_CONTACT_THUMBNAIL = 2;

    public static final int INDEX_EMAIL_HAS_PHONE = 3;
    public static final int INDEX_EMAIL_DISPLAY_NAME = 1;
    public static final int INDEX_EMAIL_ADDRESS = 2;
    public static final int INDEX_EMAIL_CONTACT_ID = 0;

    @BindView(R.id.contacts_recycler_view)
    RecyclerView mContactsRecyclerView;
    @BindView(R.id.no_contacts_view)
    TextView mEmptyList;
    ContactAdapter mContactAdapter;
    private Unbinder unbinder;
    private boolean mIsTwoPaneLayout;
    private OnContactsInteractionListener mOnContactSelectedListener;
    private static final String ARG_COLUMN_COUNT = "column-count";

    private int lastPosition;

    public ContactsFragment() {
    }


    /**
     * This interface must be implemented by any activity that loads this fragment. When an
     * interaction occurs, such as touching an item from the ListView, these callbacks will
     * be invoked to communicate the event back to the activity.
     */
    public interface OnContactsInteractionListener {
        /**
         * Called when a contact is selected from the ListView.
         *
         * @param contactUri The contact Uri.
         */
        public void onContactSelected(Uri contactUri, String name);

        /**
         * Called when the ListView selection is cleared like when
         * a contact search is taking place or is finishing.
         */
        public void onSelectionCleared();
    }

    @SuppressWarnings("unused")
    public static ContactsFragment newInstance(int columnCount) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED))
            getContacts();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mIsTwoPaneLayout = getResources().getBoolean(R.bool.has_two_panes);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContactAdapter = new ContactAdapter(getActivity(), this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mContactsRecyclerView.setLayoutManager(layoutManager);
        mContactsRecyclerView.setAdapter(mContactAdapter);
        return view;


    }


    @Override
    public void onClick(Uri contactUri, String contactName) {
        mOnContactSelectedListener.onContactSelected(contactUri, contactName);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnContactSelectedListener = (OnContactsInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnContactsInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getContacts() {
        mContactAdapter.swapCursors(null, null, null);
        mEmptyList.setVisibility(View.VISIBLE);
        Uri emailsUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        Uri numbersUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;


        // Only contacts with at least one number and one email
        String selection_with_phone = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?";// AND " +
        String[] selectionArgs_with_phone = new String[]{"1"};

        Cursor contactsWithPhonesCursor = getActivity().getContentResolver().query(
                contactsUri,
                CONTACT_AND_PHONE_PROJECTION,
                selection_with_phone,
                selectionArgs_with_phone,
                "upper(" + ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + ") ASC");

        // emails
        String selectionEmails = ContactsContract.CommonDataKinds.Email.ADDRESS + " IS NOT NULL";
        Cursor contactsWithEmailsCursor = getActivity().getContentResolver().query(
                emailsUri,
                CONTACT_EMAIL_PROJECTION,
                selectionEmails,
                null,
                "upper(" + ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY + ") ASC");
        // numbers
        String selectionNumbers = ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        Cursor phoneNumbersCursor = getActivity().getContentResolver().query(
                numbersUri,
                PONE_NUMBER_PROJECTION,
                selectionNumbers,
                null,
                "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + ") ASC");

        // Join
        CursorJoiner joiner = new CursorJoiner(
                contactsWithPhonesCursor,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, ContactsContract.Contacts._ID},
                contactsWithEmailsCursor,
                new String[]{ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY, ContactsContract.CommonDataKinds.Email.CONTACT_ID});

        MatrixCursor cursor = new MatrixCursor(new String[]
                {ContactsContract.Contacts._ID,
                        ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY,
                        ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
                });
        for (CursorJoiner.Result joinerRes : joiner) {
            switch (joinerRes) {
                case BOTH:
                    String contactId = contactsWithEmailsCursor.getString(INDEX_EMAIL_CONTACT_ID);//contactsWithPhonesCursor.getString(INDEX_CONTACT_ID);
                    String contactName = contactsWithEmailsCursor.getString(INDEX_EMAIL_DISPLAY_NAME);//contactsWithPhonesCursor.getString(INDEX_CONTACT_NAME);
                    String thumbailUrl = contactsWithPhonesCursor.getString(INDEX_CONTACT_THUMBNAIL);
/*                    String email = contactsWithEmailsCursor.getString(INDEX_EMAIL_ADDRESS);
                    String number = phoneNumbersCursor.getString(ContactDetailsFragment.INDEX_PHONE_NUMBER);*/
                    cursor.addRow(new String[]{contactId, contactName, thumbailUrl});//, email, number});
                    break;
            }
        }
        HashMap<String, String> contactEmails = new HashMap<>();
        contactsWithEmailsCursor.moveToFirst();
        while (contactsWithEmailsCursor.moveToNext()) {
            contactEmails.put(contactsWithEmailsCursor.getString(INDEX_EMAIL_CONTACT_ID),
                    contactsWithEmailsCursor.getString(INDEX_EMAIL_ADDRESS));
        }
        HashMap<String, String> contactNumbers = new HashMap<>();
        phoneNumbersCursor.moveToFirst();
        while (phoneNumbersCursor.moveToNext()) {
            contactNumbers.put(phoneNumbersCursor.getString(INDEX_PHONE_CONTACT_ID),
                    phoneNumbersCursor.getString(INDEX_PHONE_NUMBER));
        }

        if (contactsWithEmailsCursor != null) contactsWithEmailsCursor.close();
        if (contactsWithPhonesCursor != null) contactsWithPhonesCursor.close();
        if (cursor != null && cursor.getCount() > 0)
            mEmptyList.setVisibility(GONE);
        Log.i("contactEmails: ", "" + contactEmails.size());
        mContactAdapter.swapCursors(cursor, contactEmails, contactNumbers);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contacts_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (mIsTwoPaneLayout) {
            inflater.inflate(R.menu.contact_detail_menu, menu);
            MenuItem editContact = menu.findItem(R.id.menu_edit_contact);
        }


        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();

        search(searchView);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mContactAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_contact:
                Intent intent = new Intent(getActivity(), ContactEditorActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
