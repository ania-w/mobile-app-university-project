package com.am_lab.Model.IOData;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.am_lab.Model.DataModel.DataModel;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Query;

public class IODataService {
    private String BASE_URL;

    private static IODataService instance;
    private APIService api;

    private IODataService(String _url) {

BASE_URL="http://"+_url;
api= new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(APIService.class);
    }

    public static IODataService getInstance(String url) {

        if (instance == null) {
            instance = new IODataService(url);
        }
        return instance;
    }

    public Single<DataModel> getData() {
        return api.getData();
    }

    public Single<ResponseBody>sendLedScreen(String json){
        return api.sendLedScreen(json);
    }
}