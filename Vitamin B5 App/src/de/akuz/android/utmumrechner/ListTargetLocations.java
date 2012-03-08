package de.akuz.android.utmumrechner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListAdapter;
import de.akuz.android.utmumrechner.utils.MyAbstractActivity;
import de.akuz.android.utmumrechner.data.TargetLocation;
import de.akuz.android.utmumrechner.fragments.ImageViewFragment;
import de.akuz.android.utmumrechner.fragments.LocationDetailFragment;
import de.akuz.android.utmumrechner.fragments.LocationListFragment;

public class ListTargetLocations extends MyAbstractActivity implements LocationListFragment.Callback, LocationDetailFragment.LocationDetailListener {
	
	private LocationListFragment locationListFragment;
	private LocationDetailFragment locationDetailFragment;
	
	private long currentShownId;
	
	
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
		
		if(locationDetailFragment != null){
			locationDetailFragment.setListener(this);
		}
		
		ListAdapter adapter = locationListFragment.getListAdapter();
		if(adapter.getCount() > 0 && locationDetailFragment != null){
			TargetLocation firstLocation = (TargetLocation) adapter.getItem(0);
			currentShownId = firstLocation.getId();
			locationDetailFragment.updateContent(firstLocation.getId());
		}
	}

	@Override
	public void itemSelected(long id) {
		currentShownId = id;
		if(locationDetailFragment == null){
			Intent i = new Intent(this,LocationDetailActivity.class);
			i.putExtra("id", id);
			startActivity(i);
		} else {
			locationDetailFragment.updateContent(id);
		}
		
	}

	@Override
	public void itemDeleted(long id) {
		if(id == currentShownId && locationDetailFragment != null){
			locationDetailFragment.clearFields();
		}
		
	}

	@Override
	public void showPicture(TargetLocation location) {
		Intent i = new Intent(this, ImageViewActivity.class);
		i.putExtra(ImageViewFragment.EXTRA_IMAGE_URI, location.getPictureUrl());
		startActivity(i);
		
	}

	@Override
	public void showOnMap(TargetLocation location) {
		// TODO Auto-generated method stub
		
	}

}
