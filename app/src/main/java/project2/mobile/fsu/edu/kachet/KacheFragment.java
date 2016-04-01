package project2.mobile.fsu.edu.kachet;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class KacheFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private KacheAdapter mKacheAdapter;

    public KacheFragment() {
        mKacheAdapter = new KacheAdapter(null);
    }

    public static KacheFragment newInstance(KacheAdapter adapter) {
        KacheFragment fragment = new KacheFragment();
        fragment.setAdapter(adapter);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_kache, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.kache_recycler);

        LinearLayoutManager mRecyclerManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mRecyclerManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mKacheAdapter);

        mKacheAdapter.setOnItemClickListener(new KacheAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                TextView nameText = (TextView) v.findViewById(R.id.name);
                TextView dateText = (TextView) v.findViewById(R.id.date);
                TextView msgText = (TextView) v.findViewById(R.id.message);
                ImageView avView = (ImageView) v.findViewById(R.id.avatar);

                String name = nameText.getText().toString();
                String date = dateText.getText().toString();
                Date ts = null;
                try {
                    ts = new SimpleDateFormat("MM/dd/yy - hh:mm:ss", Locale.US).parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String msg = msgText.getText().toString();

                FocusFragment mFocusFragment = FocusFragment.newInstance(name, ts, msg, null);
                mFocusFragment.setNameTId(nameText.getTransitionName());
                mFocusFragment.setDateTId(dateText.getTransitionName());
                mFocusFragment.setMsgTId(msgText.getTransitionName());
                mFocusFragment.setAvatarTId(avView.getTransitionName());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setSharedElementReturnTransition(
                            TransitionInflater.from(
                                    getActivity()).inflateTransition(R.transition.focus_trans));
                    setExitTransition(new Fade());
                    mFocusFragment.setSharedElementEnterTransition(
                            TransitionInflater.from(
                                    getActivity()).inflateTransition(R.transition.focus_trans));
                    mFocusFragment.setEnterTransition(new Fade());
                }

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(android.R.id.content, mFocusFragment)
                        .addToBackStack(null)
                        .addSharedElement(nameText, nameText.getTransitionName())
                        .addSharedElement(dateText, dateText.getTransitionName())
                        .addSharedElement(msgText, msgText.getTransitionName())
                        .addSharedElement(avView, avView.getTransitionName())
                        .commit();
            }
        });

        return v;
    }

    public void setAdapter(KacheAdapter adapter){
        if(adapter != null){
            this.mKacheAdapter = adapter;
        }
    }
}

