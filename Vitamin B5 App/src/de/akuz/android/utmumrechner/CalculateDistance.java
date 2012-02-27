package de.akuz.android.utmumrechner;

import java.text.DecimalFormat;

import de.akuz.android.utmumrechner.utils.MyAbstractActivity;

import uk.me.jstott.jcoord.MGRSRef;
import uk.me.jstott.jcoord.datum.WGS84Datum;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

public class CalculateDistance extends MyAbstractActivity implements OnKeyListener {

	private EditText editUtm1;
	private EditText editUtm2;
	private TextView distanceView;
	
	private DecimalFormat decimalFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		decimalFormat = new DecimalFormat("#,###,##0.000");
		setContentView(R.layout.distance);
		initUIElements();
	}

	private void initUIElements() {
		editUtm1 = (EditText) findViewById(R.id.editTextUTM1);
		editUtm2 = (EditText) findViewById(R.id.editTextUTM2);
		distanceView = (TextView) findViewById(R.id.textViewDistance);

		editUtm1.setOnKeyListener(this);
		editUtm2.setOnKeyListener(this);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		try {
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
