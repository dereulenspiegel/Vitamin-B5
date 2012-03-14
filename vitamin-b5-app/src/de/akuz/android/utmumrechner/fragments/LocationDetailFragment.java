package de.akuz.android.utmumrechner.fragments;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.data.LocationDatabase;
import de.akuz.android.utmumrechner.data.TargetLocation;
import de.akuz.android.utmumrechner.utils.StringUtils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationDetailFragment extends MyAbstractFragment implements
		OnClickListener {

	public interface LocationDetailListener {

		public void showPicture(TargetLocation location);

		public void showOnMap(TargetLocation location);

	}

	private final static int IMAGE_MAX_SIZE = 120000;
	private TextView textViewName;
	private TextView textViewCoordinates;
	private TextView textViewDescription;
	private ImageView imageView;

	private LocationDatabase db;

	private TargetLocation location;

	private Uri imageUri;
	private Bitmap image;

	private LocationDetailListener listener;

	private final static int MENU_SHOW_ON_MAP = 31;
	private final static int MENU_SHOW_FULL_PICTURE = 32;

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
			if (id > -1) {
				updateContent(id);
			}
		}
	}

	public void clearFields() {
		textViewCoordinates.setText("");
		textViewDescription.setText("");
		textViewName.setText("");
		imageView.setImageBitmap(null);
		imageView.setVisibility(View.GONE);
	}

	public void updateContent(long id) {
		if (location != null && location.getId() == id) {
			return;
		}
		db.open();
		location = db.getLocationById(id);
		db.close();

		textViewName.setText(location.getName());
		textViewCoordinates.setText(location.getMgrsCoordinate());
		textViewDescription.setText(location.getDescription());
		updateImage(Uri.parse(location.getPictureUrl()));
	}

	private void updateImage(Uri uri) {
		if (uri == null) {
			imageView.setImageBitmap(null);
			imageView.setVisibility(View.GONE);
		} else if (imageUri == null || !imageUri.equals(uri)) {
			imageUri = uri;
			Log.d("UTM", "Showing image with uri: " + uri.toString());

			setImage(uri.toString());
		}
	}

	private void setImage(String path) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		BitmapFactory.decodeFile(path, o);
		int scale = 1;
		while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
			scale++;
		}
		Bitmap b = null;
		if (scale > 1) {
			scale--;
			Log.d("UTM", "Scaling image with factor " + scale);
			// scale to max possible inSampleSize that still yields an image
			// larger than target
			o = new BitmapFactory.Options();
			o.inSampleSize = scale;
			b = BitmapFactory.decodeFile(path, o);
			int height = b.getHeight();
			int width = b.getWidth();

			double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
			double x = (y / height) * width;

			Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
					(int) y, true);
			b.recycle();
			b = scaledBitmap;

			System.gc();

		} else {
			b = BitmapFactory.decodeFile(path);
		}
		image = b;
		imageView.setImageBitmap(image);

		imageView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_location_detail);
		db = new LocationDatabase(getActivity());
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == imageView.getId()) {
			showFullPicture();
		}
		if (v.getId() == textViewCoordinates.getId()) {
			showCoordinatesOnMap();
		}

	}

	private void showCoordinatesOnMap() {
		if (listener != null) {
			listener.showOnMap(location);
		}
	}

	private void showFullPicture() {
		if (listener != null) {
			listener.showPicture(location);
		}
	}

	public void setListener(LocationDetailListener listener) {
		if (listener != null) {
			this.listener = listener;
		}

	}

	public void unSetListener() {
		this.listener = null;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		SubMenu subMenu = menu
				.addSubMenu(getString(R.string.sub_menu_location_detail));
		subMenu.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		subMenu.add(Menu.NONE, MENU_SHOW_ON_MAP, Menu.NONE,
				R.string.show_on_map);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MENU_SHOW_ON_MAP:
			showCoordinatesOnMap();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		if (image != null) {
			image.recycle();
			image = null;
		}
		super.onDestroy();
	}

}
