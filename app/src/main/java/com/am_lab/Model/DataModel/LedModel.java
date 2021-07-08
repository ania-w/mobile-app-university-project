package com.am_lab.Model.DataModel;

public class LedModel {

    //region LedModel variables
    String LED;
    String RGB;
    //endregion

    //region getters+setters
    public String getXY() {
        return LED;
    }
    public void setXY(String XY) {
        this.LED = XY;
    }
    public String getRGB() {
        return RGB;
    }
    public void setRGB(String RGB) {
        this.RGB = RGB;
    }
    //endregion
}
