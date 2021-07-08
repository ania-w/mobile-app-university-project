package com.am_lab.Model.DataModel;

import java.util.List;

public class DataModel {

    //region DataModel variables
    List<JoystickModel> joystick;
    List<SensorModel> sensors;
    List<AngleModel> angles;
    //endregion

    //region getters+setters
    public List<JoystickModel> getJoystick() {
        return joystick;
    }
    public void setJoystick(List<JoystickModel> joystick) {
        this.joystick = joystick;
    }
    public List<SensorModel> getSensors() {
        return sensors;
    }
    public void setSensors(List<SensorModel> sensors) {
        this.sensors = sensors;
    }
    public List<AngleModel> getAngles() {
        return angles;
    }
    public void setAngles(List<AngleModel> angles) { this.angles = angles; }
    //endregion
}

