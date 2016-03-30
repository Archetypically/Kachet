package project2.mobile.fsu.edu.kachet;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class KacheAdapter extends RecyclerView.Adapter<KacheAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    private ArrayList<KacheMessage> mMessages;

    public static class KacheMessage {
        protected String msg;
        protected String usr;
        protected Date date;
        protected String picture;
        protected int kacheId;

        public KacheMessage(String msg, String usr, Date date, String img, int id) {
            this.msg = msg;
            this.usr = usr;
            this.date = date;
            this.picture = img;
            this.kacheId = id;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mMsgView;
        TextView mUsrView;
        TextView mDateView;
        ImageView mPictureView;
        CardView mCardView;

        public ViewHolder(View v){
            super(v);
            mMsgView = (TextView) v.findViewById(R.id.message);
            mUsrView = (TextView) v.findViewById(R.id.name);
            mDateView = (TextView) v.findViewById(R.id.date);
            mPictureView = (ImageView) v.findViewById(R.id.picture);
            mCardView = (CardView) v.findViewById(R.id.card_view);
        }
    }

    public KacheAdapter(ArrayList<KacheMessage> messages) {
        //initializeData();
        mMessages = messages;
    }

    @Override
    public KacheAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.kache_card, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int pos) {
        KacheMessage tmp = mMessages.get(pos);
        String date = new SimpleDateFormat("MM/dd/yy - hh:mm:ss", Locale.US).format(tmp.date);

        holder.mMsgView.setText(tmp.msg);
        if(tmp.usr != null)
            holder.mUsrView.setText(tmp.usr);
        else
            holder.mUsrView.setText("Anonymous");
        holder.mDateView.setText(date);
        if(tmp.picture != null)
            holder.mPictureView.setImageResource(R.drawable.img_default);
        else
            holder.mPictureView.setVisibility(View.GONE);

        holder.mUsrView.setTransitionName("focus" + pos);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view, pos);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    private void initializeData() {
        Date dummy = new Date();

        mMessages = new ArrayList<>();
        mMessages.add(
                new KacheMessage("Cool!", "Evan", dummy, null, 1));
        mMessages.add(
                new KacheMessage("Wow!", "Tyler", dummy, "notnull", 1));
        mMessages.add(
                new KacheMessage("Amaz!", "BB", dummy, null, 1));
        mMessages.add(
                new KacheMessage("I wish to stay anonymous!", null, dummy, null, 1));
    }
}
