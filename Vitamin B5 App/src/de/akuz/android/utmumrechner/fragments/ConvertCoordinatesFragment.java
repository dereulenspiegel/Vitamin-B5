package de.akuz.android.utmumrechner.fragments;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.MGRSRef;
import uk.me.jstott.jcoord.UTMRef;
import uk.me.jstott.jcoord.datum.WGS84Datum;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import de.akuz.android.utmumrechner.R;

public class ConvertCoordinatesFragment extends MyAbstractFragment implements
		OnCheckedChangeListener, LocationListener {

	private EditText editUTM;
	private EditText editLatitude;
	private EditText editLongitude;
	private CheckBox useCurrentPosition;

	private TextWatcher latitudeTextWatcher;
	private TextWatcher longitudeTextWatcher;
	private TextWatcher utmTextWatcher;

	private LocationManager locationManager;

	private double longitude;
	private double latitude;

	private String utm;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		setContentView(R.layout.fragment_convert_coordinates);
	}

	@Override
	protected void initUIElements() {
		editLatitude = (EditText) findViewById(R.id.editTextLatitude);
		editLongitude = (EditText) findViewById(R.id.editTextLongtitude);
		editUTM = (EditText) findViewById(R.id.editTextUTMCoordinates);
		useCurrentPosition = (CheckBox) findViewById(R.id.checkBoxUseCurrentPosition);

		latitudeTextWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				try {
					latitude = Double.parseDouble(editLatitude.getText()
							.toString());

				} catch (NumberFormatException e) {
					latitude = 0;
				}
				recalculateUTM();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		};

		longitudeTextWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				try {
					longitude = Double.parseDouble(editLongitude.getText()
							.toString());
				} catch (NumberFormatException e) {
					longitude = 0;
				}
				recalculateUTM();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		};

		utmTextWatcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				utm = editUTM.getText().toString();
				recalculateGPS();

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		};

		editLatitude.addTextChangedListener(latitudeTextWatcher);
		editLongitude.addTextChangedListener(longitudeTextWatcher);
		editUTM.addTextChangedListener(utmTextWatcher);

		useCurrentPosition.setOnCheckedChangeListener(this);
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
			editUTM.removeTextChangedListener(utmTextWatcher);
			editUTM.setText(utm);
			editUTM.addTextChangedListener(utmTextWatcher);
		} catch (IllegalArgumentException e) {
			Log.w("UTM", "Got no valid GPS Position", e);
		}
	}

	private void recalculateGPS() {
		try {
			utm = editUTM.getText().toString().toUpperCase();
			Log.d("UTM", "Recalculating GPS coordinates: " + utm);
			MGRSRef mgrsRef = new MGRSRef(utm);
			LatLng latlng = mgrsRef.toLatLng();
			latitude = latlng.getLatitude();
			longitude = latlng.getLongitude();
			editLatitude.removeTextChangedListener(latitudeTextWatcher);
			editLongitude.removeTextChangedListener(longitudeTextWatcher);
			updateGPSFields();
			editLatitude.addTextChangedListener(latitudeTextWatcher);
			editLongitude.addTextChangedListener(longitudeTextWatcher);
		} catch (IllegalArgumentException e) {
			Log.w("UTM", "UTM String is invalid", e);
		}

	}

	private void updateGPSFields() {
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
	public void onDestroy() {
		locationManager.removeUpdates(this);
		super.onDestroy();
	}

}
