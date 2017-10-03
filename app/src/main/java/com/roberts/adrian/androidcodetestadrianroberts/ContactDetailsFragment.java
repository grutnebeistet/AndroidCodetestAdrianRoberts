package com.roberts.adrian.androidcodetestadrianroberts;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roberts.adrian.androidcodetestadrianroberts.adapters.AddressAdapter;
import com.roberts.adrian.androidcodetestadrianroberts.adapters.EmailAdapter;
import com.roberts.adrian.androidcodetestadrianroberts.adapters.NumberAdapter;
import com.roberts.adrian.androidcodetestadrianroberts.models.ContactAddress;
import com.roberts.adrian.androidcodetestadrianroberts.models.ContactEmail;
import com.roberts.adrian.androidcodetestadrianroberts.models.ContactPhone;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactsFragment.EXTRA_CONTACT_NAME;

public class ContactDetailsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ContactDetailsFragment.class.getSimpleName();
    public static final String EXTRA_CONTACT_URI =
            "com.roberts.adrian.androidcodetestadrianrobert.EXTRA_CONTACT_URI";
    private static Uri mContactUri;

    public static final String[] PONE_NUMBER_PROJECTION = {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone._ID,

    };
    public static final String[] EVENT_PROJECTION = {
            ContactsContract.CommonDataKinds.Event.START_DATE,
            ContactsContract.CommonDataKinds.Event.TYPE,
            ContactsContract.CommonDataKinds.Event.MIMETYPE,
    };
    public static final String[] EMAIL_PROJECTION = {
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.TYPE,
            ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Email._ID,
            ContactsContract.CommonDataKinds.Email.PHOTO_URI,

    };
    public static final String[] ADDRESS_PROJECTION = {
            ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID,
            ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
            ContactsContract.CommonDataKinds.StructuredPostal._ID

    };
    public static final int INDEX_EMAIL_CONTACT_ID = 0;
    public static final int INDEX_EMAIL_ADDRESS = 1;
    public static final int INDEX_EMAIL_TYPE = 2;
    public static final int INDEX_EMAIL_DISPLAY_NAME = 3;
    public static final int INDEX_EMAIL_ID = 4;
    public static final int INDEX_EMAIL_PHOTO_URI = 5;

    public static final int INDEX_EVENT_START_DATE = 0;
    public static final int INDEX_EVENT_TYPE = 1;
    public static final int INDEX_EVENT_MIMETYPE = 2;

    public static final int INDEX_ADDRESS_CONTACT_ID = 0;
    public static final int INDEX_ADDRESS_FORMATTED = 1;
    public static final int INDEX_ADDRESS_TYPE = 2;
    public static final int INDEX_ADDRESS_ID = 3;

    public static final int INDEX_PHONE_CONTACT_ID = 0;
    public static final int INDEX_PHONE_NUMBER = 1;
    public static final int INDEX_PHONE_TYPE = 2;
    public static final int INDEX_PHONE_DISPLAY_NAME = 3;
    public static final int INDEX_PHONE_ID = 4;


    public final static int PHONE_NUMBERS_LOADER_ID = 1347;
    public final static int EMAILS_LOADER_ID = 1348;
    public final static int ADDRESSES_LOADER_ID = 1349;
    public final static int EVENTS_LOADER_ID = 1350;

    public final static String EXTRA_NUMBERS_LIST = "com.roberts.adrian.androidcodetestadrianroberts.EXTRA_NUMBERS_LIST";
    public final static String EXTRA_ADDRESSES_LIST = "com.roberts.adrian.androidcodetestadrianroberts.EXTRA_ADDRESSES_LIST";
    public final static String EXTRA_EMAILS_LIST = "com.roberts.adrian.androidcodetestadrianroberts.EXTRA_EMAILS_LIST";

    // Used to store references to key views, layouts and menu items as these need to be updated
    // in multiple methods throughout this class.
    @BindView(R.id.contact_image)
    ImageView mImageView;
    //    @BindView(R.id.contact_details_layout)
