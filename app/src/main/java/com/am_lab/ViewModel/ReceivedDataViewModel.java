package com.am_lab.ViewModel;



import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.am_lab.Model.DataModel.DataModel;
import com.am_lab.Model.IOData.IODataService;
import com.am_lab.Model.DataModel.AngleModel;
import com.am_lab.Model.DataModel.JoystickModel;
import com.am_lab.Model.DataModel.SensorModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class ReceivedDataViewModel extends ViewModel {

    String url;

    public MutableLiveData<DataModel> data = new MutableLiveData<DataModel>();

    public IODataService DataService;

    private CompositeDisposable disposable = new CompositeDisposable();

    public void setUrl(String _url){
        url=_url;
        DataService = IODataService.getInstance(url);
    }

    public void refresh() {

        disposable.add(
                DataService.getData()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DataModel>() {

                            @Override
                            public void onSuccess(DataModel _DataModel) {
                                data.setValue(_DataModel);
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }
                        })
        );
    }

    public List<SensorModel> getSensorList()
    {
        return data.getValue().getSensors();
    }
    public List<JoystickModel> getJoystickList()
    {
        return data.getValue().getJoystick();
    }
    public List<AngleModel> getAnglesList()
    {
        return data.getValue().getAngles();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}