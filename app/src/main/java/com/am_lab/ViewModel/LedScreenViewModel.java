package com.am_lab.ViewModel;

import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.am_lab.Model.DataModel.LedModel;
import com.am_lab.Model.IOData.IODataService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class LedScreenViewModel extends ViewModel {
    String url;

    public MutableLiveData<List<LedModel>> ledScreen = new MutableLiveData<>();

    public IODataService DataService;

    private CompositeDisposable disposable = new CompositeDisposable();

    /**
     * set url for server request
     * @param _url
     */
    public void setUrl(String _url)
    {
        url=_url;
        DataService = IODataService.getInstance(url);
    }

    /**
     * build new led list
     * Set ledScreen to default color
     */
    public void clearList()
    {
        List<LedModel> blankList=new ArrayList<>();
        int column=8;
        int row = 8;
        int total = column*row;
        for (int i = 0, c = 0, r = 0; i < total; i++, c++) {
            if (c == column) {
                c = 0;
                r++;
            }

            LedModel temp = new LedModel();
            temp.setRGB("424242");
            temp.setXY(Integer.toString(c)+Integer.toString(r));
            blankList.add(temp);

        }
        ledScreen.setValue(blankList);
    }

    public LedScreenViewModel()
    {
        //sey default list
        clearList();
    }

    public void refresh(String jsonList,TextView loading) {
        loading.setText("loading");
        disposable.add(
                DataService.sendLedScreen(jsonList)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                            @Override
                            public void onSuccess(@org.jetbrains.annotations.NotNull ResponseBody responseBody) {
                                loading.setText("loaded");
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                loading.setText("error: "+e.toString());
                            }
                        })
        );
    }

    //region get LedModel data
    public List<LedModel> getLedList() { return ledScreen.getValue();  }
    public void setLedList(List<LedModel> ledList) {ledScreen.setValue(ledList); }
    //endregion


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
