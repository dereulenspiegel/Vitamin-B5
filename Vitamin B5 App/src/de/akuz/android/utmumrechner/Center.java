package de.akuz.android.utmumrechner;

import java.text.DecimalFormat;
import java.util.List;

import de.akuz.android.utmumrechner.data.LocationDatabase;
import de.akuz.android.utmumrechner.data.TargetLocation;
import de.akuz.android.utmumrechner.utils.CoordinateUtils;
import de.akuz.android.utmumrechner.utils.MyAbstractActivity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class Center extends MyAbstractActivity implements LocationListener {

	private TextView textViewCoordinates;
	private TextView textViewAccuracy;
	private TextView textViewDatabaseCount;
	
	private LocationManager locationManager;
	
	private DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.000");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setSpeedRequired(false);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(bestProvider, 5000, 50, this);
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		String secondBestProvider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(secondBestProvider, 5000, 50, this);
		setContentView(R.layout.center);
		initUIElements();
	}
	
	protected void initUIElements(){
		textViewAccuracy = (TextView)findViewById(R.id.textViewAccuracy);
		textViewCoordinates = (TextView)findViewById(R.id.textViewCoordinates);
		textViewDatabaseCount = (TextView)findViewById(R.id.textViewDatabaseCount);
		
		textViewAccuracy.setText("");
		textViewCoordinates.setText("");
		textViewDatabaseCount.setText("");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocationDatabase db = new LocationDatabase(this);
		db.open();
		List<TargetLocation> list = db.getAllLocations();
		db.close();
		String dbCount = getResources().getQuantityString(R.plurals.you_have_in_db, list.size());
		textViewDatabaseCount.setText(String.format(dbCount, list.size()));
	}

	@Override
	public void onLocationChanged(Location location) {
		textViewCoordinates.setText(CoordinateUtils.locationToMGRS(location));
		textViewAccuracy.setText(decimalFormat.format(location.getAccuracy())+"m");
	}

	@Override
	public void onProviderDisabled(String arg0) {
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
