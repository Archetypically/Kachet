package project2.mobile.fsu.edu.kachet;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class KacheAdapter extends RecyclerView.Adapter<KacheAdapter.ViewHolder> {
    private ArrayList<KacheMessage> mMessages;

    private class KacheMessage {
        protected String msg;
        protected String usr;
        protected String date;
        protected String picture;

        public KacheMessage (String msg, String usr, String date, String img){
            this.msg = msg;
            this.usr = usr;
            this.date = date;
            this.picture = img;
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
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

    // Provide a suitable constructor (depends on the kind of dataset)
    public KacheAdapter(Bundle data) {
        Log.i("ADAPTER", "CONSTRUCTING");
        initializeData();
    }

    // Create new views (invoked by the layout manager)
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

    // This method creates an ArrayList that has three Person objects
    // Checkout the project associated with this tutorial on Github if
    // you want to use the same images.
    private void initializeData(){
        mMessages = new ArrayList<>();
        mMessages.add(new KacheMessage("Cool!", "Evan", "03/27/16", null));
        mMessages.add(new KacheMessage("Wow!", "Tyler", "03/27/16", "notnull"));
        mMessages.add(new KacheMessage("Amaz!", "BB", "03/27/16", null));
    }
}
