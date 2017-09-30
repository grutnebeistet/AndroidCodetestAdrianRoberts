package com.roberts.adrian.androidcodetestadrianroberts;

import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.FIELD_TYPE_ADDRESS_HOME;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.FIELD_TYPE_ADDRESS_OTHER;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.FIELD_TYPE_ADDRESS_WORK;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.FIELD_TYPE_EMAIL_HOME;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.FIELD_TYPE_EMAIL_WORK;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.FIELD_TYPE_PHONE_HOME;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.FIELD_TYPE_PHONE_MOBILE;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.FIELD_TYPE_PHONE_OTHER;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.FIELD_TYPE_PHONE_WORK;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.TYPE_INDEX_HOME;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.TYPE_INDEX_MOBILE;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.TYPE_INDEX_OTHER;
import static com.roberts.adrian.androidcodetestadrianroberts.ContactEditorActivity.TYPE_INDEX_WORK;

public class Utils {
    public static int getSpinnerLabelEmail(int type) {
        switch (type) {
            case FIELD_TYPE_EMAIL_HOME:
                return TYPE_INDEX_HOME;

            case FIELD_TYPE_EMAIL_WORK:
                return TYPE_INDEX_WORK;

            default:
                return TYPE_INDEX_OTHER;

        }
    }

    public static int getSpinnerLabelAddress(int type) {
        switch (type) {
            case FIELD_TYPE_ADDRESS_HOME:
                return TYPE_INDEX_HOME;
            case FIELD_TYPE_ADDRESS_WORK:
                return TYPE_INDEX_WORK;
            case FIELD_TYPE_ADDRESS_OTHER:
                return TYPE_INDEX_OTHER;
            default:
                return TYPE_INDEX_OTHER;


        }
    }

    public static int getSpinnerLabelPhone(int type) {
        switch (type) {
            case FIELD_TYPE_PHONE_HOME:
                return TYPE_INDEX_HOME;
            case FIELD_TYPE_PHONE_WORK:
                return TYPE_INDEX_WORK;
            case FIELD_TYPE_PHONE_MOBILE:
                return TYPE_INDEX_MOBILE;
            case FIELD_TYPE_PHONE_OTHER:
                return TYPE_INDEX_OTHER;
            default:
                return TYPE_INDEX_OTHER;

        }
    }
}
