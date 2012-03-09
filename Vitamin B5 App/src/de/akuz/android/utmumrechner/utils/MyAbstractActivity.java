package de.akuz.android.utmumrechner.utils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.akuz.android.utmumrechner.AddLocationActivity;
import de.akuz.android.utmumrechner.CalculateDistance;
import de.akuz.android.utmumrechner.Center;
import de.akuz.android.utmumrechner.ListTargetLocations;
import de.akuz.android.utmumrechner.R;

import de.akuz.android.utmumrechner.UTMUmrechnerActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MyAbstractActivity extends SherlockFragmentActivity implements
		OnNavigationListener {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
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
			return true;
		case android.R.id.home:
			startMyActivity(Center.class);
		}

		return false;
	}

	protected void startMyActivity(Class<? extends Activity> activityClass) {
		Intent i = new Intent(this, activityClass);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		ActionBar bar = getSupportActionBar();
		bar.setHomeButtonEnabled(true);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}
}
