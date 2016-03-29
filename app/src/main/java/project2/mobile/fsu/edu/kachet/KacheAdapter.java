package project2.mobile.fsu.edu.kachet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class KacheAdapter extends RecyclerView.Adapter<KacheAdapter.ViewHolder> {
    private ArrayList<KacheMessage> mMessages;

    private class KacheMessage {
        protected String msg;
        protected String usr;
        protected String date;
        protected String picture;
        protected int kacheId;

        public KacheMessage (String msg, String usr, String date, String img, int id){
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

        public ViewHolder(View v){
            super(v);
            mMsgView = (TextView) v.findViewById(R.id.message);
            mUsrView = (TextView) v.findViewById(R.id.name);
            mDateView = (TextView) v.findViewById(R.id.date);
            mPictureView = (ImageView) v.findViewById(R.id.picture);
        }
    }

    public KacheAdapter(Bundle data) {
        Log.i("ADAPTER", "CONSTRUCTING");
        initializeData();
    }

    @Override
    public KacheAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.kache_card, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        KacheMessage tmp = mMessages.get(pos);

        holder.mMsgView.setText(tmp.msg);
        if(tmp.usr != null)
            holder.mUsrView.setText(tmp.usr);
        holder.mDateView.setText(tmp.date);
        if(tmp.picture != null)
            holder.mPictureView.setImageResource(R.drawable.img_default);
        else
            holder.mPictureView.setVisibility(View.GONE);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    private void initializeData() {
        String date = new SimpleDateFormat("EEEE, MMMM d | hh:mm:ss z").format(new Date());

        mMessages = new ArrayList<>();
        mMessages.add(
                new KacheMessage("Cool!", "Evan", date, null, 1));
        mMessages.add(
                new KacheMessage("Wow!", "Tyler", date, "notnull", 1));
        mMessages.add(
                new KacheMessage("Amaz!", "BB", date, null, 1));
        mMessages.add(
                new KacheMessage("I wish to stay anonymous!", null, date, null, 1));
    }
}
