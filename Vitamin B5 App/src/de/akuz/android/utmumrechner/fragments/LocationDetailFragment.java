package de.akuz.android.utmumrechner.fragments;

import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.data.LocationDatabase;
import de.akuz.android.utmumrechner.data.TargetLocation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationDetailFragment extends MyAbstractFragment {

	private TextView textViewName;
	private TextView textViewCoordinates;
	private TextView textViewDescription;
	private ImageView imageView;

	private LocationDatabase db;

	@Override
	protected void initUIElements() {
		textViewName = (TextView) findViewById(R.id.textViewName);
		textViewCoordinates = (TextView) findViewById(R.id.textViewCoordinates);
		textViewDescription = (TextView) findViewById(R.id.textViewDescription);
		imageView = (ImageView) findViewById(R.id.imageView1);

		Intent i = getActivity().getIntent();
		if (i != null) {
			long id = i.getLongExtra("id", -1);
			if(id > -1){
				updateContent(id);
			}
		}

	}

	public void updateContent(long id) {
		db.open();
		TargetLocation location = db.getLocationById(id);
		db.close();

		textViewName.setText(location.getName());
		textViewCoordinates.setText(location.getMgrsCoordinate());
		textViewDescription.setText(location.getDescription());
		if (location.getPictureUrl() != null) {
			Bitmap image = BitmapFactory.decodeFile(location.getPictureUrl());
			imageView.setImageBitmap(image);
		} else {
			imageView.setImageBitmap(null);
			imageView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_location_detail);
		db = new LocationDatabase(getActivity());
	}

}
