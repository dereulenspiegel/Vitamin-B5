package de.akuz.android.utmumrechner.fragments.subfragments;

import de.akuz.android.utmumrechner.utils.CoordinateUtils;
import android.location.Location;

public class ParseDMSCoordinatesFragment extends
		AbstractParseGPSCoordinatesFragment {

	@Override
	protected Location parseInput(String latitude, String longitude) {
		return CoordinateUtils.parseDMS(latitude, longitude);
	}

	@Override
	protected void initUIElements() {
		super.initUIElements();
		editLatitude.setHint("51 27 33");
		editLongitude.setHint("7 35 22");
	}

	@Override
	protected void setCoordinates(Location l) {
		editLatitude.setText(CoordinateUtils.formatDMS(l.getLatitude()));
		editLongitude.setText(CoordinateUtils.formatDMS(l.getLongitude()));

	}

}
