package project2.mobile.fsu.edu.kachet;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

// TODO: Hook up smaller FAB to the form layout

public class MainActivity extends TabActivity {

    TabHost tabHost;
    TabHost.TabSpec spec;
    Intent tabIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = this.getTabHost();

        tabIntent = new Intent(this, KacheMap.class);
        spec = tabHost.newTabSpec("Kachet_map").setIndicator("Kachet", null)
                .setContent(tabIntent);
        tabHost.addTab(spec);

        tabIntent = new Intent(this, PostKachet.class);
        spec = tabHost.newTabSpec("send_media").setIndicator("Send", null)
                .setContent(tabIntent);
        tabHost.addTab(spec);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

            }
        });
    }
}
