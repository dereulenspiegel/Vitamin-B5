package de.akuz.android.utmumrechner.fragments.subfragments;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import de.akuz.android.utmumrechner.R;
import de.akuz.android.utmumrechner.fragments.MyAbstractFragment;

public abstract class AbstractParseGPSCoordinatesFragment extends
		MyAbstractFragment implements IInputGPSCoordinates, TextWatcher {

	public interface CoordinateChangedListener {
		public void coordinatesChanged(Location l);
	}

	protected List<CoordinateChangedListener> listeners = 
			new ArrayList<AbstractParseGPSCoordinatesFragment.CoordinateChangedListener>(1);

	protected EditText editLatitude;
	protected EditText editLongitude;

	protected Location currentLocation;

	protected boolean isEnabled = true;

	@Override
	protected void initUIElements() {
		editLatitude = (EditText) findViewById(R.id.editTextLatitude);
		editLongitude = (EditText) findViewById(R.id.editTextLongitude);

		
		if (currentLocation != null) {
			setCoordinates(currentLocation);
		}
		setEnabled(isEnabled);
		editLatitude.addTextChangedListener(this);
		editLongitude.addTextChangedListener(this);
	}

	@Override
	public Location getLocationFromInput() {
		return currentLocation;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subfragment_enter_gps_coordinates);
	}

	public void addListener(CoordinateChangedListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	public void removeListener(CoordinateChangedListener l) {
		if (l != null) {
			listeners.remove(l);
		}
	}

	protected abstract Location parseInput(String latitude, String longitude);

	private Location parseLocalInput(String latitude, String longitude) {
		try {
			return parseInput(latitude, longitude);
		} catch (Exception e) {
			Log.w("UTM","Warning! Couldn't parse Latitude "+latitude+ " and Longitude "+longitude);
			Log.w("UTM",
					"Warning! An Exception occured while parsing GPS coordinates from text",
					e);
			return null;
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// Ignore

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// Ignore

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Let this run in the background
		currentLocation = parseLocalInput(editLatitude.getText().toString(),
				editLongitude.getText().toString());
		for (CoordinateChangedListener l : listeners) {
			l.coordinatesChanged(currentLocation);
		}

	}

	public void updateFields(Location l) {
		currentLocation = l;
		if (editLatitude == null || editLongitude == null) {
			return;
		}
		editLatitude.removeTextChangedListener(this);
		editLongitude.removeTextChangedListener(this);
		if (l == null) {
			editLatitude.setText("");
			editLongitude.setText("");
		} else {
			setCoordinates(l);
		}
		editLatitude.addTextChangedListener(this);
		editLongitude.addTextChangedListener(this);
	}

	protected abstract void setCoordinates(Location l);

	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
		if (editLatitude != null && editLongitude != null) {
			editLatitude.setEnabled(enabled);
			editLongitude.setEnabled(enabled);
		}
	}
}
