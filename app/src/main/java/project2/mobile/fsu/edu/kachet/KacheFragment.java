package project2.mobile.fsu.edu.kachet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class KacheFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private KacheAdapter mKacheAdapter;
    private LinearLayoutManager mRecyclerManager;

    public KacheFragment() {
        mKacheAdapter = new KacheAdapter(null);
        mRecyclerManager = new LinearLayoutManager(getContext());
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
        mRecyclerView.setLayoutManager(mRecyclerManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mKacheAdapter);

        mKacheAdapter.setOnItemClickListener(new KacheAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                TextView nameText = (TextView) v.findViewById(R.id.name);
                String name = (nameText).getText().toString();
                String date = ((TextView) v.findViewById(R.id.date)).getText().toString();
                Date ts = null;
                try {
                    ts = new SimpleDateFormat("MM/dd/yy - hh:mm:ss", Locale.US).parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String msg = ((TextView) v.findViewById(R.id.message)).getText().toString();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                FocusFragment mFocusFragment = FocusFragment.newInstance(name, ts, msg, null);
                fragmentManager.beginTransaction()
                        .add(android.R.id.content, mFocusFragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
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

