package com.roberts.adrian.androidcodetestadrianroberts;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.roberts.adrian.androidcodetestadrianroberts.models.ContactAddress;
import com.roberts.adrian.androidcodetestadrianroberts.models.ContactEmail;
import com.roberts.adrian.androidcodetestadrianroberts.models.ContactPhone;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.roberts.adrian.androidcodetestadrianroberts.DbUtils.getRawId;
import static com.roberts.adrian.androidcodetestadrianroberts.DbUtils.insertAdditionalAddress;
import static com.roberts.adrian.androidcodetestadrianroberts.DbUtils.insertAdditionalEmail;
import static com.roberts.adrian.androidcodetestadrianroberts.DbUtils.insertAdditionalNumber;
import static com.roberts.adrian.androidcodetestadrianroberts.DbUtils.insertAddress;
import static com.roberts.adrian.androidcodetestadrianroberts.DbUtils.insertBirthday;
import static com.roberts.adrian.androidcodetestadrianroberts.DbUtils.insertDisplayName;
import static com.roberts.adrian.androidcodetestadrianroberts.DbUtils.insertEmail;
import static com.roberts.adrian.androidcodetestadrianroberts.DbUtils.insertNumber;
import static com.roberts.adrian.androidcodetestadrianroberts.DbUtils.updateBirthdate;
import static com.roberts.adrian.androidcodetestadrianroberts.DbUtils.updateContactField;
import static com.roberts.adrian.androidcodetestadrianroberts.DbUtils.updateDisplayName;

