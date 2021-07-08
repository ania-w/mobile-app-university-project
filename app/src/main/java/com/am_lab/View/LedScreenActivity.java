package com.am_lab.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class LedScreenActivity  extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private LedScreenViewModel viewModel;
    GridLayout gridLayout;
    EditText colorInput;
    int color;
    TextView loading;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);
        Button btn = (Button) findViewById(R.id.btnShow);

        viewModel = new ViewModelProvider(this).get(LedScreenViewModel.class);
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        viewModel.setUrl(sh.getString("IP_address","null"));
        color=Integer.parseInt(sh.getString("Def_Led_Color","0"));
        colorInput= (EditText) findViewById(R.id.RGB);
    colorInput.setText(sh.getString("Default_LED_Color","0"));
        gridLayout=(GridLayout)findViewById(R.id.led_screen);
        setData();


        loading=findViewById(R.id.loadingView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(LedScreenActivity.this, v);
                popup.setOnMenuItemClickListener(LedScreenActivity.this);
                popup.inflate(R.menu.popup_menu);
                popup.show();

            }
        });
    }


    public void sendLedData(View view)
    {
        List<LedModel> ledList= viewModel.getLedList();
        String jsonList=new Gson().toJson(ledList);
        viewModel.refresh(jsonList,loading);
    }

    public void resetLedData(View view)
    {
        gridLayout.removeAllViews();
        viewModel.clearList();
        viewModel.refresh("[]",loading);
        setData();
    }

    public void setData() {

        int column=gridLayout.getColumnCount();
        int row = gridLayout.getRowCount();
        int total = column*row;
        for (int i = 0, c = 0, r = 0; i < total; i++, c++) {
            if (c == column) {
                c = 0;
                r++;
            }
            Button button = new Button(this);
            button.setText(Integer.toString(c)+Integer.toString(r));
            button.setId(Integer.parseInt(Integer.toString(c)+Integer.toString(r)));
            button.setTextSize(15);
            button.setBackgroundColor(0xFF9F9F9F);

            button.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String input=colorInput.getText().toString();
                    try{
                        color = Integer.parseInt(input,16);
                    }
                    catch(NumberFormatException err){
                        colorInput.setHint("Wrong value!");
                        color=0x9F9F9F;
                    }
                    finally {
                        List<LedModel> ledList= viewModel.getLedList();
                        for (LedModel ledModel : ledList) {
                            @SuppressLint("ResourceType")
                            String id=button.getId()<10?("0"+Integer.toString(button.getId())):Integer.toString(button.getId());
                            if (ledModel.getXY().equals(id)) {
                                button.setBackgroundColor(0xFF000000+color);
                                ledModel.setRGB(input.toUpperCase());
                                viewModel.setLedList(ledList);
                                break;
                            }
                        }
                    }

                }
            });

            GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.Spec colSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
            GridLayout.LayoutParams gridParam = new GridLayout.LayoutParams(
                    rowSpan, colSpan);
            gridParam.width = dp2px(this, 50.0f);
            gridLayout.addView(button,gridParam);
        }
    }
    public static int dp2px(Context ctx, float dp) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
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