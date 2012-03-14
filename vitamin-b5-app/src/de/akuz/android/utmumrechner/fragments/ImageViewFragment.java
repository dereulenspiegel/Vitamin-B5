package de.akuz.android.utmumrechner.fragments;

import com.polites.android.GestureImageView;

import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.utils.StringUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class ImageViewFragment extends MyAbstractFragment {

	private GestureImageView imageView;

	private Uri imageUri;
	private Bitmap image;

	public final static String EXTRA_IMAGE_URI = "extra.image.uri";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("UTM", "Fragment: onCreate");
		if (getArguments() != null) {
			String uri = getArguments().getString(EXTRA_IMAGE_URI);
			if (!StringUtils.isEmtpy(uri)) {
				imageUri = Uri.parse(uri);
			}
		}
		setContentView(R.layout.image_view);
		// setRetainInstance(true);
	}

	@Override
	protected void initUIElements() {
		LinearLayout myLayout = (LinearLayout) findViewById(R.id.imageViewLinearLayout);
		myLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		imageView = (GestureImageView) findViewById(R.id.gestureImageView);
		imageView.setMaxScale(10.0f);
		imageView.setMinScale(0.1f);
		if (imageUri != null) {
			setImageUri(imageUri);
		}

	}

	public void setImageUri(Uri imageUri) {
		if (this.imageUri == null || !this.imageUri.equals(imageUri)) {
			Log.d("UTM", "Fragment: Setting image uri and decoding bitmap");
			this.imageUri = imageUri;
			image = BitmapFactory.decodeFile(imageUri.toString());
			imageView.setImageBitmap(image);
		} else if (imageUri != null && image != null) {
			Log.d("UTM", "Reusing old bitmap");
			imageView.setImageBitmap(image);
		}
	}

	@Override
	public void onDestroy() {
		Log.d("UTM","Fragment: onDestroy");
		if (image != null) {
			image.recycle();
			image = null;
		}
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

}
