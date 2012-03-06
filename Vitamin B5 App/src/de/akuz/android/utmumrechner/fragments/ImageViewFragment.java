package de.akuz.android.utmumrechner.fragments;

import com.polites.android.GestureImageView;

import de.akuz.android.utmumrechner.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class ImageViewFragment extends MyAbstractFragment {
	
	private GestureImageView imageView;
	
	private Uri imageUri;
	
	public final static String EXTRA_IMAGE_URI = "extra.image.uri";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageUri = Uri.parse(getArguments().getString(EXTRA_IMAGE_URI));
	}

	@Override
	protected void initUIElements() {
		imageView = (GestureImageView) findViewById(R.id.gestureImageView);
		imageView.setMaxScale(10.0f);
		imageView.setMinScale(0.1f);
		Bitmap image = BitmapFactory.decodeFile(imageUri.toString());
		imageView.setImageBitmap(image);
		
	}


}
