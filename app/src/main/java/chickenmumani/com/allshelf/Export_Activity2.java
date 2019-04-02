package chickenmumani.com.allshelf;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import chickenmumani.com.allshelf.R;

public class Export_Activity2 extends PreferenceActivity  {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            toolbar.setTitle("안녕 나는 안드로이드야. 초면이네 반가워");
            addPreferencesFromResource(R.xml.pref_export);
    }

    public void setContentView(int layoutResID) {
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.activity_main, new LinearLayout(this), false);

        toolbar = (Toolbar) contentView.findViewById(R.id.toolbar);

        ViewGroup contentWrapper = (ViewGroup) contentView
                .findViewById(R.id.content);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);

        getWindow().setContentView(contentView);
    }
}
