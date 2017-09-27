package com.roberts.adrian.androidcodetestadrianroberts.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.roberts.adrian.androidcodetestadrianroberts.ContactDetailsFragment;
import com.roberts.adrian.androidcodetestadrianroberts.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context mContext;
    private Cursor mCursor;


    public AddressAdapter(Context context) {
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
        final String address = mCursor.getString(ContactDetailsFragment.INDEX_ADDRESS_FORMATTED);
        int type = mCursor.getInt(ContactDetailsFragment.INDEX_ADDRESS_TYPE);

        CharSequence label = ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabel(mContext.getResources(),
                type, null);
        if (address.isEmpty()) {
            holder.mAddressButton.setVisibility(View.GONE);
            return;
        }
        holder.mDetailHeader.setText(label);
        holder.mDetailItem.setText(address);
        holder.mAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String GEO_URI_PREFIX = "geo:0,0?q=";
                Uri addressUri = Uri.parse(GEO_URI_PREFIX + Uri.encode(address));

                final Intent viewIntent =
                        new Intent(Intent.ACTION_VIEW, addressUri);

                final PackageManager packageManager = mContext.getPackageManager();

                // Checks for an activity that can handle this intent. Preferred in this
                // case over Intent.createChooser() as it will still let the user choose
                // a default (or use a previously set default) for geo Uris.
                if (packageManager.resolveActivity(
                        viewIntent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                    mContext.startActivity(viewIntent);
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
        ImageButton mAddressButton;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
