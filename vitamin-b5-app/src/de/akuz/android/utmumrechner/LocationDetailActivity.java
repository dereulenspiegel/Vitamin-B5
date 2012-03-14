package de.akuz.android.utmumrechner;

import android.content.Intent;
import android.os.Bundle;
import de.akuz.android.utmumrechner.data.TargetLocation;
import de.akuz.android.utmumrechner.fragments.ImageViewFragment;
import de.akuz.android.utmumrechner.fragments.LocationDetailFragment;
import de.akuz.android.utmumrechner.fragments.LocationDetailFragment.LocationDetailListener;
import de.akuz.android.utmumrechner.utils.MyAbstractActivity;

public class LocationDetailActivity extends MyAbstractActivity implements LocationDetailListener{
	
	private LocationDetailFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.location_detail);
		fragment = (LocationDetailFragment)getSupportFragmentManager().findFragmentById(R.id.details_fragment);
		fragment.setListener(this);
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
