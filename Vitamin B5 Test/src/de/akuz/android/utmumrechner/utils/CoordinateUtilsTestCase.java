package de.akuz.android.utmumrechner.utils;

import junit.framework.Assert;

import org.junit.Test;

import android.location.Location;
import android.test.AndroidTestCase;

public class CoordinateUtilsTestCase extends AndroidTestCase{
	
	
	@Test
	public void testParseDMS() throws Exception {
		String latitudeDMS = "51 27 33";
		String longitudeDMS = "7 35 22";
		
		double latitudeDecimal = 51.45916666666667;
		double longitudeDecimal = 7.589444444444444;
		
		Location l = CoordinateUtils.parseDMS(latitudeDMS, longitudeDMS);
		Assert.assertEquals("Latitude does not match",latitudeDecimal, l.getLatitude(), 0.0);
		Assert.assertEquals("Longitude does not match", longitudeDecimal, l.getLongitude(), 0.0);
		
	}
	
	@Test
	public void testParseDM() throws Exception {
		String latitudeDMM = "51 27.55";
		String longitudeDMM = "7 35.36666666666667";
		
		double latitudeDecimal = 51.45916666666667;
		double longitudeDecimal = 7.589444444444444;
		
		Location l = CoordinateUtils.parseDMM(latitudeDMM, longitudeDMM);
		Assert.assertEquals("Latitude does not match",latitudeDecimal, l.getLatitude(), 0.0);
		Assert.assertEquals("Longitude does not match", longitudeDecimal, l.getLongitude(), 0.0);
	}

}
