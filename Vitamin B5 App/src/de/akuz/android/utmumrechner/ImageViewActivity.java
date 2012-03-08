package de.akuz.android.utmumrechner;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(getImageUri() != null){
			Log.d("UTM","Activity Setting image uri in onResume:"+getImageUri().toString());
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
		if(getLastCustomNonConfigurationInstance() != null){
			return (Uri)getLastCustomNonConfigurationInstance();
		}
		return null;
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		Log.d("UTM","Retaining image uri");
		return getImageUri();
	}

}
