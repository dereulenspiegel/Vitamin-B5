package de.akuz.android.utmumrechner.fragments;

import com.polites.android.GestureImageView;

import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.utils.StringUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class ImageViewFragment extends MyAbstractFragment {

	private GestureImageView imageView;

	private Uri imageUri;

	public final static String EXTRA_IMAGE_URI = "extra.image.uri";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			String uri = getArguments().getString(EXTRA_IMAGE_URI);
			if (!StringUtils.isEmtpy(uri)) {
				imageUri = Uri.parse(uri);
			}
		}
		setContentView(R.layout.image_view);
	}

	@Override
	protected void initUIElements() {
		LinearLayout myLayout = (LinearLayout) findViewById(R.id.imageViewLinearLayout);
		myLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageView = (GestureImageView) findViewById(R.id.gestureImageView);
		imageView.setMaxScale(10.0f);
		imageView.setMinScale(0.1f);
		if (imageUri != null) {
			setImageUri(imageUri);
		}

	}

	public void setImageUri(Uri imageUri) {
		this.imageUri = imageUri;
		Bitmap image = BitmapFactory.decodeFile(imageUri.toString());
		imageView.setImageBitmap(image);
		imageView.setMinimumHeight(2000);
	}

	@Override
	public void setArguments(Bundle arguments) {
		super.setArguments(arguments);
		String uri = getArguments().getString(EXTRA_IMAGE_URI);
		if (!StringUtils.isEmtpy(uri)) {
			imageUri = Uri.parse(uri);
		}
		setImageUri(imageUri);
	}

}
