package com.am_lab.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
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

public class JoystickActivity  extends AppCompatActivity{


    //region timer
    boolean shouldStopLoop = false;
    Handler mHandler = new Handler();
    Runnable runnable;
    //endregion

    //ViewModel
    private ReceivedDataViewModel viewModel;

    //region graph
    int samplingTime;
    int[] range = {0, 7};
    int X;
    int Y;
    int CENTER;
    public GraphModel joystickGraphModel = new GraphModel();
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);

        viewModel = new ViewModelProvider(this).get(ReceivedDataViewModel.class);

        //region getting data from settings
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        samplingTime = Integer.parseInt(sh.getString("Sampling_Time", "800"));
        viewModel.setUrl(sh.getString("IP_address","null"));
        //endregion

        //init graph
        joystickGraphModel.initPointGraph(samplingTime, range, range, "Y", "X", (GraphView) findViewById(R.id.dataGraph_joystick));
        joystickGraphModel.dataGraph.getGridLabelRenderer().setNumVerticalLabels(8);
        joystickGraphModel.dataGraph.getGridLabelRenderer().setNumHorizontalLabels(8);

        //data update
        observerViewModel();
    }

    /**
     * observator made to update joystick position and center counter
     */
    private void observerViewModel() {
        viewModel.data.observe(this, data -> {
            if (data != null) {
                Y=Integer.parseInt(viewModel.getJoystickList().get(0).getX());
                X=Integer.parseInt(viewModel.getJoystickList().get(0).getY());
                CENTER=Integer.parseInt(viewModel.getJoystickList().get(0).getCenter());
            }
        });
    }

    //region timer handling
    //new timer task initialization
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

    //cancel ongoin timer
    public void stopGraph(View view) {
    mHandler.removeCallbacks(runnable);}
    //endregion

    /**
     * update joystick point position and center counter
     */
    public void update() {
        viewModel.refresh();

        //remove last data and add new
        joystickGraphModel.dataGraph.removeAllSeries();
        joystickGraphModel.initPointGraph(samplingTime, range, range, "Y", "X", (GraphView) findViewById(R.id.dataGraph_joystick));
        joystickGraphModel.dataGraph.addSeries(joystickGraphModel.pointDataSeries);
        joystickGraphModel.addPoint(X,Y);

        //center counter
        TextView centerText=findViewById(R.id.center_count);
        centerText.setText(Integer.toString(CENTER));
    }

    /**
     * OnClick method,
     * reinitialises graph, and sets counter to 0
     * @param view
     */
    public void resetGraph(View view){
        joystickGraphModel.dataGraph.removeAllSeries();
        joystickGraphModel.initPointGraph(samplingTime, range, range, "Y", "X", (GraphView) findViewById(R.id.dataGraph_joystick));
        CENTER=0;
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
