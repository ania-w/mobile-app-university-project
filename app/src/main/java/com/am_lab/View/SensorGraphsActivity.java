package com.am_lab.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.am_lab.Model.DataModel.GraphModel;
import com.am_lab.R;
import com.am_lab.ViewModel.ReceivedDataViewModel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;

import java.util.Timer;
import java.util.TimerTask;

public class SensorGraphsActivity  extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    //region timer
    private Timer requestTimer;
    private long requestTimerTimeStamp = 0;
    private long requestTimerPreviousTime = -1;
    private boolean requestTimerFirstRequest = true;
    private boolean requestTimerFirstRequestAfterStop;
    private TimerTask requestTimerTask;
    private final Handler handler = new Handler();
    //endregion

    //ViewModel
    private ReceivedDataViewModel viewModel;


    //region graph
    int samplingTime;
    int[] rangeX = {0, 100};
    int[] range_temperature = {0, 100};
    int[] range_pressure = {-260, 1260};
    int[] range_humidity = {0, 100};
    double temperature_point;
    double pressure_point;
    double humidity_point;
    public GraphModel sensorGraphModel = new GraphModel();
    Boolean initLegend=true;
    //endregion


    //region checkboxes used to determine which graph to display
    CheckBox checkTemp;
    CheckBox checkHum;
    CheckBox checkPress;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        Button btn = (Button) findViewById(R.id.btnShow);

        viewModel = new ViewModelProvider(this).get(ReceivedDataViewModel.class);

        //region getting data from settings
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        samplingTime = Integer.parseInt(sh.getString("Sampling_Time", "800"));
        viewModel.setUrl(sh.getString("IP_address","null"));
        //endregion

        //region UI initialization
        sensorGraphModel.init(samplingTime, rangeX, range_temperature, "Sensors Graph", "t[s]", (GraphView) findViewById(R.id.dataGraph_sensors));
        sensorGraphModel.dataGraph.getGridLabelRenderer().setNumVerticalLabels(20);
        TextView samplingTimeTextView = (TextView) findViewById(R.id.samplingTime);
        samplingTimeTextView.setText(Integer.toString(samplingTime));
        checkTemp = (CheckBox) findViewById(R.id.temp_check);
        checkHum = (CheckBox) findViewById(R.id.humidity_check);
        checkPress = (CheckBox) findViewById(R.id.pressure_check);
        //endregion

        //region menu listener
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(SensorGraphsActivity.this, v);
                popup.setOnMenuItemClickListener(SensorGraphsActivity.this);
                popup.inflate(R.menu.popup_menu);
                popup.show();

            }
        });
        //endregion

        //data update
        observerViewModel();
    }

    /**
     * observator made to update every sensor data variable on data change
     */
    private void observerViewModel() {
        viewModel.data.observe(this, imu -> {
            if (imu != null) {
                temperature_point = viewModel.getSensorList().get(0).getValue();
                pressure_point = viewModel.getSensorList().get(2).getValue();
                humidity_point = viewModel.getSensorList().get(1).getValue();
            }
        });
    }

    /**
     * OnClick method,
     * changes sampling time based on users input
     * @param view
     */
    public void setSamplingTime(View view) {

        //takes input from EditText
        EditText samplingTimeText = (EditText) findViewById(R.id.textInputEditText);
        samplingTime = Integer.parseInt(samplingTimeText.getText().toString());

        //Sets TextView sampling time display on the screen
        TextView samplingTimeTextView = (TextView) findViewById(R.id.samplingTime);
        samplingTimeTextView.setText(Integer.toString(samplingTime));

        //update of the sampling time
        sensorGraphModel.setSamplingTime(samplingTime);
        stopRequestTimerTask();
        startRequestTimer();
    }

    /**
     * OnClick method,
     * clears the graph screen and reinitialises it
     * @param view
     */
    public void resetSensorGraph(View view){

        //clear graph
        sensorGraphModel.dataGraph.removeAllSeries();
        sensorGraphModel.dataGraph.getSecondScale().removeAllSeries();
        sensorGraphModel.dataGraph.clearSecondScale();

        //init default graph
        sensorGraphModel.init(samplingTime, rangeX, range_temperature, " ", "t[s]", (GraphView) findViewById(R.id.dataGraph_sensors));
        sensorGraphModel.dataGraph.getGridLabelRenderer().setNumVerticalLabels(20);

        //reset timer data
        requestTimerTimeStamp = 0;
        requestTimerPreviousTime = -1;
        requestTimerFirstRequest = true;
        initLegend=true;
    }

    //region timer handling
    public void startGraph(View view) {
        startRequestTimer();
    }
    public void stopGraph(View view) {
        stopRequestTimerTask();
    }

    //new timer task initialization
    private void startRequestTimer() {
        if (requestTimer == null) {
            // set a new Timer
            requestTimer = new Timer();

            // initialize the TimerTask's job
            initializeRequestTimerTask();
            requestTimer.schedule(requestTimerTask, 0, samplingTime);
        }
    }

    //cancel ongoing task
    private void stopRequestTimerTask() {
        // stop the timer, if it's not already null
        if (requestTimer != null) {
            requestTimer.cancel();
            requestTimer = null;
            requestTimerFirstRequestAfterStop = true;
        }
    }

    private long getValidTimeStampIncrease ( long currentTime)
    {
        // Right after start remember current time and return 0
        if (requestTimerFirstRequest) {
            requestTimerPreviousTime = currentTime;
            requestTimerFirstRequest = false;
            return 0;
        }

        // After each stop return value not greater than sample time
        // to avoid "holes" in the plot
        if (requestTimerFirstRequestAfterStop) {
            if ((currentTime - requestTimerPreviousTime) > 100)
                requestTimerPreviousTime = currentTime - 100;

            requestTimerFirstRequestAfterStop = false;
        }

        // If time difference is equal zero after start
        // return sample time
        if ((currentTime - requestTimerPreviousTime) == 0)
            return 100;

        // Return time difference between current and previous request
        return (currentTime - requestTimerPreviousTime);
    }

    private void initializeRequestTimerTask () {
        requestTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        update();
                    }
                });
            }
        };
    }
    //endregion


    /**
     * Updates all graphs, updates data via viewmodel refreshing, adds legend
     */
    public void update() {
        viewModel.refresh();

        if (requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // update timestamp
            double timeStamp = requestTimerTimeStamp / 100.0; // [sec]
            boolean scrollGraph = (timeStamp > 100);

            //update graph series
            sensorGraphModel.updateFirst(timeStamp,  temperature_point, scrollGraph);
            sensorGraphModel.updateSecond(timeStamp,  pressure_point, scrollGraph);
            sensorGraphModel.updateThird(timeStamp,  humidity_point, scrollGraph);

            //checking which checkbox is checked and adding corresponding data series
            if (!(checkTemp.isChecked() && checkPress.isChecked() && checkHum.isChecked())&&initLegend) {

                 if (checkTemp.isChecked()&&checkHum.isChecked()){

                    sensorGraphModel.dataGraph.addSeries(sensorGraphModel.dataSeries1);
                    sensorGraphModel.setTitleY("Temperature [C]");
                    sensorGraphModel.dataGraph.getViewport().setMinY(range_temperature[0]);
                    sensorGraphModel.dataGraph.getViewport().setMaxY(range_temperature[1]);

                    sensorGraphModel.dataGraph.getSecondScale().addSeries(sensorGraphModel.dataSeries3);
                    sensorGraphModel.dataGraph.getSecondScale().setVerticalAxisTitle("Humidity [%]");
                    sensorGraphModel.dataGraph.getSecondScale().setVerticalAxisTitleTextSize(14);
                    sensorGraphModel.dataGraph.getSecondScale().setMinY(range_humidity[0]);
                    sensorGraphModel.dataGraph.getSecondScale().setMaxY(range_humidity[1]);
                }
                if (checkTemp.isChecked()&&checkPress.isChecked()){

                    sensorGraphModel.dataGraph.addSeries(sensorGraphModel.dataSeries1);
                    sensorGraphModel.setTitleY("Temperature [C]");
                    sensorGraphModel.dataGraph.getViewport().setMinY(range_temperature[0]);
                    sensorGraphModel.dataGraph.getViewport().setMaxY(range_temperature[1]);

                    sensorGraphModel.dataGraph.getSecondScale().addSeries(sensorGraphModel.dataSeries2);
                    sensorGraphModel.dataGraph.getSecondScale().setMinY(range_pressure[0]);
                    sensorGraphModel.dataGraph.getSecondScale().setMaxY(range_pressure[1]);
                }

                if (checkHum.isChecked()&&checkPress.isChecked()){
                    sensorGraphModel.dataGraph.addSeries(sensorGraphModel.dataSeries3);
                    sensorGraphModel.setTitleY("Humidity [%]");
                    sensorGraphModel.dataGraph.getViewport().setMinY(range_humidity[0]);
                    sensorGraphModel.dataGraph.getViewport().setMaxY(range_humidity[1]);

                    sensorGraphModel.dataGraph.getSecondScale().addSeries(sensorGraphModel.dataSeries2);
                    sensorGraphModel.dataGraph.getSecondScale().setMinY(range_pressure[0]);
                    sensorGraphModel.dataGraph.getSecondScale().setMaxY(range_pressure[1]);
                }

                if (checkTemp.isChecked()&&!checkHum.isChecked()&&!checkPress.isChecked()){
                    sensorGraphModel.dataGraph.addSeries(sensorGraphModel.dataSeries1);
                    sensorGraphModel.setTitleY("Temperature [C]");
                    sensorGraphModel.dataGraph.getViewport().setMinY(range_temperature[0]);
                    sensorGraphModel.dataGraph.getViewport().setMaxY(range_temperature[1]);
                    sensorGraphModel.dataGraph.getSecondScale().removeAllSeries();
                }
                if (checkHum.isChecked()&&!checkTemp.isChecked()&&!checkPress.isChecked()){
                    sensorGraphModel.dataGraph.addSeries(sensorGraphModel.dataSeries3);
                    sensorGraphModel.setTitleY("Humidity [%]");
                    sensorGraphModel.dataGraph.getViewport().setMinY(range_humidity[0]);
                    sensorGraphModel.dataGraph.getViewport().setMaxY(range_humidity[1]);
                }
                if (!checkHum.isChecked()&&!checkTemp.isChecked()&&checkPress.isChecked()){
                    sensorGraphModel.dataGraph.addSeries(sensorGraphModel.dataSeries2);
                    sensorGraphModel.setTitleY("Pressure [hPa]");
                    sensorGraphModel.dataGraph.getViewport().setMinY(range_pressure[0]);
                    sensorGraphModel.dataGraph.getViewport().setMaxY(range_pressure[1]);
                }
        }

            //if legend is not initialised, and add legend
            if(initLegend)
            {
                sensorGraphModel.dataGraph.getLegendRenderer().setVisible(true);
                sensorGraphModel.dataGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                sensorGraphModel.setAllTitles("Temperature [C]","Pressure [hPa]","Humidity [%]");
                initLegend=false;
            }

            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }


    /**
     * Menu
     * @param item item of the menu list
     * @return true if id is correct
     */
        @Override
        public boolean onMenuItemClick (MenuItem item){
            Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            Intent switchActivityIntent;
            switch (item.getItemId()) {
                case R.id.List:
                    switchActivityIntent = new Intent(SensorGraphsActivity.this, DynamicListActivity.class);
                    startActivity(switchActivityIntent);
                    return true;
                case R.id.Imu:
                    switchActivityIntent = new Intent(SensorGraphsActivity.this, ImuGraphsActivity.class);
                    startActivity(switchActivityIntent);
                    return true;
                case R.id.Joystick:
                    switchActivityIntent = new Intent(SensorGraphsActivity.this, JoystickActivity.class);
                    startActivity(switchActivityIntent);
                    return true;
                case R.id.Led:
                    switchActivityIntent = new Intent(SensorGraphsActivity.this, LedScreenActivity.class);
                    startActivity(switchActivityIntent);
                    return true;
                case R.id.Sensors:
                    switchActivityIntent = new Intent(SensorGraphsActivity.this, SensorGraphsActivity.class);
                    startActivity(switchActivityIntent);
                    return true;
                case R.id.Settings:
                    switchActivityIntent = new Intent(SensorGraphsActivity.this, SettingsActivity.class);
                    startActivity(switchActivityIntent);
                    return true;
                default:
                    return false;
            }
        }
    }

