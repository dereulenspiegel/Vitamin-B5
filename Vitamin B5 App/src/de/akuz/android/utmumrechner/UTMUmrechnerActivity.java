package de.akuz.android.utmumrechner;

import android.os.Bundle;
import de.akuz.android.utmumrechner.utils.MyAbstractActivity;

public class UTMUmrechnerActivity extends MyAbstractActivity  {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.convert_coordinates);
	}

	
}