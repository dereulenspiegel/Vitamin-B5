package de.akuz.android.utmumrechner.fragments;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.data.LocationDatabase;
import de.akuz.android.utmumrechner.data.TargetLocation;
import de.akuz.android.utmumrechner.utils.CoordinateUtils;
import de.akuz.android.utmumrechner.utils.StringUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddLocationFragment extends MyAbstractFragment implements
LocationListener, GpsStatus.Listener, OnClickListener{

	private LocationManager locationManager;

	private LocationDatabase db;

	private List<Location> locations;

	private double averageLongitude = 0;
	private double averageLatitude = 0;
	private double averagePrecision = 0;

	private int currentSatelliteCount = 0;

	private TextView textViewCurrentPosition;
	private TextView textViewSatelliteCount;
	private TextView textViewPrecision;

	private EditText editTextName;
	private EditText editTextDescription;

	private Button buttonSave;
	private Button buttonTakePicture;

	private DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.000");

	private File externalStorage;

	private File currentImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new LocationDatabase(this.getActivity());
		externalStorage = new File(Environment.getExternalStorageDirectory(),
				this.getActivity().getPackageName());
		locations = new LinkedList<Location>();
		setContentView(R.layout.fragment_add_location);
		setRetainInstance(true);

	}

	@Override
	protected void initUIElements() {
		textViewCurrentPosition = (TextView) findViewById(R.id.textViewCurrentPosition);
		textViewPrecision = (TextView) findViewById(R.id.textViewPrecision);
		textViewSatelliteCount = (TextView) findViewById(R.id.textViewSatelliteCount);

		editTextDescription = (EditText) findViewById(R.id.editTextDescription);
		editTextName = (EditText) findViewById(R.id.editTextName);

		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(this);
		buttonTakePicture = (Button) findViewById(R.id.buttonTakePicture);
		buttonTakePicture.setOnClickListener(this);
		clearFieldsAndResetAverages();
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		enableUseOfCurrentPosition();
	}

	@Override
	public void onDestroy() {
		disableUseOfCurrentPosition();
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(Location location) {
		locations.add(location);
		if (averageLatitude == 0) {
			averageLatitude = location.getLatitude();
		} else {
			averageLatitude = ((averageLatitude + location.getLatitude()) / 2);
		}

		if (averageLongitude == 0) {
			averageLongitude = location.getLongitude();
		} else {
			averageLongitude = ((averageLongitude + location.getLongitude()) / 2);
		}

		if (averagePrecision == 0) {
			averagePrecision = location.getAccuracy();
		} else {
			averagePrecision = ((averagePrecision + location.getAccuracy()) / 2);
		}
		updateFields();
	}

	private void updateFields() {
		textViewPrecision.setText(decimalFormat.format(averagePrecision) + "m");
		textViewSatelliteCount.setText(String.valueOf(currentSatelliteCount));
		if (averageLatitude != 0 && averageLongitude != 0) {
			textViewCurrentPosition.setText(CoordinateUtils.latLonToMGRS(
					averageLatitude, averageLongitude));
		} else {
			textViewCurrentPosition.setText("");
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	private void enableUseOfCurrentPosition() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		criteria.setSpeedRequired(false);

		String bestProvider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(bestProvider, 2000, 0, this);
		locationManager.addGpsStatusListener(this);
	}

	private void disableUseOfCurrentPosition() {
		locationManager.removeUpdates(this);
		locationManager.removeGpsStatusListener(this);
	}

	@Override
	public void onGpsStatusChanged(int event) {
		if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
			GpsStatus status = locationManager.getGpsStatus(null);
			Iterable<GpsSatellite> satellites = status.getSatellites();
			int satellitesInFix = 0;
			for(GpsSatellite s : satellites){
				if(s.usedInFix()){
					satellitesInFix++;
				}
			}
			currentSatelliteCount = satellitesInFix;
			updateFields();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == buttonSave.getId()) {
			save();
		}
		if (id == buttonTakePicture.getId()) {
			takePicture();
		}
	}

	private void save() {
		TargetLocation location;
		String description = editTextDescription.getText().toString();
		String coordinates = textViewCurrentPosition.getText().toString();
		String name = editTextName.getText().toString();
		if (StringUtils.isEmtpy(description)
				|| StringUtils.isEmtpy(coordinates)
				|| StringUtils.isEmtpy(name)) {
			Toast.makeText(this.getActivity(), R.string.error_please_provide_all_info,
					Toast.LENGTH_LONG).show();
			return;
		}
		db.open();
		location = db.createTargetLocation(name, coordinates, description);
		if (currentImage != null) {
			location.setPictureUrl(currentImage.getAbsolutePath());
			db.updateTargetLocation(location);
		}
		db.close();
		clearFieldsAndResetAverages();
		getActivity().finish();
	}

	private void clearFieldsAndResetAverages() {
		locations.clear();
		averageLatitude = 0;
		averageLongitude = 0;
		averagePrecision = 0;
		editTextDescription.setText("");
		editTextName.setText("");
		buttonTakePicture.setEnabled(true);
		updateFields();
	}

	private void takePicture() {
		if (StringUtils.isEmtpy(textViewCurrentPosition.getText().toString())) {
			Toast.makeText(this.getActivity(), R.string.error_coordinate_required,
					Toast.LENGTH_LONG).show();
			return;
		}
		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getImageFile()));
		startActivityForResult(i, 0);
	}

	private File getImageFile() {
		if (!externalStorage.exists()) {
			externalStorage.mkdirs();
		}
		String coordinates = textViewCurrentPosition.getText().toString();
		try {
			File imagePath = new File(externalStorage, "images");
			if (!imagePath.exists()) {
				imagePath.mkdirs();
			}
			imagePath = new File(imagePath, StringUtils.hashSha1(coordinates)+".jpg");
			currentImage = imagePath;
			return imagePath;
		} catch (NoSuchAlgorithmException e) {
			Log.e("UTM", "Error while hashing", e);
		} catch (UnsupportedEncodingException e) {
			Log.e("UTM", "Error while hashing", e);
		}
		return null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("UTM", "Received result for Camera action. ResultCode "
				+ resultCode);
		if (resultCode == Activity.RESULT_OK && requestCode == 0) {
			Log.d("UTM", "image intent: " + data.toString());
			buttonTakePicture.setEnabled(false);
			
		} else if (requestCode == 0) {
			Log.d("UTM","Received no success, setting image to null");
			currentImage = null;
		}
	}

}
