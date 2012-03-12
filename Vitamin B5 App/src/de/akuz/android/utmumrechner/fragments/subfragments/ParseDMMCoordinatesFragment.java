package de.akuz.android.utmumrechner.fragments.subfragments;

import de.akuz.android.utmumrechner.utils.CoordinateUtils;
import android.location.Location;

public class ParseDMMCoordinatesFragment extends
		AbstractParseGPSCoordinatesFragment {

	@Override
	protected Location parseInput(String latitude, String longitude) {
		return CoordinateUtils.parseDMM(latitude, longitude);
	}

	@Override
	protected void initUIElements() {
		super.initUIElements();
		editLatitude.setHint("51 27.55");
		editLongitude.setHint("7 35.36666666666667");
	}

	@Override
	protected void setCoordinates(Location l) {
		editLatitude.setText(CoordinateUtils.formatDMM(l.getLatitude()));
		editLongitude.setText(CoordinateUtils.formatDMM(l.getLongitude()));

	}

}
