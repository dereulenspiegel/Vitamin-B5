package de.akuz.android.utmumrechner.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class BestLocationListenerWrapper implements LocationListener {
	
	private LocationManager locationManager;
	private LocationListener listener;
	
	private Location currentBestLocation;
	
	private final static int TWO_MINUTES = 1000*60*2;
	
	private long timeInterval;
	private int minDistance;
	
	public BestLocationListenerWrapper(Context context, LocationListener listener){
		this(context,listener,2000,20);
	}
	
	public BestLocationListenerWrapper(Context context, LocationListener listener, long timeInterval, int minDistance){
		this.listener = listener;
		this.timeInterval = timeInterval;
		this.minDistance = minDistance;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public void onLocationChanged(Location location) {
		listener.onLocationChanged(isBetterLocation(location)?location:currentBestLocation);

	}

	@Override
	public void onProviderDisabled(String provider) {
		listener.onProviderDisabled(provider);

	}

	@Override
	public void onProviderEnabled(String provider) {
		listener.onProviderEnabled(provider);

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		listener.onStatusChanged(provider, status, extras);

	}
	
	private boolean isBetterLocation(Location location){
		return isBetterLocation(location, currentBestLocation);
	}
	
	private boolean isBetterLocation(Location location, Location currentBestLocation){
		if(currentBestLocation == null){
			return true;
		}
		
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < TWO_MINUTES;
		boolean isNewer = timeDelta > 0;
		
		if(isSignificantlyNewer){
			return true;
		} else if(isSignificantlyOlder){
			return false;
		}
		
		int accuracyDelta = (int)(location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;
		
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());
		
		if(isMoreAccurate){
			return true;
		} else if (isNewer && !isLessAccurate){
			return true;
		} else if(isNewer && !isSignificantlyLessAccurate && isFromSameProvider){
			return true;
		}
		return false;
	}
	
	private boolean isSameProvider(String provider1, String provider2){
		if(provider1 == null){
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
	
	public void destroy(){
		locationManager.removeUpdates(this);
	}
	
	public Location getLastBestLocation(){
		Location lastNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		Location lastGPSLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		return isBetterLocation(lastNetworkLocation, lastGPSLocation)?lastNetworkLocation:lastGPSLocation;
	}
	
	public void disableLocationUpdates(){
		locationManager.removeUpdates(this);
	}
	
	public void enableLocationUpdates(){
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, timeInterval, minDistance, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, timeInterval, minDistance, this);
	}

}
