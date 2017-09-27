package com.roberts.adrian.androidcodetestadrianroberts.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.roberts.adrian.androidcodetestadrianroberts.ContactDetailsFragment;
import com.roberts.adrian.androidcodetestadrianroberts.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.id.message;


public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.ViewHolder> {

    private Context mContext;
    private Cursor mCursor;


    public EmailAdapter(Context context) {
        mContext = context;
    }

    public void swapCursor(Cursor data) {
        mCursor = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        final String address = mCursor.getString(ContactDetailsFragment.INDEX_EMAIL_ADDRESS);
        int type = mCursor.getInt(ContactDetailsFragment.INDEX_EMAIL_TYPE);
        CharSequence label = ContactsContract.CommonDataKinds.Email.getTypeLabel(mContext.getResources(),
                type, null);
        Log.i("BIND EMAIL", "label, type " + label + " " + type);
        holder.mDetailHeader.setText(label);
        holder.mDetailItem.setText(address);
        holder.mEmailButton.setImageResource(R.drawable.ic_action_mailto);
        holder.mEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", address, null));
                intent.putExtra(Intent.EXTRA_TEXT, message);
                mContext.startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contact_detail_header)
        TextView mDetailHeader;
        @BindView(R.id.contact_detail_item)
        TextView mDetailItem;
        @BindView(R.id.button_view_address)
        ImageButton mEmailButton;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
