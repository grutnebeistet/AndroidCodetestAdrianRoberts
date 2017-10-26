package com.roberts.adrian.androidcodetestadrianroberts.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.roberts.adrian.androidcodetestadrianroberts.ContactsFragment;
import com.roberts.adrian.androidcodetestadrianroberts.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>
        implements Filterable {

    private String mSearchTerm;
    private Cursor mCursor;
    private Context mContext;
    private MergeCursor mFilteredCursor;
    private ContactAdapterOnClickHandler mOnClickHandler;
    private HashMap<String, String> mEmails;
    private HashMap<String, String> mNumbers;
    private int selectedPos = -1;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                mSearchTerm = charSequence.toString();
                if (mSearchTerm.isEmpty()) {
                    mFilteredCursor = new MergeCursor(new Cursor[]{mCursor, null});
                } else {
                    MatrixCursor filteredMatrixC = new MatrixCursor(
                            new String[]{ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                                    ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY,
                                    ContactsContract.CommonDataKinds.Email.ADDRESS,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI});

                    mCursor.moveToFirst();
                    do {
                        String id = mCursor.getString(ContactsFragment.INDEX_EMAIL_CONTACT_ID);
                        String name = mCursor.getString(ContactsFragment.INDEX_EMAIL_DISPLAY_NAME);
                        String contactThumbnail = mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                        Log.i("Adapter", "Name " + name + "\nsearchTerm: " + mSearchTerm);
                        String email = mEmails.get(id) != null ? mEmails.get(id) : "";
                        String number = mNumbers.get(id) != null ? mNumbers.get(id) : "";
                        if (name.toLowerCase().contains(mSearchTerm.toLowerCase()) ||
                                email.toLowerCase().contains(mSearchTerm.toLowerCase()) ||
                                number.toLowerCase().contains(mSearchTerm.toLowerCase()))
                            filteredMatrixC.addRow(new Object[]{id, name, mEmails.get(id), mNumbers.get(id), contactThumbnail});//, email, number});

                    } while (mCursor.moveToNext());
                    mCursor.close();
                    mFilteredCursor = new MergeCursor(new Cursor[]{filteredMatrixC, null});
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredCursor;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredCursor = (MergeCursor) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactAdapterOnClickHandler {
        void onClick(Uri contactUri, String ContactName);
    }

    public ContactAdapter(Context context,
                          ContactAdapterOnClickHandler onClickHandler) {

        mOnClickHandler = onClickHandler;
        mContext = context;

    }

    public void swapCursors(Cursor data, HashMap<String, String> emails,
                            HashMap<String, String> numbers) {
        mEmails = emails;
        mNumbers = numbers;
        mCursor = data;
        mFilteredCursor = new MergeCursor(new Cursor[]{mCursor, null});

        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mFilteredCursor.moveToPosition(position);

        holder.itemView.setSelected(
                mContext.getResources().getBoolean(R.bool.has_two_panes) &&
                        selectedPos == position);

        String contactName = mFilteredCursor.getString(mFilteredCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY));
        String contactId = mFilteredCursor.getString(ContactsFragment.INDEX_CONTACT_ID);
        String contactThumbnail = mFilteredCursor.getString(mFilteredCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));

        String contactEmail = mEmails.get(contactId);
        // mFilteredCursor.getString(mFilteredCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS));
        String contactNumber = mNumbers.get(contactId);


        holder.mContactName.setText(contactName);
        holder.mContactEmail.setText(contactEmail);
        holder.mContactNumber.setText(contactNumber);

        Picasso.with(mContext).load(contactThumbnail).
                centerCrop().fit().placeholder(R.drawable.ic_contact_picture).error(R.drawable.ic_contact_picture).into(holder.mIcon);

        String fullName = contactName;

        if (mSearchTerm != null && !mSearchTerm.isEmpty()) {
            contactThumbnail = mFilteredCursor.getString(mFilteredCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
            holder.mContactEmail.setVisibility(View.VISIBLE);
            holder.mContactNumber.setVisibility(View.VISIBLE);
            int startPos = fullName.toLowerCase(Locale.US).indexOf(mSearchTerm.toLowerCase(Locale.US));
            int endPos = startPos + mSearchTerm.length();
            Log.i("ContactAdapter", "thumb: " + contactThumbnail + "\nstartpos: " + startPos + "\nendpos: " + endPos);
            if (startPos != -1) {
                Spannable spannable = new SpannableString(fullName);
                int color = ContextCompat.getColor(mContext, R.color.colorAccent);
                ColorStateList accentColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{color});
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, accentColor, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.mContactName.setText(spannable);
                Picasso.with(mContext).load(contactThumbnail).
                        centerCrop().fit().placeholder(R.drawable.ic_contact_picture).error(R.drawable.ic_contact_picture).into(holder.mIcon);
            } else {
                holder.mContactName.setText(fullName);
                Picasso.with(mContext).load(contactThumbnail).
                        centerCrop().fit().placeholder(R.drawable.ic_contact_picture).error(R.drawable.ic_contact_picture).into(holder.mIcon);

            }
        }/* else {
            holder.mContactEmail.setVisibility(View.GONE);
            holder.mContactNumber.setVisibility(View.GONE);
        }*/
            /*e
            contactThumbnail = mFilteredCursor.getString(4);
            holder.mContactName.setText(fullName);
            Picasso.with(mContext).load(contactThumbnail).
                    centerCrop().fit().placeholder(R.drawable.ic_contact_picture).error(R.drawable.ic_contact_picture).into(holder.mIcon);

        }*/

    }

    @Override
    public int getItemCount() {
        return mFilteredCursor == null ? 0 : mFilteredCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.list_item_contact_name)
        TextView mContactName;
        @BindView(R.id.list_item_contact_email)
        TextView mContactEmail;
        @BindView(R.id.list_item_contact_number)
        TextView mContactNumber;
        @BindView(android.R.id.icon)
        de.hdodenhof.circleimageview.CircleImageView mIcon; //QuickContactBadge mIcon;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setClickable(true);
            view.setOnClickListener(this);
        }

        // Click a contact - send uri / id / (all info bundle)?
        @Override
        public void onClick(View view) {
            if (mFilteredCursor.isClosed()) return;
            mFilteredCursor.moveToPosition(getAdapterPosition());
            String contactName = mFilteredCursor.getString(mFilteredCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            long contactId = mFilteredCursor.getLong(ContactsFragment.INDEX_EMAIL_CONTACT_ID);

            Uri contactUri = Uri.withAppendedPath(
                    ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
            selectedPos = getAdapterPosition();
            notifyDataSetChanged();

            mOnClickHandler.onClick(contactUri, contactName);
        }
    }
}
