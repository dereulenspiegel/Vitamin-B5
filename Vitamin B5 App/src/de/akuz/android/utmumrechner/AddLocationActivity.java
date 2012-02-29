package de.akuz.android.utmumrechner;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.akuz.android.utmumrechner.data.LocationDatabase;
import de.akuz.android.utmumrechner.data.TargetLocation;
import de.akuz.android.utmumrechner.utils.CoordinateUtils;
import de.akuz.android.utmumrechner.utils.MyAbstractActivity;

public class AddLocationActivity extends MyAbstractActivity implements
		LocationListener, GpsStatus.Listener, OnClickListener {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new LocationDatabase(this);
		locations = new LinkedList<Location>();
		setContentView(R.layout.add_location);
		initUiElements();
		updateFields();
		textViewCurrentPosition.setText("");
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		enableUseOfCurrentPosition();
		
	}
	
	private void initUiElements(){
		textViewCurrentPosition = (TextView)findViewById(R.id.textViewCurrentPosition);
		textViewPrecision = (TextView)findViewById(R.id.textViewPrecision);
		textViewSatelliteCount = (TextView)findViewById(R.id.textViewSatelliteCount);
		
		editTextDescription = (EditText)findViewById(R.id.editTextDescription);
		editTextName = (EditText)findViewById(R.id.editTextName);
		
		buttonSave = (Button)findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(this);
		buttonTakePicture = (Button)findViewById(R.id.buttonTakePicture);
		buttonTakePicture.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		disableUseOfCurrentPosition();
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(Location location) {
		locations.add(location);
		if(averageLatitude == 0){
			averageLatitude = location.getLatitude();
		} else {
			averageLatitude = ((averageLatitude + location.getLatitude())/2);
		}
		
		if(averageLongitude == 0){
			averageLongitude = location.getLongitude();
		} else {
			averageLongitude = ((averageLongitude + location.getLongitude())/2);
		}
		
		if(averagePrecision == 0){
			averagePrecision = location.getAccuracy();
		} else {
			averagePrecision = ((averagePrecision + location.getAccuracy())/2);
		}
		updateFields();
	}
	
	private void updateFields(){
		textViewPrecision.setText(decimalFormat.format(averagePrecision)+"m");
		textViewSatelliteCount.setText(String.valueOf(currentSatelliteCount));
		textViewCurrentPosition.setText(CoordinateUtils.latLonToMGRS(averageLatitude, averageLongitude));
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
	}

	private void disableUseOfCurrentPosition() {
		locationManager.removeUpdates(this);
	}

	@Override
	public void onGpsStatusChanged(int event) {
		GpsStatus status = locationManager.getGpsStatus(null);
		currentSatelliteCount = status.getMaxSatellites();
		updateFields();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == buttonSave.getId()){
			save();
		}
		if(id == buttonTakePicture.getId()){
			//TODO: take a picture and get the picture url
		}
	}
	
	private void save(){
		TargetLocation location;
		String description = editTextDescription.getText().toString();
		String coordinates = textViewCurrentPosition.getText().toString();
		String name = editTextName.getText().toString();
		db.open();
		location = db.createTargetLocation(name, coordinates, description);
		db.close();
	}

}
