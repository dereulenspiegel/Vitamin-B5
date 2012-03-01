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

	private TextView textViewName;
	private TextView textViewCoordinates;
	private TextView textViewDescription;
	private ImageView imageView;

	private LocationDatabase db;
	
	private Uri imageUri;

	@Override
	protected void initUIElements() {
		textViewName = (TextView) findViewById(R.id.textViewName);
		textViewCoordinates = (TextView) findViewById(R.id.textViewCoordinates);
		textViewDescription = (TextView) findViewById(R.id.textViewDescription);
		imageView = (ImageView) findViewById(R.id.imageView1);
		imageView.setOnClickListener(this);
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
		TargetLocation location = db.getLocationById(id);
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
//			showFullPicture();
		}
		
	}
	
	private void showFullPicture(){
		//TODO Fix the bug causing the image viewer to crash (probably permissions)
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		i.setDataAndType(imageUri, "image/*");
		startActivity(i);
	}

}
