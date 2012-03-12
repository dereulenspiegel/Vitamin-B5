package de.akuz.android.utmumrechner.test.utils;

import junit.framework.Assert;

import org.junit.Test;

import de.akuz.android.utmumrechner.utils.CoordinateUtils;

import android.location.Location;
import android.test.AndroidTestCase;

public class CoordinateUtilsTestCase extends AndroidTestCase {

	private final static double latitudeDecimal = 51.45916666666667;
	private final static double longitudeDecimal = 7.589444444444444;

	private final static String latitudeDMS = "51 27 33";
	private final static String longitudeDMS = "7 35 22";

	private final static String latitudeDMM = "51 27.55";
	private final static String longitudeDMM = "7 35.36666666666667";

	@Test
	public void testParseDMS() throws Exception {
		Location l = CoordinateUtils.parseDMS(latitudeDMS, longitudeDMS);
		Assert.assertEquals("Latitude does not match", latitudeDecimal,
				l.getLatitude(), 0.0);
		Assert.assertEquals("Longitude does not match", longitudeDecimal,
				l.getLongitude(), 0.0);

	}

	@Test
	public void testParseDM() throws Exception {
		Location l = CoordinateUtils.parseDMM(latitudeDMM, longitudeDMM);
		Assert.assertEquals("Latitude does not match", latitudeDecimal,
				l.getLatitude(), 0.0);
		Assert.assertEquals("Longitude does not match", longitudeDecimal,
				l.getLongitude(), 0.0);
	}

	public void testFormatDMM() throws Exception {
		String latitude = CoordinateUtils.formatDMM(latitudeDecimal);
		String longitude = CoordinateUtils.formatDMM(longitudeDecimal);

		Assert.assertEquals("Latitude does not match", latitudeDMM, latitude);
		// Fix for rounding error
		Assert.assertEquals("Longitude does not match", "7 35.3667", longitude);
	}

	public void testFormatDMS() throws Exception {
		String latitude = CoordinateUtils.formatDMS(latitudeDecimal);
		String longitude = CoordinateUtils.formatDMS(longitudeDecimal);
		Assert.assertEquals("Latitude doesn't match", latitudeDMS, latitude);
		Assert.assertEquals("Longitude doesn't match", longitudeDMS, longitude);
	}

}
