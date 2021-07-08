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

public class ImuGraphsActivity  extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    //timer
    private Timer requestTimer;
    private long requestTimerTimeStamp = 0;
    private long requestTimerPreviousTime = -1;
    private boolean requestTimerFirstRequest = true;
    private boolean requestTimerFirstRequestAfterStop;
    private TimerTask requestTimerTask;
    private final Handler handler = new Handler();

    //ViewModel
    private ReceivedDataViewModel viewModel;

    //graph
    int samplingTime;
    int[] rangeX = {0, 100};
    int[] rangeY = {-180, 180};
    double roll_point;
    double pitch_point;
    double yaw_point;
    public GraphModel imuGraphModel = new GraphModel();
    Boolean initLegend=true;

    //interface
    CheckBox checkRoll;
    CheckBox checkPitch;
    CheckBox checkYaw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imu);
        Button btn = (Button) findViewById(R.id.btnShow);

        viewModel = new ViewModelProvider(this).get(ReceivedDataViewModel.class);


        imuGraphModel.init(samplingTime, rangeX, rangeY, "Imu RPY Graph", "t[s]", (GraphView) findViewById(R.id.dataGraph_imu));

        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        samplingTime = Integer.parseInt(sh.getString("Sampling_Time", "800"));
        viewModel.setUrl(sh.getString("IP_address","null"));
        checkRoll = (CheckBox) findViewById(R.id.roll_check);
        checkPitch = (CheckBox) findViewById(R.id.pitch_check);
        checkYaw = (CheckBox) findViewById(R.id.yaw_check);
        viewModel.refresh();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ImuGraphsActivity.this, v);
                popup.setOnMenuItemClickListener(ImuGraphsActivity.this);
                popup.inflate(R.menu.popup_menu);
                popup.show();

            }
        });
        observerViewModel();

        TextView samplingTimeTextView = (TextView) findViewById(R.id.samplingTime);
        samplingTimeTextView.setText(Integer.toString(samplingTime));

    }

    private void observerViewModel() {

        viewModel.data.observe(this, imu -> {
            if (imu != null) {
                double roll_temp = viewModel.getAnglesList().get(0).getValue();
                double pitch_temp = viewModel.getAnglesList().get(2).getValue();
                double yaw_temp = viewModel.getAnglesList().get(1).getValue();
                roll_point=(roll_temp<180)?roll_temp:(-(360-roll_temp));
                pitch_point=(pitch_temp<180)?pitch_temp:(-(360-pitch_temp));
                yaw_point=(yaw_temp<180)?yaw_temp:(-(360-yaw_temp));
            }
        });
    }

    public void setSamplingTime(View view) {
        EditText samplingTimeText = (EditText) findViewById(R.id.textInputEditText);
        samplingTime = Integer.parseInt(samplingTimeText.getText().toString());


        imuGraphModel.setSamplingTime(samplingTime);
        TextView samplingTimeTextView = (TextView) findViewById(R.id.samplingTime);
        samplingTimeTextView.setText(Integer.toString(samplingTime));
        stopRequestTimerTask();
        startRequestTimer();

    }

    public void startGraph(View view) {
        startRequestTimer();
    }

    public void stopGraph(View view) {
        stopRequestTimerTask();
    }

    private void startRequestTimer() {
        if (requestTimer == null) {
            // set a new Timer
            requestTimer = new Timer();

            // initialize the TimerTask's job
            initializeRequestTimerTask();
            requestTimer.schedule(requestTimerTask, 0, samplingTime);
        }
    }

    /**
     * @brief Stops request timer (if currently exist)
     * and sets 'requestTimerFirstRequestAfterStop' flag.
     */

    private void stopRequestTimerTask() {
        // stop the timer, if it's not already null
        if (requestTimer != null) {
            requestTimer.cancel();
            requestTimer = null;
            requestTimerFirstRequestAfterStop = true;
        }
    }

    public void resetSensorGraph(View view){
        imuGraphModel.dataGraph.removeAllSeries();
        imuGraphModel.dataGraph.getSecondScale().removeAllSeries();

        imuGraphModel.setDataSeries();

        imuGraphModel.init(samplingTime, rangeX, rangeY, "Imu RPY Graph", "t[s]", (GraphView) findViewById(R.id.dataGraph_imu));

        requestTimerTimeStamp = 0;
        requestTimerPreviousTime = -1;
        requestTimerFirstRequest = true;
        initLegend=true;


    }

    public void update() {
        viewModel.refresh();

        imuGraphModel.setAllTitles("Roll [deg]","Pitch [deg]","Yaw [deg]");

        if (requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // update plot series
            double timeStamp = requestTimerTimeStamp / 100.0; // [sec]
            boolean scrollGraph = (timeStamp > 100);

            imuGraphModel.updateFirst(timeStamp, roll_point, scrollGraph);
            imuGraphModel.updateSecond(timeStamp,  pitch_point, scrollGraph);
            imuGraphModel.updateThird(timeStamp, yaw_point, scrollGraph);

            if (!(checkRoll.isChecked() && checkPitch.isChecked() && checkYaw.isChecked())&&initLegend) {

                if (checkRoll.isChecked()&&checkYaw.isChecked()){

                    imuGraphModel.dataGraph.addSeries(imuGraphModel.dataSeries1);

                    imuGraphModel.dataGraph.getSecondScale().addSeries(imuGraphModel.dataSeries3);
                }
                if (checkRoll.isChecked()&&checkPitch.isChecked()){

                    imuGraphModel.dataGraph.addSeries(imuGraphModel.dataSeries1);

                    imuGraphModel.dataGraph.getSecondScale().addSeries(imuGraphModel.dataSeries2);
                }

                if (checkYaw.isChecked()&&checkPitch.isChecked()){
                    imuGraphModel.dataGraph.addSeries(imuGraphModel.dataSeries3);

                    imuGraphModel.dataGraph.getSecondScale().addSeries(imuGraphModel.dataSeries2);
                }

                if (checkRoll.isChecked()&&!checkYaw.isChecked()&&!checkPitch.isChecked()){
                    imuGraphModel.dataGraph.addSeries(imuGraphModel.dataSeries1);
                }
                if (checkYaw.isChecked()&&!checkRoll.isChecked()&&!checkPitch.isChecked()){
                    imuGraphModel.dataGraph.addSeries(imuGraphModel.dataSeries3);
                }
                if (!checkYaw.isChecked()&&!checkRoll.isChecked()&&checkPitch.isChecked()){
                    imuGraphModel.dataGraph.addSeries(imuGraphModel.dataSeries2);
                }


            }
            if(initLegend)
            {
                imuGraphModel.dataGraph.getLegendRenderer().setVisible(true);
                imuGraphModel.dataGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                imuGraphModel.dataGraph.getViewport().setMinY(rangeY[0]);
                imuGraphModel.dataGraph.getViewport().setMaxY(rangeY[1]);
                imuGraphModel.dataGraph.getSecondScale().setMaxY(rangeY[1]);
                imuGraphModel.dataGraph.getSecondScale().setMaxY(rangeY[0]);
                initLegend=false;
            }

            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
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

    @Override
    public boolean onMenuItemClick (MenuItem item){
        Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        Intent switchActivityIntent;
        switch (item.getItemId()) {
            case R.id.List:
                switchActivityIntent = new Intent(ImuGraphsActivity.this, DynamicListActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Imu:
                switchActivityIntent = new Intent(ImuGraphsActivity.this, ImuGraphsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Joystick:
                switchActivityIntent = new Intent(ImuGraphsActivity.this, JoystickActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Led:
                switchActivityIntent = new Intent(ImuGraphsActivity.this, LedScreenActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Sensors:
                switchActivityIntent = new Intent(ImuGraphsActivity.this, SensorGraphsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Settings:
                switchActivityIntent = new Intent(ImuGraphsActivity.this, SettingsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            default:
                return false;
        }
    }
}

