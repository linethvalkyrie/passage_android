package com.passageproject;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PassageRecyclerViewAdapter extends RecyclerView.Adapter<PassageRecyclerViewAdapter.ViewHolder> {

    private List<PassageModel> mValues;
    private final PassageModelListener mListener;

    public PassageRecyclerViewAdapter(List<PassageModel> items, PassageModelListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getPassageTitle());
        holder.mDateView.setText(mValues.get(position).getPassageCategory());
        holder.mContentView.setText(mValues.get(position).getPassageAuthor());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onPassageClick(holder.mItem);
                }
            }
        });
    }

    public void swap(List<PassageModel> list){
        if (mValues != null) {
            mValues.clear();
            mValues = list;
        }
        else {
            mValues = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mIdView;
        public final TextView mDateView;
        public final TextView mContentView;
        public PassageModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.imgPic);
            mIdView = (TextView) view.findViewById(R.id.tvCreatedBy);
            mDateView = (TextView) view.findViewById(R.id.tvCreatedTime);
            mContentView = (TextView) view.findViewById(R.id.tvMessage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public interface PassageModelListener {
        void onPassageClick(PassageModel passageModel);
    }
}
