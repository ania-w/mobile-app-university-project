package com.am_lab.Model.IOData;

import com.am_lab.Model.DataModel.DataModel;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    /**
     * @return Returns RPi sensor, joystick, and imu data
     */
    @GET("cgi-bin/data_json_emu_sensehat.py")
    Single<DataModel> getData();

    /**
     * @param json list of LedModel objects
     * @return nothing valuable
     */
    @GET("/cgi-bin/led.py")
    Single<ResponseBody>sendLedScreen(@Query("JSON") String json);

}