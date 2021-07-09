package com.am_lab.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.am_lab.Model.DataModel.LedModel;
import com.am_lab.R;
import com.am_lab.ViewModel.LedScreenViewModel;
import com.google.gson.Gson;

import java.util.List;

public class LedScreenActivity  extends AppCompatActivity {

    //viewmodel
    private LedScreenViewModel viewModel;


    //region UI elements
    GridLayout gridLayout;
    EditText colorInput;
    int color;
    TextView loading;
    //endregion



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        viewModel = new ViewModelProvider(this).get(LedScreenViewModel.class);

        //region getting data from settings
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        viewModel.setUrl(sh.getString("IP_address","null"));
        color=Integer.parseInt(sh.getString("Def_Led_Color","0"));
        //endregion

        //region set screen layout
        colorInput= (EditText) findViewById(R.id.RGB);
        colorInput.setText(sh.getString("Default_LED_Color","0"));
        gridLayout=(GridLayout)findViewById(R.id.led_screen);
        loading=findViewById(R.id.loadingView);
        setData();
        //endregion
    }


    /**
     * OnClick method,
     * Sends updated LedModel list via viewmodel to server
     * @param view
     */
    public void sendLedData(View view)
    {
        List<LedModel> ledList= viewModel.getLedList();
        String jsonList=new Gson().toJson(ledList);
        viewModel.refresh(jsonList,loading);
    }

    /**
     * sends "[]" to server via viewmodel, which clears SenseHat screen
     * sets app screen to default
     * @param view
     */
    public void resetLedData(View view)
    {
        gridLayout.removeAllViews();
        viewModel.clearList();
        viewModel.refresh("[]",loading);
        setData();
    }

    /**
     * Dynamic Led Matrix setup
     * GridLayout, 8x8, button id is its position in XY format
     * e.g. id=34 equals x=3, y=4
     * every button has OnClick listener, that changes its color according to user input, and updates viewmodels ledList
     *
     *
     */
    public void setData() {

        //parameters
        int column=gridLayout.getColumnCount();
        int row = gridLayout.getRowCount();
        int total = column*row;
        //dynamic ledmatrix
        for (int i = 0, c = 0, r = 0; i < total; i++, c++) {
            if (c == column) {
                c = 0;
                r++;
            }
            //button description
            Button button = new Button(this);
            button.setText(Integer.toString(c)+Integer.toString(r));
            //id: column number+row unmber
            button.setId(Integer.parseInt(Integer.toString(c)+Integer.toString(r)));
            button.setTextSize(15);
            //default color: grey
            button.setBackgroundColor(0xFF424242);

            //onClickListener
            button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //get user input
                    String input=colorInput.getText().toString();
                    try{
                        //check if user input is correct hex color
                        color = Integer.parseInt(input,16);
                    }
                    catch(NumberFormatException err){
                        //display message if input is incorrect hex color
                        colorInput.setHint("Wrong value!");
                        color=0x424242;
                    }
                    finally {
                        //change viewModel and matrix button list color,
                        List<LedModel> ledList= viewModel.getLedList();
                        for (LedModel ledModel : ledList) {
                            //if id is in range 0-9 add 0 in the beggining of a string (e.g. "7"->"07")
                            @SuppressLint("ResourceType")
                            String id=button.getId()<10?("0"+Integer.toString(button.getId())):Integer.toString(button.getId());
                            if (ledModel.getXY().equals(id)) {
                                //color is in RGB, so mask is needed
                                button.setBackgroundColor(0xFF000000+color);
                                ledModel.setRGB(input.toUpperCase());
                                viewModel.setLedList(ledList);
                                break;
                            }
                        }
                    }

                }
            });

            //region GridLayout initialisation
            GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.Spec colSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                    rowSpan, colSpan);
            gridParam.width = dp2px(this, 50.0f);
            gridLayout.addView(button,gridParam);
            //endregion
        }
    }

    /**
     * dp to pixels conversion
     * @param ctx activity context
     * @param dp dp in number
     * @return
     */
    public static int dp2px(Context ctx, float dp) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
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
                switchActivityIntent = new Intent(LedScreenActivity.this, DynamicListActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Imu:
                switchActivityIntent = new Intent(LedScreenActivity.this, ImuGraphsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Joystick:
                switchActivityIntent = new Intent(LedScreenActivity.this, JoystickActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Led:
                switchActivityIntent = new Intent(LedScreenActivity.this, LedScreenActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Sensors:
                switchActivityIntent = new Intent(LedScreenActivity.this, SensorGraphsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.Settings:
                switchActivityIntent = new Intent(LedScreenActivity.this, SettingsActivity.class);
                startActivity(switchActivityIntent);
                return true;
            default:
                return false;
        }
    }
}
