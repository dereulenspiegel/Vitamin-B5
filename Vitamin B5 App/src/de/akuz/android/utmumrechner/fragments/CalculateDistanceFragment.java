package de.akuz.android.utmumrechner.fragments;

import java.text.DecimalFormat;

import uk.me.jstott.jcoord.MGRSRef;
import uk.me.jstott.jcoord.datum.WGS84Datum;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
import de.akuz.android.utmumrechner.R;

public class CalculateDistanceFragment extends MyAbstractFragment implements OnKeyListener{

	private EditText editUtm1;
	private EditText editUtm2;
	private TextView distanceView;
	
	private DecimalFormat decimalFormat;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		decimalFormat = new DecimalFormat("#,###,##0.000");
		setContentView(R.layout.fragment_calculate_distance);
	}

	@Override
	protected void initUIElements() {
		editUtm1 = (EditText) findViewById(R.id.editTextUTM1);
		editUtm2 = (EditText) findViewById(R.id.editTextUTM2);
		distanceView = (TextView) findViewById(R.id.textViewDistance);

		editUtm1.setOnKeyListener(this);
		editUtm2.setOnKeyListener(this);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		try {
			Log.d("UTM","Received Key Event");
			MGRSRef utm1 = new MGRSRef(editUtm1.getText().toString());
			utm1.setDatum(WGS84Datum.getInstance());
			MGRSRef utm2 = new MGRSRef(editUtm2.getText().toString());
			utm2.setDatum(WGS84Datum.getInstance());
			double kilometers = utm1.toLatLng().distance(utm2.toLatLng());
			if(kilometers > 2.0d){
				distanceView.setText(decimalFormat.format(kilometers)+"km");
			} else {
				distanceView.setText(decimalFormat.format(kilometers*1000)+"m");
			}
		} catch (IllegalArgumentException e) {
			//Ignore
		}
		return false;
	}

}
