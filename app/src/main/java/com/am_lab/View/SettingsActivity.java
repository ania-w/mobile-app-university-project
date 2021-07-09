package com.am_lab.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.am_lab.R;

import java.io.File;
import java.text.BreakIterator;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //go to fragment for this activity
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, new MySettingsFragment())
                .commit();

        }


    /**
     * inflate popup menu in default action bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    /**
     * Menu
     * @param item item of the menu list
     * @return true if id is correct
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent switchActivityIntent;

        switch (item.getItemId()) {
            case R.id.List:
                switchActivityIntent = new Intent(SettingsActivity.this, DynamicListActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Imu:
                switchActivityIntent = new Intent(SettingsActivity.this, ImuGraphsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Joystick:
                switchActivityIntent = new Intent(SettingsActivity.this, JoystickActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Led:
                switchActivityIntent = new Intent(SettingsActivity.this, LedScreenActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Sensors:
                switchActivityIntent = new Intent(SettingsActivity.this, SensorGraphsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Settings:
                switchActivityIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            default: return false;
        }
    }
}

