package com.am_lab.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.am_lab.R;
import com.am_lab.View.Adapters.AnglesAdapter;
import com.am_lab.View.Adapters.JoystickAdapter;
import com.am_lab.View.Adapters.SensorAdapter;
import com.am_lab.ViewModel.ReceivedDataViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DynamicListActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    //ViewModel
    private ReceivedDataViewModel viewModel;

    //List for every data category
    @BindView(R.id.ImuList)
    RecyclerView ImuList;
    @BindView(R.id.SensorsList_xml)
    RecyclerView SensorsList;
    @BindView(R.id.JoystickList)
    RecyclerView JoystickList;

    //Adapters for each list
    private SensorAdapter sensorAdapter = new SensorAdapter(new ArrayList<>());
    private AnglesAdapter anglesAdapter = new AnglesAdapter(new ArrayList<>());
    private JoystickAdapter joystickAdapter = new JoystickAdapter(new ArrayList<>());

    //parameters
    int samplingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ButterKnife.bind(this);
        viewModel = new ViewModelProvider(this).get(ReceivedDataViewModel.class);

        //region getting data from settings
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        samplingTime = Integer.parseInt(sh.getString("Sampling_Time", "800"));
        viewModel.setUrl(sh.getString("IP_address","null"));
        //endregion

        //region adapter setup
        ImuList.setLayoutManager(new LinearLayoutManager(this));
        ImuList.setAdapter(anglesAdapter);
        SensorsList.setLayoutManager(new LinearLayoutManager(this));
        SensorsList.setAdapter(sensorAdapter);
        JoystickList.setLayoutManager(new LinearLayoutManager(this));
        JoystickList.setAdapter(joystickAdapter);
        //endregion

        //region refreshing list every *sampling time* milliseconds
        boolean shouldStopLoop = false;
        Handler mHandler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                viewModel.refresh();
                if (!shouldStopLoop) {
                    mHandler.postDelayed(this, samplingTime);
                }
            }
        };

        mHandler.post(runnable);
        //endregion

        //region menu listener
        Button btn = (Button) findViewById(R.id.btnShow);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(DynamicListActivity.this, v);
                popup.setOnMenuItemClickListener(DynamicListActivity.this);
                popup.inflate(R.menu.popup_menu);
                popup.show();

            }
        });
        //endregion

        //data update
        observerViewModel();
    }

    /**
     * observator made to update every adapter data on data change
     */
    private void observerViewModel() {
        viewModel.data.observe(this, dataModels -> {
            if (dataModels != null) {
                sensorAdapter.update(dataModels);
               anglesAdapter.update(dataModels);
                joystickAdapter.update(dataModels);
            }
        });
    }

    /**
     * Menu
     * @param item item of the menu list
     * @return true if id is correct
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        Intent switchActivityIntent;
        switch (item.getItemId()) {
            case R.id.List:
                switchActivityIntent = new Intent(DynamicListActivity.this, DynamicListActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Imu:
                switchActivityIntent = new Intent(DynamicListActivity.this, ImuGraphsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Joystick:
                switchActivityIntent = new Intent(DynamicListActivity.this, JoystickActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Led:
                switchActivityIntent = new Intent(DynamicListActivity.this, LedScreenActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Sensors:
                switchActivityIntent = new Intent(DynamicListActivity.this, SensorGraphsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Settings:
                switchActivityIntent = new Intent(DynamicListActivity.this, SettingsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            default:
                return false;
        }
    }
}
