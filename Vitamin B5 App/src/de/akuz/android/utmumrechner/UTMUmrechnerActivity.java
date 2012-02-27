package de.akuz.android.utmumrechner;

import de.akuz.android.utmumrechner.utils.MyAbstractActivity;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.MGRSRef;
import uk.me.jstott.jcoord.UTMRef;
import uk.me.jstott.jcoord.datum.WGS84Datum;
import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class UTMUmrechnerActivity extends MyAbstractActivity implements OnKeyListener,
		OnCheckedChangeListener, LocationListener {

	private EditText editUTM;
	private EditText editLatitude;
	private EditText editLongitude;
	private CheckBox useCurrentPosition;

	private LocationManager locationManager;

	private double longitude;
	private double latitude;

	private String utm;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		setContentView(R.layout.convert);
		initUIElements();

	}

	private void initUIElements() {
		editLatitude = (EditText) findViewById(R.id.editTextLatitude);
		editLongitude = (EditText) findViewById(R.id.editTextLongtitude);
		editUTM = (EditText) findViewById(R.id.editTextUTMCoordinates);
		useCurrentPosition = (CheckBox) findViewById(R.id.checkBoxUseCurrentPosition);

		editLatitude.setOnKeyListener(this);
		editLongitude.setOnKeyListener(this);
		editUTM.setOnKeyListener(this);
		
		useCurrentPosition.setOnCheckedChangeListener(this);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (v.getId() == editLatitude.getId()
				|| v.getId() == editLongitude.getId()) {
			latitude = Double.parseDouble(editLatitude.getText().toString());
			longitude = Double.parseDouble(editLongitude.getText().toString());
			recalculateUTM();
		} else if (v.getId() == editUTM.getId()) {
			utm = editUTM.getText().toString();
			recalculateGPS();
		}

		return false;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		if (isChecked) {
			enableUseOfCurrentPosition();
		} else {
			disableUseOfCurrentPosition();
		}
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

		editLatitude.setEnabled(false);
		editLongitude.setEnabled(false);
		editUTM.setEnabled(false);
	}

	private void disableUseOfCurrentPosition() {
		locationManager.removeUpdates(this);

		editLatitude.setEnabled(true);
		editLongitude.setEnabled(true);
		editUTM.setEnabled(true);
	}

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();

		updateGPSFields();
		recalculateUTM();
	}

	private void recalculateUTM() {
		try {
			LatLng gps = new LatLng(latitude, longitude);
			gps.toDatum(WGS84Datum.getInstance());
			UTMRef utmRef = gps.toUTMRef();
			MGRSRef mgrsRef = new MGRSRef(utmRef);
			utm = mgrsRef.toString();
			editUTM.setText(utm);
		} catch (IllegalArgumentException e) {
			Log.e("UTM","Got no valid GPS Position",e);
		}
	}

	private void recalculateGPS() {
		try {
			utm = editUTM.getText().toString();
			Log.d("UTM", "Recalculating GPS coordinates: "+utm);
			MGRSRef mgrsRef = new MGRSRef(utm);
			LatLng latlng = mgrsRef.toLatLng();
			latitude = latlng.getLatitude();
			longitude = latlng.getLongitude();
			updateGPSFields();
		} catch (IllegalArgumentException e) {
			Log.e("UTM", "UTM String is invalid",e);
		}

		
	}

	private void updateGPSFields() {
		Log.d("UTM","Updating GPS-Fields");
		editLatitude.setText(String.valueOf(latitude));
		editLongitude.setText(String.valueOf(longitude));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		locationManager.removeUpdates(this);
		super.onDestroy();
	}
}