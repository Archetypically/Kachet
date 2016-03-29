package project2.mobile.fsu.edu.kachet;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FocusFragment extends DialogFragment {
    private static final String NAME_PARAM = "name";
    private static final String DATE_PARAM = "date";
    private static final String MSG_PARAM = "msg";
    private static final String PIC_PARAM = "pic";

    private String name;
    private String date;
    private String msg;
    private String pic;

    public FocusFragment() {
        // Required empty public constructor
    }

    public static FocusFragment newInstance(String name, String date, String msg, String pic) {
        FocusFragment fragment = new FocusFragment();
        Bundle args = new Bundle();
        args.putString(NAME_PARAM, name);
        args.putString(DATE_PARAM, date);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_focus, container, false);
        try {
            ((TextView) v.findViewById(R.id.name)).setText(this.name);
            ((TextView) v.findViewById(R.id.message)).setText(this.msg);
            ((TextView) v.findViewById(R.id.date)).setText(this.date);
        }
        catch (NullPointerException npe){
            npe.printStackTrace();
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
    }
}