//    LinearLayout mDetailsLayout;
    @BindView(R.id.empty_details)
    TextView mEmptyView;
    @BindView(R.id.details_ll)
    LinearLayout
            mDetailsLinearLayout;
    @BindView(R.id.numbers_recycler_view)
    RecyclerView mNumbersRecyclerView;
    @BindView(R.id.addresses_recycler_view)
    RecyclerView mAddressesRecyclerView;
    @BindView(R.id.emails_recycler_view)
    RecyclerView mEmailsRecyclerView;
    @BindView(R.id.contact_bday)
    TextView mContactBday;
    @BindView(R.id.contact_bday_label)
    TextView mContactBdayLabel;
    private AddressAdapter mAddressAdapter;
    private EmailAdapter mEmailAdapter;
    private NumberAdapter mNumberAdapter;
    private MenuItem mEditContactMenuItem;
    private Unbinder unbinder;

    private ArrayList<ContactPhone> mPhoneNumbers;
    private ArrayList<ContactEmail> mEmails;
    private ArrayList<ContactAddress> mAddresses;
    private String mBdate;
    private String mName;
    private String mPhotoUri;

    private boolean mIsTwoPaneLayout;

    public static ContactDetailsFragment newInstance(Uri contactUri, String name) {
        ContactDetailsFragment fragment = new ContactDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_CONTACT_URI, contactUri);
        args.putString(EXTRA_CONTACT_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    public ContactDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
        // Check if this fragment is part of a two pane set up or a single pane
        mIsTwoPaneLayout = getResources().getBoolean(R.bool.has_two_panes);

        // Let this fragment contribute menu items
        setHasOptionsMenu(true);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_contact_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        mContactUri = getActivity().getIntent().getData();
        mName = getActivity().getIntent().getStringExtra(EXTRA_CONTACT_NAME);

        mNumbersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNumberAdapter = new NumberAdapter(getActivity());
        mNumbersRecyclerView.setAdapter(mNumberAdapter);

        mAddressesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAddressAdapter = new AddressAdapter(getActivity());
        mAddressesRecyclerView.setAdapter(mAddressAdapter);

        mEmailsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEmailAdapter = new EmailAdapter(getActivity());
        mEmailsRecyclerView.setAdapter(mEmailAdapter);

       /* if(mContactUri != null) {
            getLoaderManager().initLoader(PHONE_NUMBERS_LOADER_ID, null, this);
            getLoaderManager().initLoader(EMAILS_LOADER_ID, null, this);
            getLoaderManager().initLoader(ADDRESSES_LOADER_ID, null, this);
            getLoaderManager().initLoader(EVENTS_LOADER_ID, null, this);
        }*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPhoneNumbers = new ArrayList<>();
        mEmails = new ArrayList<>();
        mAddresses = new ArrayList<>();
        Log.i(TAG, "onResume");
        Log.i(TAG, "uri : " + mContactUri + ", mname: " + mName);
        if (mContactUri == null) {
            mDetailsLinearLayout.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(VISIBLE);

        } else {
            getLoaderManager().restartLoader(PHONE_NUMBERS_LOADER_ID, null, this);
            getLoaderManager().restartLoader(EMAILS_LOADER_ID, null, this);
            getLoaderManager().restartLoader(ADDRESSES_LOADER_ID, null, this);
            getLoaderManager().restartLoader(EVENTS_LOADER_ID, null, this);
        }


     /*   getLoaderManager().restartLoader(PHONE_NUMBERS_LOADER_ID, null, this);
        getLoaderManager().restartLoader(EMAILS_LOADER_ID, null, this);
        getLoaderManager().restartLoader(ADDRESSES_LOADER_ID, null, this);
        getLoaderManager().restartLoader(EVENTS_LOADER_ID, null, this);*/
    }

    public void setContact(Uri uri, String name) {
        Log.i(TAG, "onSetContact");
        if (uri != null) {
            mContactUri = uri;
            mName = name;


            // Shows the contact photo ImageView and hides the empty view
            mDetailsLinearLayout.setVisibility(VISIBLE);
            mImageView.setVisibility(VISIBLE);
            mEmptyView.setVisibility(GONE);
            getLoaderManager().restartLoader(PHONE_NUMBERS_LOADER_ID, null, this);
            getLoaderManager().restartLoader(EMAILS_LOADER_ID, null, this);
            getLoaderManager().restartLoader(ADDRESSES_LOADER_ID, null, this);
            getLoaderManager().restartLoader(EVENTS_LOADER_ID, null, this);

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
        if (savedInstanceState == null) {
            // Sets the argument extra as the currently displayed contact
            setContact(getArguments() != null ?
                            ((Uri) getArguments().getParcelable(EXTRA_CONTACT_URI)) : null,
                    getArguments() != null ? getArguments().getString(EXTRA_CONTACT_NAME) : null);
        } else {
            // If being recreated from a saved state, sets the contact from the incoming
            // savedInstanceState Bundle
            setContact((Uri) savedInstanceState.getParcelable(EXTRA_CONTACT_URI),
                    savedInstanceState.getString(EXTRA_CONTACT_NAME));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_CONTACT_URI, mContactUri);
        outState.putString(EXTRA_CONTACT_NAME, mName);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        long contactId = ContentUris.parseId(mContactUri);
        String selection;
        String[] selectionArgs;
        switch (i) {
            case PHONE_NUMBERS_LOADER_ID:
                selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
                selectionArgs = new String[]{String.valueOf(contactId)};

                return new android.content.CursorLoader(getActivity(),
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        PONE_NUMBER_PROJECTION, selection, selectionArgs, null);

            case EMAILS_LOADER_ID:
                selection = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?";
                selectionArgs = new String[]{String.valueOf(contactId)};

                Log.i("oncreateLo", "uri: " + mContactUri);
                return new android.content.CursorLoader(getActivity(),
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        EMAIL_PROJECTION, selection, selectionArgs, null);

            case ADDRESSES_LOADER_ID:
                selection = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?";
                selectionArgs = new String[]{String.valueOf(contactId)};

                return new android.content.CursorLoader(getActivity(),
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                        ADDRESS_PROJECTION,
                        selection, selectionArgs, null);
            case EVENTS_LOADER_ID:
                selection = ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY +
                        " AND " + ContactsContract.CommonDataKinds.Event.MIMETYPE +
                        " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE +
                        "' AND " + ContactsContract.Data.CONTACT_ID + " = " + contactId;

                return new android.content.CursorLoader(getActivity(),
                        ContactsContract.Data.CONTENT_URI,
                        EVENT_PROJECTION,
                        selection, null, null);
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind(); //TODO here?
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i("OnLoad", "Finished details:");
        switch (loader.getId()) {
            case EMAILS_LOADER_ID:
                while (cursor.moveToNext()) {
                    mEmails.add(new ContactEmail(
                            cursor.getString(INDEX_EMAIL_ADDRESS),
                            cursor.getString(INDEX_EMAIL_ID),
                            cursor.getInt(INDEX_EMAIL_TYPE)));
                }
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    mPhotoUri = cursor.getString(INDEX_EMAIL_PHOTO_URI);
                    Picasso.with(getActivity()).load(mPhotoUri).centerCrop().resize(222, 222).
                            placeholder(R.drawable.ic_contact_picture).error(R.drawable.ic_contact_picture).into(mImageView);
                }
                mEmailAdapter.swapCursor(cursor);
                break;
            case ADDRESSES_LOADER_ID:
                while (cursor.moveToNext()) {
                    mAddresses.add(new ContactAddress(
                            cursor.getString(INDEX_ADDRESS_FORMATTED),
                            cursor.getString(INDEX_ADDRESS_ID),
                            cursor.getInt(INDEX_ADDRESS_TYPE)));

                }

                mAddressAdapter.swapCursor(cursor);
                break;
            case PHONE_NUMBERS_LOADER_ID:
                //cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    mPhoneNumbers.add(new ContactPhone(
                            cursor.getString(INDEX_PHONE_NUMBER),
                            cursor.getString(INDEX_PHONE_ID),
                            cursor.getInt(INDEX_PHONE_TYPE)));
                }
                mNumberAdapter.swapCursor(cursor);
                break;
            case EVENTS_LOADER_ID:
                if (cursor.moveToFirst()) {
                    mBdate = cursor.getString(INDEX_EVENT_START_DATE);
                    if (!mBdate.isEmpty()) {
                        mContactBday.setVisibility(VISIBLE);
                        mContactBdayLabel.setVisibility(VISIBLE);
                        mContactBday.setText(mBdate);
                    }
                } else {
                    mContactBday.setVisibility(GONE);
                    mContactBdayLabel.setVisibility(GONE);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAddressAdapter.swapCursor(null);
        mEmailAdapter.swapCursor(null);
        mNumberAdapter.swapCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (mContactUri != null && !mIsTwoPaneLayout) {
            inflater.inflate(R.menu.contact_detail_menu, menu);

            mEditContactMenuItem = menu.findItem(R.id.menu_edit_contact);
            Log.i("DetailsFragOptionsMenu", "mContact uri null " + (mContactUri == null));
            mEditContactMenuItem.setVisible(mContactUri != null && !mIsTwoPaneLayout); //duh
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_contact:
                Intent editContact = new Intent(Intent.ACTION_EDIT, mContactUri,
                        getActivity().getBaseContext(), ContactEditorActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(EXTRA_NUMBERS_LIST, mPhoneNumbers);
                bundle.putParcelableArrayList(EXTRA_EMAILS_LIST, mEmails);
                bundle.putParcelableArrayList(EXTRA_ADDRESSES_LIST, mAddresses);
                bundle.putString("birthday", mBdate);
                bundle.putString("photoUri", mPhotoUri);
                bundle.putString(EXTRA_CONTACT_NAME,
                        mName);
                editContact.putExtras(bundle);
                startActivity(editContact);
                break;
            case R.id.delete_contact:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Delete contact?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (mContactUri != null)
                                    getActivity().getContentResolver().delete(mContactUri, null, null);
                                if (mIsTwoPaneLayout) {
                                    startActivity(new Intent(getActivity(), ContactsActivity.class));
                                    mEmptyView.setVisibility(VISIBLE);
                                    mDetailsLinearLayout.setVisibility(View.INVISIBLE);

                                } else
                                    getActivity().finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
