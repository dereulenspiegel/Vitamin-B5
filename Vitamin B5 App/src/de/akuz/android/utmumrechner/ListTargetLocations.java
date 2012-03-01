package de.akuz.android.utmumrechner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListAdapter;
import de.akuz.android.utmumrechner.utils.MyAbstractActivity;
import de.akuz.android.utmumrechner.data.TargetLocation;
import de.akuz.android.utmumrechner.fragments.LocationDetailFragment;
import de.akuz.android.utmumrechner.fragments.LocationListFragment;

public class ListTargetLocations extends MyAbstractActivity implements LocationListFragment.Callback {
	
	private LocationListFragment locationListFragment;
	private LocationDetailFragment locationDetailFragment;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_list);
		initUIElements();
	}
	
	private void initUIElements(){
		locationDetailFragment = (LocationDetailFragment)getSupportFragmentManager().findFragmentById(R.id.details_fragment);
		locationListFragment = (LocationListFragment)getSupportFragmentManager().findFragmentById(R.id.list_fragment);
		
		locationListFragment.addCallback(this);
		
		ListAdapter adapter = locationListFragment.getListAdapter();
		if(adapter.getCount() > 0 && locationDetailFragment != null){
			TargetLocation firstLocation = (TargetLocation) adapter.getItem(0);
			locationDetailFragment.updateContent(firstLocation.getId());
		}
	}

	@Override
	public void itemSelected(long id) {
		if(locationDetailFragment == null){
			Intent i = new Intent(this,LocationDetailActivity.class);
			i.putExtra("id", id);
			startActivity(i);
		} else {
			locationDetailFragment.updateContent(id);
		}
		
	}

}
