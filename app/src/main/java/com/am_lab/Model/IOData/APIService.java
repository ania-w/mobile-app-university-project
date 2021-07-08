package com.am_lab.Model.IOData;

import com.am_lab.Model.DataModel.DataModel;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {
    @GET("cgi-bin/data_json_emu_sensehat.py")
    Single<DataModel> getData();

    @GET("/cgi-bin/led.py")
    Single<ResponseBody>sendLedScreen(@Query("JSON") String json);

}