package project2.mobile.fsu.edu.kachet;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
    public static final String name_t = "name_t";
    public static final String date_t = "date_t";
    public static final String msg_t = "msg_t";
    public static final String av_t = "av_t";
    public static final String pic_t = "pic_t";

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
        ImageView mAvatarView;
        CardView mCardView;

        public ViewHolder(View v){
            super(v);
            mMsgView = (TextView) v.findViewById(R.id.message);
            mUsrView = (TextView) v.findViewById(R.id.name);
            mDateView = (TextView) v.findViewById(R.id.date);
            mPictureView = (ImageView) v.findViewById(R.id.picture);
            mAvatarView = (ImageView) v.findViewById(R.id.avatar);
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

        if(tmp.usr == null || tmp.usr.equalsIgnoreCase(""))
            holder.mUsrView.setText("Anonymous");
        else
            holder.mUsrView.setText(tmp.usr);

        holder.mUsrView.setTransitionName(name_t + pos);

        holder.mMsgView.setText(tmp.msg);
        holder.mMsgView.setTransitionName(msg_t + pos);

        holder.mDateView.setText(date);
        holder.mDateView.setTransitionName(date_t + pos);

        holder.mAvatarView.setTransitionName(av_t + pos);

        if(tmp.picture != null) {
            holder.mPictureView.setVisibility(View.VISIBLE);
            holder.mPictureView.setImageResource(R.mipmap.splash);
            holder.mPictureView.setTransitionName(pic_t + pos);
        }
        else
            holder.mPictureView.setVisibility(View.GONE);

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }
}
