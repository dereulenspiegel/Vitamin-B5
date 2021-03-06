package de.akuz.android.utmumrechner.test;

import junit.framework.Assert;

import com.jayway.android.robotium.solo.Solo;

import de.akuz.android.utmumrechner.UTMUmrechnerActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Spinner;

public class UTMUmrechnerActivityTestCase extends
		ActivityInstrumentationTestCase2<UTMUmrechnerActivity> {

	private Solo solo;

	public UTMUmrechnerActivityTestCase() {
		super("de.akuz.android.utmumrechner", UTMUmrechnerActivity.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testSelectGPSFormat() throws Exception {
		Assert.assertTrue(solo.isSpinnerTextSelected("Dezimalgrad"));
		solo.pressSpinnerItem(0, 1);
		Assert.assertTrue(solo.isSpinnerTextSelected("Grad Dezimalminuten"));
		solo.pressSpinnerItem(0, 2);
		Assert.assertTrue(solo.isSpinnerTextSelected("Grad Minuten Sekunden"));
	}
	
	public void testAdHocConversion() throws Exception {
		String utmCoordinates = "";
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (solo != null) {
			solo.finishOpenedActivities();
		}
	}

}
