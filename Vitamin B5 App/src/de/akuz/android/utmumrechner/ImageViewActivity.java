package de.akuz.android.utmumrechner;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import de.akuz.android.utmumrechner.fragments.ImageViewFragment;
import de.akuz.android.utmumrechner.utils.StringUtils;

public class ImageViewActivity extends FragmentActivity {
	
	private ImageViewFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.image_view_activity);
		getSupportActionBar().hide();
		fragment = (ImageViewFragment)getSupportFragmentManager().findFragmentById(R.id.image_view_fragment);
		if(getImageUri() != null){
			fragment.setImageUri(getImageUri());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(getImageUri() != null){
			fragment.setImageUri(getImageUri());
		}
	}
	
	private Uri getImageUri(){
		if(getIntent().getExtras() != null){
			Bundle extras = getIntent().getExtras();
			String uri = extras.getString(ImageViewFragment.EXTRA_IMAGE_URI);
			if(!StringUtils.isEmtpy(uri)){
				return Uri.parse(uri);
			}
		}
		return null;
	}

}
