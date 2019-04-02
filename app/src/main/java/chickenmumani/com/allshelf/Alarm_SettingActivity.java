package chickenmumani.com.allshelf;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import chickenmumani.com.allshelf.R;

public class Alarm_SettingActivity extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_alarm);
    }

    public void onResume(){
        super.onResume();
        ((Navi_Activity) getActivity())
                .setActionBarTitle("알림");

    }

}