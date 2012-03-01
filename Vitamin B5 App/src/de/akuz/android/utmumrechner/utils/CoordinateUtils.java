package de.akuz.android.utmumrechner.utils;

import android.location.Location;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.MGRSRef;
import uk.me.jstott.jcoord.UTMRef;
import uk.me.jstott.jcoord.datum.WGS84Datum;

public class CoordinateUtils {
	
	public static String latLonToMGRS(double latitude, double longitude){
		LatLng gps = new LatLng(latitude, longitude);
		gps.toDatum(WGS84Datum.getInstance());
		UTMRef utmRef = gps.toUTMRef();
		MGRSRef mgrsRef = new MGRSRef(utmRef);
		return mgrsRef.toString();
	}
	
	public static String locationToMGRS(Location location){
		return latLonToMGRS(location.getLatitude(), location.getLongitude());
	}
	
	public static Location mgrsToLocation(String mgrs){
		Location location = new Location("calculatedLocation");
		MGRSRef mgrsRef = new MGRSRef(mgrs);
		location.setLatitude(mgrsRef.toLatLng().getLatitude());
		location.setLongitude(mgrsRef.toLatLng().getLongitude());
		return location;
	}

}
