package org.hekangping.easypic;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * ��ѡ��
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
