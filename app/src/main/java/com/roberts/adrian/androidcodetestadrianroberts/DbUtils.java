package com.roberts.adrian.androidcodetestadrianroberts;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;


public class DbUtils {
    private final static String TAG = DbUtils.class.getSimpleName();

    public void DbUtils() {

    }

    public static void updateDisplayName(ArrayList<ContentProviderOperation> ops,
                                         String displayName, String contactId) {

        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                ContactsContract.Data.MIMETYPE + " = ?";
        String[] params = new String[]{contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, params)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName)
                .build());
    }

    public static void insertDisplayName(ArrayList<ContentProviderOperation> ops, String displayName,
                                         int rawContactInsertIndex) {
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName)
                .build());
    }


    public static void updateBirthdate(ArrayList<ContentProviderOperation> ops,
                                       String birthdate, String contactId) {

        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " +
                ContactsContract.Data.MIMETYPE + " = ?";
        String[] params = new String[]{contactId, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE};
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, params)
                .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, birthdate)
                .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                .build());
    }

    public static void insertBirthday(ArrayList<ContentProviderOperation> ops, String date,
                                      int rawContactInsertIndex) {
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, date)
                .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                .build());
    }


    public static void insertEmail(ArrayList<ContentProviderOperation> ops, String email, int index, int type) {
        Log.i(TAG, "add new email: " + email);

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, type)
                .build());
    }

    public static void insertAdditionalEmail(ArrayList<ContentProviderOperation> ops, String email, int contactId, int type) {
        Log.i(TAG, "add new email: " + email);

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, type)
                .build());
    }


    public static void insertAddress(ArrayList<ContentProviderOperation> ops, String address, int index, int type) {
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, type)
                .build());

    }

    public static void insertAdditionalAddress(ArrayList<ContentProviderOperation> ops, String address, int id, int type) {
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, id)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, type)
                .build());

    }

    public static void insertAdditionalNumber(ArrayList<ContentProviderOperation> ops, String phoneNO, int contactId, int type) {
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNO)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, type)
                .build());
    }

    public static void insertNumber(ArrayList<ContentProviderOperation> ops, String phoneNO, int index, int type) {
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, type)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNO)
                .build());
    }

    public static void updateContactField(ArrayList<ContentProviderOperation> ops,
                                          String value,
                                          String fieldId,
                                          String contactId,
                                          String mimetype,
                                          String fieldType,
                                          int type) {
        Log.i(TAG, "type " + type);
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + " = ?", new String[]{contactId})
                .withSelection(ContactsContract.Data._ID + " = ?", new String[]{fieldId})
                .withValue(ContactsContract.Data.MIMETYPE, mimetype)
                .withValue(ContactsContract.Data.DATA1, value)
                .withValue(fieldType, String.valueOf(type))
                // .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, label)
                .build());

    }



    public static void deleteContactDetail(Context c, ArrayList<ContentProviderOperation> ops, String contactId, String fieldId, String id_type, String mimetype, String content_item_type) {
        Cursor cur = c.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID},
                ContactsContract.RawContacts.CONTACT_ID + "=?",
                new String[]{contactId}, null);
        int rowId = 0;

        if (cur.moveToFirst()) {
            rowId = cur.getInt(cur.getColumnIndex(ContactsContract.RawContacts._ID));
        }
        Log.i(TAG, "delete numberID " + fieldId);

        //  ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        String selectPhone = ContactsContract.RawContacts.Data.RAW_CONTACT_ID + " = ? AND " +
                mimetype + " = ? AND " +
                id_type + " = ?";
        String[] phoneArgs = new String[]{String.valueOf(rowId),
                content_item_type,
                fieldId};

        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(selectPhone, phoneArgs).build());
    }

    public static int getRawId(Context context, String contactId) {
        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(contactId)};
        Cursor c = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(ContactsContract.RawContacts._ID));
        }
        return 0;
    }
}
