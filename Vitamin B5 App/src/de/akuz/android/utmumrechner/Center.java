package de.akuz.android.utmumrechner;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class Center extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TabHost tabHost = getTabHost();

		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		intent = new Intent(this, UTMUmrechnerActivity.class);
		spec = tabHost.newTabSpec("convert").setIndicator("Umrechnen")
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent(this,CalculateDistance.class);
		spec = tabHost.newTabSpec("distance").setIndicator("Distanz")
				.setContent(intent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(0);

	}

}
