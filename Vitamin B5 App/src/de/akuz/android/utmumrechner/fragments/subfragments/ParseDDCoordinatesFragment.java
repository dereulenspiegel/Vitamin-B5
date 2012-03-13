package de.akuz.android.utmumrechner.fragments.subfragments;

import de.akuz.android.utmumrechner.utils.CoordinateUtils;
import android.location.Location;

public class ParseDDCoordinatesFragment extends
		AbstractParseGPSCoordinatesFragment {

	@Override
	protected Location parseInput(String latitude, String longitude) {
		try {
			double lat = Double.parseDouble(latitude);
			double lon = Double.parseDouble(longitude);
			Location l = new Location(CoordinateUtils.LOCATION_UTILS_PROVIDER);
			l.setLatitude(lat);
			l.setLongitude(lon);
			return l;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	protected void initUIElements() {
		super.initUIElements();
		editLatitude.setHint("51.45916666666667");
		editLongitude.setHint("7.589444444444444");
	}

	@Override
	protected void setCoordinates(Location l) {
		String latitude = CoordinateUtils.formatDD(l.getLatitude());
		String longitude = CoordinateUtils.formatDD(l.getLongitude());
		editLatitude.setText(latitude);
		editLongitude.setText(longitude);
	}

}
