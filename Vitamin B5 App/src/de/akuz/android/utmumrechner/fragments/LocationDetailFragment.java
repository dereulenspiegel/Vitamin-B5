package de.akuz.android.utmumrechner.fragments;

import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.data.LocationDatabase;
import de.akuz.android.utmumrechner.data.TargetLocation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationDetailFragment extends MyAbstractFragment implements OnClickListener{
	
	public interface LocationDetailListener{
		
		public void showPicture(TargetLocation location);
		public void showOnMap(TargetLocation location);
		
	}

	private TextView textViewName;
	private TextView textViewCoordinates;
	private TextView textViewDescription;
	private ImageView imageView;

	private LocationDatabase db;
	
	private TargetLocation location;
	
	private Uri imageUri;
	
	private LocationDetailListener listener;

	@Override
	protected void initUIElements() {
		textViewName = (TextView) findViewById(R.id.textViewName);
		textViewCoordinates = (TextView) findViewById(R.id.textViewCoordinates);
		textViewDescription = (TextView) findViewById(R.id.textViewDescription);
		imageView = (ImageView) findViewById(R.id.imageView1);
		
		imageView.setOnClickListener(this);
		textViewCoordinates.setOnClickListener(this);
		clearFields();
		Intent i = getActivity().getIntent();
		if (i != null) {
			long id = i.getLongExtra("id", -1);
			if(id > -1){
				updateContent(id);
			}
		}
	}
	
	public void clearFields(){
		textViewCoordinates.setText("");
		textViewDescription.setText("");
		textViewName.setText("");
		imageView.setImageBitmap(null);
		imageView.setVisibility(View.GONE);
	}

	public void updateContent(long id) {
		db.open();
		location = db.getLocationById(id);
		db.close();

		textViewName.setText(location.getName());
		textViewCoordinates.setText(location.getMgrsCoordinate());
		textViewDescription.setText(location.getDescription());
		updateImage(location.getPictureUrl());
	}
	
	private void updateImage(String url){
		if (url != null) {
			imageUri = Uri.parse(url);
			Bitmap image = BitmapFactory.decodeFile(url);
			imageView.setImageBitmap(image);
			imageView.setVisibility(View.VISIBLE);
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

	@Override
	public void onClick(View v) {
		if(v.getId() == imageView.getId()){
			showFullPicture();
		}
		if(v.getId() == textViewCoordinates.getId()){
			showCoordinatesOnMap();
		}
		
	}
	
	private void showCoordinatesOnMap(){
//		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//		MapViewFragment mapFragment = new MapViewFragment();
//		mapFragment.show(ft, "mapView");
	}
	
	private void showFullPicture(){
		listener.showPicture(location);
	}
	
	public void setListener(LocationDetailListener listener){
		this.listener = listener;
	}
	
	public void unSetListener(){
		this.listener = null;
	}

}