public class ContactEditorActivity extends AppCompatActivity
        implements View.OnClickListener, TextWatcher {
    final static String TAG = ContactEditorActivity.class.getSimpleName();

    @BindView(R.id.spinner_type_address)
    Spinner mSpinnerAddress;
    @BindView(R.id.spinner_type_phone)
    Spinner mSpinnerPhone;
    @BindView(R.id.spinner_type_email)
    Spinner mSpinnerEmail;
    @BindView(R.id.addresses_ll)
    LinearLayout mAddressesLL;
    @BindView(R.id.emails_ll)
    LinearLayout mEmailLL;
    @BindView(R.id.phone_numbers_ll)
    LinearLayout mPhoneLL;
    @BindView(R.id.add_address_field)
    ImageButton mExtraAddressButton;
    @BindView(R.id.add_email_field)
    ImageButton mExtraEmailButton;
    @BindView(R.id.add_phone_field)
    ImageButton mExtraPhoneButton;
    @BindView(R.id.edit_text_address)
    EditText mAddressEditText;
    @BindView(R.id.edit_text_phone)
    EditText mPhoneEditText;
    @BindView(R.id.edit_text_email)
    EditText mEmailEditText;
    @BindView(R.id.edit_text_fname)
    EditText mFnameEditText;
    @BindView(R.id.edit_text_lname)
    EditText mLnameEditText;
    @BindView(R.id.edit_birthday)
    EditText mBirthdayEditText;
    @BindView(R.id.image_view)
    ImageView mImageView;
    @BindView(R.id.add_photo_image_button)
    ImageButton mImageButton;

    static final int FIELD_TYPE_EMAIL_HOME = ContactsContract.CommonDataKinds.Email.TYPE_HOME;
    static final int FIELD_TYPE_EMAIL_WORK = ContactsContract.CommonDataKinds.Email.TYPE_WORK;
    static final int FIELD_TYPE_EMAIL_OTHER = ContactsContract.CommonDataKinds.Email.TYPE_OTHER;
    static final int FIELD_TYPE_PHONE_HOME = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
    static final int FIELD_TYPE_PHONE_WORK = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
    static final int FIELD_TYPE_PHONE_MOBILE = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
    static final int FIELD_TYPE_PHONE_OTHER = ContactsContract.CommonDataKinds.Phone.TYPE_OTHER;
    static final int FIELD_TYPE_ADDRESS_HOME = ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME;
    static final int FIELD_TYPE_ADDRESS_WORK = ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK;
    static final int FIELD_TYPE_ADDRESS_OTHER = ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER;

    static final int MY_CONTACT_REQ = 101;
    static final int MY_WRITE_REQ = 102;
    static final int ADDRESS_BASE_ID = 100;
    static final int EMAIL_BASE_ID = 200;
    static final int NUMBER_BASE_ID = 300;
    static final int SPINNER_ID_PREFIX = 900;


    static final int TYPE_INDEX_HOME = 0;
    static final int TYPE_INDEX_WORK = 1;
    static final int TYPE_INDEX_OTHER = 2;
    static final int TYPE_INDEX_MOBILE = 3;


    private ArrayAdapter<String> mPhoneTypesAdapter;
    private ArrayAdapter<String> mAddressTypesAdapter;

    private ArrayList<Integer> mAdditionalAddressViews;
    private ArrayList<Integer> mAdditionalEmailsViews;
    private ArrayList<Integer> mAdditionalPhonesViews;


    private ArrayList<ContactPhone> mPhoneNumbers;
    private ArrayList<ContactEmail> mEmails;
    private ArrayList<ContactAddress> mAddresses;

    private Uri mContactUri;
    boolean mEditContactMode;
    private String mContactID;
    private int mRawContactId;
    private Calendar myCalendar;
    private ArrayList<ContentProviderOperation> ops = new ArrayList<>();
    private boolean mExitWithoutPrompt = true;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    private static final String EXTRA_FILENAME =
            " com.roberts.adrian.androidcodetestadrianroberts.EXTRA_FILENAME";
    private static final String FILENAME = "testFileName";
    private static final int CONTENT_REQUEST = 1337;
    private static final String AUTHORITY =
            BuildConfig.APPLICATION_ID + ".provider";
    private static final String PHOTOS = "photos";
    private File output = null;
    private String selectedImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_editor);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_REQ);
        }
        ButterKnife.bind(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, MY_CONTACT_REQ);
        }
        mPhoneNumbers = new ArrayList<>();
        mAddresses = new ArrayList<>();
        mEmails = new ArrayList<>();

        mAdditionalAddressViews = new ArrayList<>();
        mAdditionalEmailsViews = new ArrayList<>();
        mAdditionalPhonesViews = new ArrayList<>();

        mPhoneTypesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.field_types_phone));
        mAddressTypesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.field_types_address_email));

        mSpinnerPhone.setAdapter(mPhoneTypesAdapter);
        mSpinnerEmail.setAdapter(mAddressTypesAdapter);
        mSpinnerAddress.setAdapter(mAddressTypesAdapter);


        Intent intent = getIntent();
        if (intent.getData() != null) {
            mContactUri = intent.getData();
            mContactID = String.valueOf(ContentUris.parseId(mContactUri));
            mRawContactId = getRawId(this, mContactID);

            mEditContactMode = true;
            Bundle args = intent.getExtras();

            mPhoneNumbers = args.getParcelableArrayList(ContactDetailsFragment.EXTRA_NUMBERS_LIST);
            mAddresses = args.getParcelableArrayList(ContactDetailsFragment.EXTRA_ADDRESSES_LIST);
            mEmails = args.getParcelableArrayList(ContactDetailsFragment.EXTRA_EMAILS_LIST);


            String fullName = args.getString(ContactsFragment.EXTRA_CONTACT_NAME);
            String fName;
            String lName;
            if (fullName.contains(" ")) {
                fName = fullName.substring(0, fullName.indexOf(' '));
                lName = fullName.substring(fName.length() + 1, fullName.length());
            } else {
                fName = fullName;
                lName = null;
            }
            mLnameEditText.setText(lName);
            mFnameEditText.setText(fName);

            mBirthdayEditText.setText(args.getString("birthday"));
            if (mAddresses.size() > 0)
                mSpinnerAddress.setSelection(Utils.getSpinnerLabelAddress(mAddresses.get(0).getType()));
            mSpinnerPhone.setSelection(Utils.getSpinnerLabelPhone(mPhoneNumbers.get(0).getType()));
            mSpinnerEmail.setSelection(Utils.getSpinnerLabelEmail(mEmails.get(0).getType()));

            mPhoneEditText.setText(mPhoneNumbers.get(0).getNumber());
            for (int i = 1; i < mPhoneNumbers.size(); i++) {
                addField(mPhoneLL, mPhoneNumbers.get(i).getNumber(), mPhoneNumbers.get(i).getType());
            }
            mEmailEditText.setText(mEmails.get(0).getEmail());
            for (int i = 1; i < mEmails.size(); i++) {
                addField(mEmailLL, mEmails.get(i).getEmail(), mEmails.get(i).getType());
            }
            if (mAddresses.size() > 0) {
                mAddressEditText.setText(mAddresses.get(0).getAddress());
                for (int i = 1; i < mAddresses.size(); i++) {
                    addField(mAddressesLL, mAddresses.get(i).getAddress(), mAddresses.get(i).getType());
                }
            }
            Log.i(TAG, "hi");
        }

        mBirthdayEditText.setOnClickListener(this);

        mExtraAddressButton.setOnClickListener(this);
        mExtraPhoneButton.setOnClickListener(this);
        mExtraEmailButton.setOnClickListener(this);
        mImageButton.setOnClickListener(this);

        mFnameEditText.addTextChangedListener(this);
        mLnameEditText.addTextChangedListener(this);
        mEmailEditText.addTextChangedListener(this);
        mPhoneEditText.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        mExitWithoutPrompt = false;
        switch (view.getId()) {
            case R.id.add_address_field:
                addField(mAddressesLL, null, 0);
                break;
            case R.id.add_email_field:
                addField(mEmailLL, null, 0);
                break;
            case R.id.add_phone_field:
                addField(mPhoneLL, null, 0);
                break;
            case R.id.edit_birthday:
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(mBirthdayEditText.getWindowToken(), 0);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                setBirthday();
                break;
            case R.id.add_photo_image_button:
               // addPhoto();
                break;
        }
    }

    private void setBirthday() {
        myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DATE, dayOfMonth);
                updateBirtdayEditText();
            }

        };
        DatePickerDialog pickerDialog = new DatePickerDialog(ContactEditorActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DATE));

        DatePicker datePicker = pickerDialog.getDatePicker();
        datePicker.setMaxDate(System.currentTimeMillis());
        datePicker.getTouchables().get(0).performClick();
        pickerDialog.show();
    }

    private void addPhoto() {
        output = new File(new File(getFilesDir(), PHOTOS), FILENAME);

        if (output.exists()) {
            output.delete();
        } else {
            output.getParentFile().mkdirs();
        }

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri outputUri = FileProvider.getUriForFile(this, AUTHORITY, output);

        i.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip =
                    ClipData.newUri(getContentResolver(), "A photo", outputUri);

            i.setClipData(clip);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            List<ResolveInfo> resInfoList =
                    getPackageManager()
                            .queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                grantUriPermission(packageName, outputUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }

        try {
            startActivityForResult(i, CONTENT_REQUEST);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No camera detected", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CONTENT_REQUEST) {
            if (resultCode == RESULT_OK) {
               // Intent i = new Intent(Intent.ACTION_VIEW);
                Uri outputUri = FileProvider.getUriForFile(this, AUTHORITY, output);

              //  i.setDataAndType(outputUri, "image/jpeg");
               // i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Bitmap bitmap = decodeFile(output.getAbsolutePath());
                Bitmap bitmap2 = decodeFile(outputUri.getPath());
                mImageView.setImageBitmap(bitmap);

                try {
                   // startActivity(i);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, R.string.msg_no_viewer, Toast.LENGTH_LONG).show();
                }

                finish();
            }
        }
    }

    private void insertPhoto(Bitmap photo) {
        ByteArrayOutputStream BmpStream = new ByteArrayOutputStream();
        ContentResolver cr = this.getContentResolver();
        Uri RawContactPhotoUri;

        RawContactPhotoUri = Uri.withAppendedPath(
                ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, mRawContactId),
                ContactsContract.RawContacts.DisplayPhoto.CONTENT_DIRECTORY
        );

        photo.compress(Bitmap.CompressFormat.JPEG, 100, BmpStream);

        try {
            AssetFileDescriptor fd = cr.openAssetFileDescriptor(RawContactPhotoUri, "rw");
            OutputStream os = fd.createOutputStream();
            os.write(BmpStream.toByteArray());
            os.close();
            fd.close();
        } catch (IOException e) {

        }
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    public String getAbsolutePath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void updateBirtdayEditText() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);
        mBirthdayEditText.setText(sdf.format(myCalendar.getTime()));
    }


    private int getEmailFieldType(String selection) {
        return selection.equals(getString(R.string.field_type_work)) ? FIELD_TYPE_EMAIL_WORK :
                selection.equals(getString(R.string.field_type_home)) ? FIELD_TYPE_EMAIL_HOME : FIELD_TYPE_EMAIL_OTHER;
    }

    private int getAddressFieldType(String selection) {
        return selection.equals(getString(R.string.field_type_work)) ? FIELD_TYPE_ADDRESS_WORK :
                selection.equals(getString(R.string.field_type_home)) ? FIELD_TYPE_ADDRESS_HOME : FIELD_TYPE_ADDRESS_OTHER;
    }

    private int getPhoneFieldType(String selection) {
        return selection.equals(getString(R.string.field_type_work)) ? FIELD_TYPE_PHONE_WORK :
                selection.equals(getString(R.string.field_type_home)) ? FIELD_TYPE_PHONE_HOME :
                        selection.equals(getString(R.string.field_type_mobile)) ? FIELD_TYPE_PHONE_MOBILE :
                                FIELD_TYPE_PHONE_OTHER;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(ContactDetailsFragment.EXTRA_NUMBERS_LIST, mPhoneNumbers);
        outState.putParcelableArrayList(ContactDetailsFragment.EXTRA_EMAILS_LIST, mEmails);
        outState.putParcelableArrayList(ContactDetailsFragment.EXTRA_ADDRESSES_LIST, mAddresses);
        super.onSaveInstanceState(outState);

    }

    private void saveContact() {
        int rawContactInsertIndex = ops.size();

        // getting main fields from views visible by default
        String mainNumber = mPhoneEditText.getText().toString();
        String mainEmail = mEmailEditText.getText().toString();
        String familyName = mLnameEditText.getText().toString();
        String givenName = mFnameEditText.getText().toString();
        String displayName = givenName + " " + familyName;
        String bDay = mBirthdayEditText.getText().toString();
        String mainAddress = mAddressEditText.getText().toString();
        int email_type = getEmailFieldType((String) mSpinnerEmail.getSelectedItem());
        int phone_type = getPhoneFieldType((String) mSpinnerPhone.getSelectedItem());


        int address_type = getAddressFieldType((String) mSpinnerAddress.getSelectedItem());

        if (!mEditContactMode) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());
            insertDisplayName(ops, displayName, rawContactInsertIndex);
            if (!isEmpty(mBirthdayEditText))
                insertBirthday(ops, bDay, rawContactInsertIndex);
            insertNumber(ops, mainNumber, rawContactInsertIndex, phone_type);
            insertEmail(ops, mainEmail, rawContactInsertIndex, email_type);
            if (!isEmpty(mAddressEditText))
                insertAddress(ops, mainAddress, rawContactInsertIndex, address_type);

            // Add extra details
            Collections.sort(mAdditionalAddressViews);
            Collections.sort(mAdditionalEmailsViews);
            Collections.sort(mAdditionalPhonesViews);
            for (int i = 0; i < mAdditionalAddressViews.size(); i++) {
                EditText extraAddressEt = (EditText) findViewById(mAdditionalAddressViews.get(i));
                Spinner extraAddressSpinner = (Spinner) findViewById(SPINNER_ID_PREFIX + mAdditionalAddressViews.get(i));
                int addressType = getAddressFieldType((String) extraAddressSpinner.getSelectedItem());
                insertAddress(ops, extraAddressEt.getText().toString(), rawContactInsertIndex, addressType);
            }

            for (int i = 0; i < mAdditionalEmailsViews.size(); i++) {
                EditText extraEmailEt = (EditText) findViewById(mAdditionalEmailsViews.get(i));
                Spinner extraEmailSpinner = (Spinner) findViewById(SPINNER_ID_PREFIX + mAdditionalEmailsViews.get(i));
                int extraEmailType = getEmailFieldType((String) extraEmailSpinner.getSelectedItem());
                String exEmail = extraEmailEt.getText().toString();
                insertEmail(ops, exEmail, rawContactInsertIndex, extraEmailType);
            }
            for (int i = 0; i < mAdditionalPhonesViews.size(); i++) {
                EditText extraPhoneEt = (EditText) findViewById(mAdditionalPhonesViews.get(i));
                Spinner extraPhoneSpinner = (Spinner) findViewById(SPINNER_ID_PREFIX + mAdditionalPhonesViews.get(i));
                int extraNumberType = getEmailFieldType((String) extraPhoneSpinner.getSelectedItem());
                insertNumber(ops, extraPhoneEt.getText().toString(), rawContactInsertIndex, extraNumberType);
            }

        } else {
            updateDisplayName(ops, displayName, mContactID);
            updateBirthdate(ops, bDay, mContactID);

            // update main email
            updateContactField(ops,
                    mainEmail,
                    mEmails.get(0).getEmailId(),
                    mContactID,
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                    ContactsContract.CommonDataKinds.Email.TYPE,
                    email_type);
            mEmails.get(0).setEmail(mainEmail);

            // update main number
            updateContactField(ops,
                    mainNumber,
                    mPhoneNumbers.get(0).getNumberIdd(),
                    mContactID,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    phone_type);
            mPhoneNumbers.get(0).setNumber(mainNumber);

            // update main address
            if (!isEmpty(mAddressEditText)) {
                if (mAddresses.size() == 0) {
                    insertAdditionalAddress(ops, mAddressEditText.getText().toString(), mRawContactId, address_type);
                    Log.i(TAG, "insert addresss");
                } else {
                    updateContactField(ops,
                            mainAddress,
                            mAddresses.get(0).getAddressId(),
                            mContactID,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE,
                            ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
                            address_type);
                    mAddresses.get(0).setAddress(mainAddress);
                }
            }
            /**
             * update additional fields
             * if a field has an id it should be updated otherwise perform new insertion
             */

            for (int i = 0; i < mAdditionalPhonesViews.size(); i++) {
                EditText extraPhoneEt = (EditText) findViewById(mAdditionalPhonesViews.get(i));
                String newNumber = extraPhoneEt.getText().toString();
                Spinner extraPhoneSpinner = (Spinner) findViewById(SPINNER_ID_PREFIX + mAdditionalPhonesViews.get(i));
                int extraNumberType = getEmailFieldType((String) extraPhoneSpinner.getSelectedItem());

                int numberIndex = i + 1;
                if (mPhoneNumbers.size() > numberIndex &&
                        mPhoneNumbers.get(numberIndex).getNumberIdd() != null) {
                    // Update exisiting number
                    updateContactField(ops,
                            newNumber,
                            mPhoneNumbers.get(numberIndex).getNumberIdd(),
                            mContactID,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE,
                            extraNumberType);
                    mPhoneNumbers.get(numberIndex).setNumber(newNumber);
                } // insert new number
                else {
                    insertAdditionalNumber(ops, newNumber, mRawContactId, extraNumberType);
                }

            }
            for (int i = 0; i < mAdditionalEmailsViews.size(); i++) {
                int emailsIndex = i + 1;
                EditText extraEmailEt = (EditText) findViewById(mAdditionalEmailsViews.get(i));
                String newEmail = extraEmailEt.getText().toString();
                Spinner extraEmailSpinner = (Spinner) findViewById(SPINNER_ID_PREFIX + mAdditionalEmailsViews.get(i));
                int extraEmailType = getEmailFieldType((String) extraEmailSpinner.getSelectedItem());
                if (mEmails.size() > emailsIndex && mEmails.get(emailsIndex).getEmailId() != null) {

                    updateContactField(ops,
                            newEmail,
                            mEmails.get(emailsIndex).getEmailId(),
                            mContactID,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                            ContactsContract.CommonDataKinds.Email.TYPE,
                            extraEmailType);
                    mEmails.get(emailsIndex).setEmail(newEmail);
                } else {
                    insertAdditionalEmail(ops, newEmail, mRawContactId, extraEmailType);
                    // insertAdditionalAddress(ops, new, mRawContactId, extraNumberType);
                }


            }
            for (int i = 0; i < mAdditionalAddressViews.size(); i++) {
                int addrIndex = i + 1;
                EditText extraEmailEt = (EditText) findViewById(mAdditionalAddressViews.get(i));
                String newAddr = extraEmailEt.getText().toString();
                Spinner extraEmailSpinner = (Spinner) findViewById(SPINNER_ID_PREFIX + mAdditionalAddressViews.get(i));
                int extraAddressType = getEmailFieldType((String) extraEmailSpinner.getSelectedItem());
                if (mAddresses.size() > addrIndex && mAddresses.get(addrIndex).getAddressId() != null) {

                    updateContactField(ops,
                            newAddr,
                            mAddresses.get(addrIndex).getAddressId(),
                            mContactID,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE,
                            ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
                            extraAddressType);
                    mAddresses.get(addrIndex).setAddress(newAddr);
                } else
                    insertAdditionalAddress(ops, newAddr, mRawContactId, extraAddressType);

            }

        }

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    /**
     * Adds a linear layout of either an additional phonenumber, email, or address.
     *
     * @param linearLayout
     * @param existingValue
     * @param contactType
     * @return
     */
    private LinearLayout addField(final LinearLayout linearLayout, final String existingValue, int contactType) {
        final LinearLayout newField = new LinearLayout(this);
        newField.setLayoutParams((new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)));
        newField.setOrientation(LinearLayout.HORIZONTAL);
        final ImageButton deleteButton = new ImageButton(this);
        Spinner spinner = new Spinner(this);

        final int index = linearLayout.getChildCount() - 1; //  -1  Already has one child

        String hint = "";
        int inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS;
        final EditText editText = new EditText(this);

        // set hint, iputType, and ID of edittext and spinner according to which contact field is being added
        switch (linearLayout.getId()) {
            case R.id.addresses_ll:
                hint = "Additional address";
                inputType += InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS;
                editText.setId(ADDRESS_BASE_ID + linearLayout.getChildCount());
                mAdditionalAddressViews.add(index, editText.getId());
                deleteButton.setId(editText.getId());
                newField.setId(index + 1);

                spinner.setAdapter(mAddressTypesAdapter);
                spinner.setSelection(Utils.getSpinnerLabelAddress(contactType));
                spinner.setId(SPINNER_ID_PREFIX + editText.getId());
                break;
            case R.id.emails_ll:
                hint = "Additional e-mail";
                inputType += InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                editText.setId(EMAIL_BASE_ID + linearLayout.getChildCount());
                mAdditionalEmailsViews.add(index, editText.getId());
                deleteButton.setId(editText.getId());
                spinner.setAdapter(mAddressTypesAdapter);
                spinner.setSelection(Utils.getSpinnerLabelAddress(contactType));
                spinner.setId(SPINNER_ID_PREFIX + editText.getId());
                break;
            case R.id.phone_numbers_ll:
                hint = "Additional phone";
                inputType += InputType.TYPE_CLASS_PHONE;
                editText.setId(NUMBER_BASE_ID + linearLayout.getChildCount());
                spinner.setAdapter(mPhoneTypesAdapter);
                mAdditionalPhonesViews.add(index, editText.getId());
                newField.setId(index);
                deleteButton.setId(editText.getId());
                spinner.setId(SPINNER_ID_PREFIX + editText.getId());
                spinner.setSelection(Utils.getSpinnerLabelPhone(contactType));
                break;
        }


        editText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4));
        editText.setHint(hint);
        editText.setInputType(inputType);
        if (mEditContactMode) editText.setText(existingValue);
        newField.addView(editText);


        spinner.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
        newField.addView(spinner);


        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        deleteButton.setImageResource(R.drawable.ic_action_delete);
        deleteButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBackground));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            // Deletes the view of any of the tree contact fields.
            // if in edit mode deletion from contactContract is set up
            @Override
            public void onClick(View view) {
                mExitWithoutPrompt = false;
                Object deleteItem = deleteButton.getId();
                linearLayout.removeView(newField);
                switch (linearLayout.getId()) {
                    case R.id.addresses_ll:
                        int addrIndex = mAdditionalAddressViews.indexOf(deleteItem) + 1;
                        mAdditionalAddressViews.remove(mAdditionalAddressViews.indexOf(deleteItem));

                        if (mEditContactMode && mAddresses.size() > addrIndex) {
                            DbUtils.deleteContactDetail(ContactEditorActivity.this,
                                    ops,
                                    mContactID,
                                    mAddresses.get(addrIndex).getAddressId(),
                                    ContactsContract.CommonDataKinds.StructuredPostal._ID,
                                    ContactsContract.CommonDataKinds.StructuredPostal.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
                            mAddresses.remove(addrIndex);
                        }
                        break;
                    case R.id.emails_ll:
                        int emailIndex = mAdditionalEmailsViews.indexOf(deleteItem) + 1;
                        mAdditionalEmailsViews.remove(mAdditionalEmailsViews.indexOf(deleteItem));
                        if (mEditContactMode && mEmails.size() > emailIndex) {
                            DbUtils.deleteContactDetail(ContactEditorActivity.this,
                                    ops,
                                    mContactID,
                                    mEmails.get(emailIndex).getEmailId(),
                                    ContactsContract.CommonDataKinds.Email._ID,
                                    ContactsContract.CommonDataKinds.Email.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                            mEmails.remove(emailIndex);
                        }
                        break;
                    case R.id.phone_numbers_ll:
                        int numberIndex = mAdditionalPhonesViews.indexOf(deleteItem) + 1;
                        mAdditionalPhonesViews.remove(mAdditionalPhonesViews.indexOf(deleteItem));

                        if (mEditContactMode && mPhoneNumbers.size() > numberIndex) {
                            Log.i(TAG, "index: " + index);
                            DbUtils.deleteContactDetail(ContactEditorActivity.this,
                                    ops,
                                    mContactID,
                                    mPhoneNumbers.get(numberIndex).getNumberIdd(),
                                    ContactsContract.CommonDataKinds.Phone._ID,
                                    ContactsContract.CommonDataKinds.Phone.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                            mPhoneNumbers.remove(numberIndex);
                        }
                        break;
                }

            }
        });
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        newField.addView(deleteButton);
        linearLayout.addView(newField);

        return linearLayout;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.edit_contact_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_contact_menu_item:
                if (isEmpty(mFnameEditText) || isEmpty(mLnameEditText) || isEmpty(mEmailEditText) || isEmpty(mPhoneEditText))
                    Toast.makeText(this, "Some required fields are empty!", Toast.LENGTH_SHORT).show();
                    //(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                else {

                    saveContact();
                }
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onBackPressed() {
        if (mExitWithoutPrompt) ContactEditorActivity.this.finish();
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Discard edit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ContactEditorActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mExitWithoutPrompt = false;
    }


}
