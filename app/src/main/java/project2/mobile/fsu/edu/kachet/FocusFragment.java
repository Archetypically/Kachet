package project2.mobile.fsu.edu.kachet;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FocusFragment extends DialogFragment {
    private static final String NAME_PARAM = "name";
    private static final String DATE_PARAM = "date";
    private static final String MSG_PARAM = "msg";
    private static final String PIC_PARAM = "pic";

    private String name;
    private String date;
    private String msg;
    private String pic;

    private String nameTId;
    private String dateTId;
    private String avatarTId;
    private String msgTId;
    private String picTId;

    public FocusFragment() {
        // Required empty public constructor
    }

    public static FocusFragment newInstance(String name, Date date, String msg, String pic) {
        String ts = null;
        if(date != null)
             ts = new SimpleDateFormat("EEEE, MMMM dd, yyyy | hh:mm:ss", Locale.US).format(date);
        FocusFragment fragment = new FocusFragment();
        Bundle args = new Bundle();
        args.putString(NAME_PARAM, name);
        args.putString(DATE_PARAM, ts);
        args.putString(MSG_PARAM, msg);
        args.putString(PIC_PARAM, pic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(NAME_PARAM);
            date = getArguments().getString(DATE_PARAM);
            msg = getArguments().getString(MSG_PARAM);
            pic = getArguments().getString(PIC_PARAM);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_focus, container, false);

        TextView nameView = (TextView) v.findViewById(R.id.name);
        TextView msgView = (TextView) v.findViewById(R.id.message);
        TextView dateView =(TextView) v.findViewById(R.id.date);
        ImageView avatarView = (ImageView) v.findViewById(R.id.avatar);
        ImageView picView = (ImageView) v.findViewById(R.id.picture);

        try {
            nameView.setText(this.name);
            nameView.setTransitionName(nameTId);
            msgView.setText(this.msg);
            msgView.setTransitionName(msgTId);
            dateView.setText(this.date);
            dateView.setTransitionName(dateTId);
            avatarView.setTransitionName(avatarTId);
            picView.setVisibility(View.GONE);
        }
        catch (NullPointerException npe){
            npe.printStackTrace();
        }

        return v;
    }

    public void setNameTId(String id){
        this.nameTId = id;
    }

    public void setDateTId(String id){
        this.dateTId = id;
    }

    public void setAvatarTId(String id){
        this.avatarTId = id;
    }

    public void setMsgTId(String id){
        this.msgTId = id;
    }
}
