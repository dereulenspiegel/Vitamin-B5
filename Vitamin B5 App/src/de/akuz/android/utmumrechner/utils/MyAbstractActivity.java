package de.akuz.android.utmumrechner.utils;

import de.akuz.android.utmumrechner.AddLocationActivity;
import de.akuz.android.utmumrechner.CalculateDistance;
import de.akuz.android.utmumrechner.ListTargetLocations;
import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.UTMUmrechnerActivity;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MyAbstractActivity extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id){
		case R.id.action_menu_convert:
			startMyActivity(UTMUmrechnerActivity.class);
			return true;
		case R.id.action_menu_distance:
			startMyActivity(CalculateDistance.class);
			return true;
		case R.id.action_menu_list_locations:
			startMyActivity(ListTargetLocations.class);
			return true;
		case R.id.action_menu_add_location:
			startMyActivity(AddLocationActivity.class);
		}
		
		return false;
	}
	
	private void startMyActivity(Class<? extends Activity> activityClass){
		Intent i = new Intent(this,activityClass);
		startActivity(i);
	}
}
