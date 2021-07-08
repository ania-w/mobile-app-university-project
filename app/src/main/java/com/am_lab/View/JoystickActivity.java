package com.am_lab.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class JoystickActivity  extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    //timer
    boolean shouldStopLoop = false;
    Handler mHandler = new Handler();
    Runnable runnable;

    //ViewModel
    private ReceivedDataViewModel viewModel;

    //graph
    int samplingTime;
    int[] range = {0, 7};
    int X;
    int Y;
    int CENTER;
    public GraphModel joystickGraphModel = new GraphModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);
        Button btn = (Button) findViewById(R.id.btnShow);

        viewModel = new ViewModelProvider(this).get(ReceivedDataViewModel.class);

        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        samplingTime = Integer.parseInt(sh.getString("Sampling_Time", "800"));
        viewModel.setUrl(sh.getString("IP_address","null"));
        joystickGraphModel.initPointGraph(samplingTime, range, range, "Y", "X", (GraphView) findViewById(R.id.dataGraph_joystick));
        viewModel.refresh();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(JoystickActivity.this, v);
                popup.setOnMenuItemClickListener(JoystickActivity.this);
                popup.inflate(R.menu.popup_menu);
                popup.show();

            }
        });


        observerViewModel();


    }

    private void observerViewModel() {

        viewModel.data.observe(this, data -> {
            if (data != null) {
                Y=Integer.parseInt(viewModel.getJoystickList().get(0).getX());
                X=Integer.parseInt(viewModel.getJoystickList().get(0).getY());
                CENTER=Integer.parseInt(viewModel.getJoystickList().get(0).getCenter());
            }
        });
    }

    public void startGraph(View view) {

        runnable = new Runnable() {
            @Override
            public void run() {
                update();
                if (!shouldStopLoop) {
                    mHandler.postDelayed(this, samplingTime);
                }
            }
        };
        mHandler.post(runnable);
    }

    public void stopGraph(View view) {
    mHandler.removeCallbacks(runnable);}

    public void update() {
        viewModel.refresh();
        joystickGraphModel.dataGraph.removeAllSeries();
        joystickGraphModel.initPointGraph(samplingTime, range, range, "Y", "X", (GraphView) findViewById(R.id.dataGraph_joystick));
        joystickGraphModel.dataGraph.addSeries(joystickGraphModel.pointDataSeries);
        joystickGraphModel.addPoint(X,Y);
        TextView centerText=findViewById(R.id.center_count);
        centerText.setText(Integer.toString(CENTER));
    }
    /**
     * @brief Stops request timer (if currently exist)
     * and sets 'requestTimerFirstRequestAfterStop' flag.
     */


    public void resetGraph(View view){
        joystickGraphModel.dataGraph.removeAllSeries();
        joystickGraphModel.initPointGraph(samplingTime, range, range, "Y", "X", (GraphView) findViewById(R.id.dataGraph_joystick));
        CENTER=0;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        Intent switchActivityIntent;
        switch (item.getItemId()) {
            case R.id.List:
                switchActivityIntent = new Intent(JoystickActivity.this, DynamicListActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Imu:
                switchActivityIntent = new Intent(JoystickActivity.this, ImuGraphsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Joystick:
                switchActivityIntent = new Intent(JoystickActivity.this, JoystickActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Led:
                switchActivityIntent = new Intent(JoystickActivity.this, LedScreenActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Sensors:
                switchActivityIntent = new Intent(JoystickActivity.this, SensorGraphsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Settings:
                switchActivityIntent = new Intent(JoystickActivity.this, SettingsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            default:
                return false;
        }
    }
}
