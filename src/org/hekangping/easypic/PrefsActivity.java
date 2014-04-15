package org.hekangping.easypic;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Ê×Ñ¡Ïî
 * 
 * @author developer
 */
public class PrefsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}

}
