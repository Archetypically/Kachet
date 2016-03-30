package project2.mobile.fsu.edu.kachet;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;

/**
 * Created by Raidel on 3/30/16.
 */
public class Send_Media extends TabActivity {

    TabHost tabHost;
    TabHost.TabSpec spec;
    Intent tabIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_activity);

        tabHost = this.getTabHost();

        tabIntent = new Intent(this, Send_Message.class);
        spec = tabHost.newTabSpec("send_message").setIndicator(null, getResources().getDrawable(R.drawable.message_icon))
                .setContent(tabIntent);
        tabHost.addTab(spec);

        tabIntent = new Intent(this, Camera.class);
        spec = tabHost.newTabSpec("send_picture").setIndicator(null, getResources().getDrawable(R.drawable.camera_icon))
                .setContent(tabIntent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Toast.makeText(getApplicationContext(), "Tab ID = " + tabId,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

