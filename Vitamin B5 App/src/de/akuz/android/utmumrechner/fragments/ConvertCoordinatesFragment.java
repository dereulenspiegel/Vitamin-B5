package de.akuz.android.utmumrechner.fragments;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.fragments.subfragments.AbstractParseGPSCoordinatesFragment;
import de.akuz.android.utmumrechner.fragments.subfragments.ParseDDCoordinatesFragment;
import de.akuz.android.utmumrechner.fragments.subfragments.ParseDMMCoordinatesFragment;
import de.akuz.android.utmumrechner.fragments.subfragments.ParseDMSCoordinatesFragment;
import de.akuz.android.utmumrechner.fragments.subfragments.AbstractParseGPSCoordinatesFragment.CoordinateChangedListener;
import de.akuz.android.utmumrechner.utils.BestLocationListenerWrapper;
import de.akuz.android.utmumrechner.utils.CoordinateUtils;

public class ConvertCoordinatesFragment extends MyAbstractFragment implements
		OnCheckedChangeListener, LocationListener, CoordinateChangedListener,
		OnItemSelectedListener {

	private EditText editUTM;
	private CheckBox useCurrentPosition;

	private Spinner gpsFormatSpinner;

	private TextWatcher utmTextWatcher;

	private BestLocationListenerWrapper locationWrapper;

	private Location location;
	
	private AbstractParseGPSCoordinatesFragment currentParseGPSCoordinatesFragment;
	
	private FragmentManager fragmentManager;

	private String utm;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentManager = getFragmentManager();
		locationWrapper = new BestLocationListenerWrapper(getActivity(), this);

		setContentView(R.layout.fragment_convert_coordinates);
	}

	@Override
	protected void initUIElements() {
		editUTM = (EditText) findViewById(R.id.editTextUTMCoordinates);
		gpsFormatSpinner = (Spinner) findViewById(R.id.spinnerGPSFormat);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(
				this.getActivity(), R.array.gps_formats,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		gpsFormatSpinner.setAdapter(adapter);
		gpsFormatSpinner.setOnItemSelectedListener(this);
		
		currentParseGPSCoordinatesFragment = new ParseDDCoordinatesFragment();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.add(R.id.gpsInputPlaceholder,currentParseGPSCoordinatesFragment);
		transaction.commit();
		
		useCurrentPosition = (CheckBox) findViewById(R.id.checkBoxUseCurrentPosition);

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
				// Ignore

			}

			@Override
			public void afterTextChanged(Editable s) {
				// Ignore

			}
		};

		editUTM.addTextChangedListener(utmTextWatcher);

		useCurrentPosition.setOnCheckedChangeListener(this);
	}
	
	private void showParseGPSFragment(AbstractParseGPSCoordinatesFragment fragment){
		currentParseGPSCoordinatesFragment.removeListener(this);
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.gpsInputPlaceholder,fragment);
		transaction.commit();
		currentParseGPSCoordinatesFragment = fragment;
		currentParseGPSCoordinatesFragment.updateFields(location);
		currentParseGPSCoordinatesFragment.setEnabled(!useCurrentPosition.isChecked());
		currentParseGPSCoordinatesFragment.addListener(this);
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
		locationWrapper.enableLocationUpdates();

		editUTM.setEnabled(false);
		currentParseGPSCoordinatesFragment.setEnabled(false);
	}

	private void disableUseOfCurrentPosition() {
		locationWrapper.disableLocationUpdates();

		editUTM.setEnabled(true);
		currentParseGPSCoordinatesFragment.setEnabled(true);
	}

	@Override
	public void onLocationChanged(Location location) {
		this.location = location;

		updateGPSFields();
		recalculateUTM();
	}

	private void recalculateUTM() {
		try {
			utm =  CoordinateUtils.locationToMGRS(location);
			editUTM.removeTextChangedListener(utmTextWatcher);
			editUTM.setText(utm);
			editUTM.addTextChangedListener(utmTextWatcher);
		} catch (IllegalArgumentException e) {
			Log.w("UTM", "Got no valid GPS Position", e);
		}
	}

	private void recalculateGPS() {
		try {
			location = CoordinateUtils.mgrsToLocation(utm);
			currentParseGPSCoordinatesFragment.updateFields(location);
		} catch (IllegalArgumentException e) {
			Log.w("UTM", "UTM String is invalid", e);
		}

	}

	private void updateGPSFields() {
		currentParseGPSCoordinatesFragment.updateFields(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// Ignore

	}

	@Override
	public void onProviderEnabled(String provider) {
		// Ignore

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Ignore

	}

	@Override
	public void onDestroy() {
		locationWrapper.disableLocationUpdates();
		super.onDestroy();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch(position){
		case 0:
			showParseGPSFragment(new ParseDDCoordinatesFragment());
			break;
		case 1:
			showParseGPSFragment(new ParseDMMCoordinatesFragment());
			break;
		case 2:
			showParseGPSFragment(new ParseDMSCoordinatesFragment());
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// ignore

	}

	@Override
	public void coordinatesChanged(Location l) {
		this.location = l;
		recalculateUTM();
	}

}
