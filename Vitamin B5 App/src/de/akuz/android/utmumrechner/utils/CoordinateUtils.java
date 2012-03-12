package de.akuz.android.utmumrechner.utils;

import java.text.DecimalFormat;

import android.location.Location;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.MGRSRef;
import uk.me.jstott.jcoord.UTMRef;
import uk.me.jstott.jcoord.datum.WGS84Datum;

public class CoordinateUtils {

	public final static String LOCATION_UTILS_PROVIDER = "calculatedLocation";
	
	private final static DecimalFormat decFormat = new DecimalFormat("###.####");

	public static String latLonToMGRS(double latitude, double longitude) {
		LatLng gps = new LatLng(latitude, longitude);
		gps.toDatum(WGS84Datum.getInstance());
		UTMRef utmRef = gps.toUTMRef();
		MGRSRef mgrsRef = new MGRSRef(utmRef);
		return mgrsRef.toString();
	}

	public static String locationToMGRS(Location location) {
		return latLonToMGRS(location.getLatitude(), location.getLongitude());
	}

	public static Location mgrsToLocation(String mgrs) {
		Location location = new Location(LOCATION_UTILS_PROVIDER);
		MGRSRef mgrsRef = new MGRSRef(mgrs);
		location.setLatitude(mgrsRef.toLatLng().getLatitude());
		location.setLongitude(mgrsRef.toLatLng().getLongitude());
		return location;
	}

	public static Location parseDMM(String latitude, String longitude) {
		double lat = parseDMM(latitude);
		double lon = parseDMM(longitude);
		return buildLocationObject(lat, lon);
	}

	private static double parseDMM(String in) {
		String[] parts = in.split(" ");
		double fullDegress = Double.parseDouble(parts[0].trim());
		double minutes = Double.parseDouble(parts[1].trim());
		return (fullDegress + (minutes / 60.0));
	}

	public static Location parseDMS(String latitude, String longitude) {
		double lat = parseDMS(latitude);
		double lon = parseDMS(longitude);
		return buildLocationObject(lat, lon);
	}

	private static double parseDMS(String in) {
		String[] parts = in.split(" ");
		double fullDegrees = Double.parseDouble(parts[0].trim());
		double minutes = Double.parseDouble(parts[1].trim());
		double seconds = Double.parseDouble(parts[2].trim());
		
		seconds = seconds / 60.0d;
		minutes = ((minutes + seconds )/ 60.0d);

		return (fullDegrees + minutes);
	}
	
	private static Location buildLocationObject(double latitude, double longitude){
		Location l = new Location(LOCATION_UTILS_PROVIDER);
		l.setLatitude(latitude);
		l.setLongitude(longitude);
		return l;
	}
	
	public static String formatDD(double in){
		return String.valueOf(in);
	}
	
	public static String formatDMM(double in){
		int fullDegrees = (int)in;
		double minutes = in - fullDegrees;
		StringBuffer buffer = new StringBuffer();
		buffer.append(fullDegrees);
		buffer.append(" ");
		buffer.append(decFormat.format(minutes * 60.0d));
		return buffer.toString();
	}
	
	public static String formatDMS(double in){
		int fullDegrees = (int)in;
		double minutesTemp = (in - fullDegrees) * 60.0d;
		int minutes = (int)minutesTemp;
		int seconds = (int) Math.round(((minutesTemp - minutes)*60.0d));
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(fullDegrees);
		buffer.append(" ");
		buffer.append(minutes);
		buffer.append(" ");
		buffer.append(seconds);
		return buffer.toString();
		
	}
}
