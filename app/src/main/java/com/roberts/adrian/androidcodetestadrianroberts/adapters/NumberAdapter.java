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


public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.ViewHolder> {

    private Context mContext;
    private Cursor mCursor;


    public NumberAdapter(Context context) {
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
        final String number = mCursor.getString(ContactDetailsFragment.INDEX_PHONE_NUMBER);
        int type = mCursor.getInt(ContactDetailsFragment.INDEX_PHONE_TYPE);
        String numberId = mCursor.getString(ContactDetailsFragment.INDEX_PHONE_ID);
        CharSequence label = ContactsContract.CommonDataKinds.Phone.getTypeLabel(mContext.getResources(),
                type, null);
        Log.i("BIND NUMBER", "label, type " + label + " " + type);
        holder.mDetailHeader.setText(label);
        holder.mDetailItem.setText(number);
        holder.mCallButton.setImageResource(R.drawable.ic_action_dial);
        holder.mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }


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
        ImageButton mCallButton;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
