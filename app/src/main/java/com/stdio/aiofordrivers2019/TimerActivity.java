package com.stdio.aiofordrivers2019;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TimerActivity extends AppCompatActivity {

    private final CompositeDisposable disposables = new CompositeDisposable();

    int minutPrice = 0;
    int orderPrice = 0;
    int order = 0;
    int waitMinut = 0;

    int seconds = 0;
    int minutes = 0;
    TextView tvTimer, tvPrice;

    int currentPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        tvTimer = findViewById(R.id.tvTimer);
        tvPrice = findViewById(R.id.tvPrice);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                minutPrice = 0;
            } else {
                minutPrice = extras.getInt("minutPrice");
                orderPrice = extras.getInt("orderPrice");
                order = extras.getInt("order");
                waitMinut = extras.getInt("waitMinut");
            }
        } else {
            minutPrice = (int) savedInstanceState.getSerializable("minutPrice");
            orderPrice = (int) savedInstanceState.getSerializable("orderPrice");
            order = (int) savedInstanceState.getSerializable("order");
            waitMinut = (int) savedInstanceState.getSerializable("waitMinut");
        }

        Log.e("666", minutPrice  + " " + orderPrice + " " + order + " " + waitMinut);
        tvPrice.setText("Стоимость доставки: " + (orderPrice + currentPrice));

        doSomeWork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear(); // clearing it : do not emit after destroy
    }

    /*
     * simple example using interval to run task at an interval of 2 sec
     * which start immediately
     */
    private void doSomeWork() {
        disposables.add(getObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver()));
    }

    private Observable<? extends Long> getObservable() {
        return Observable.interval(0, 1, TimeUnit.SECONDS);
    }

    private DisposableObserver<Long> getObserver() {
        return new DisposableObserver<Long>() {

            @Override
            public void onNext(Long value) {
                seconds += 1;
                if (seconds >= 60) {
                    minutes++;
                    seconds = seconds % 60;

                    if (minutes > waitMinut) {
                        currentPrice += minutPrice;
                        tvPrice.setText("Стоимость доставки: " + (orderPrice + currentPrice));
                    }
                }
                String strSeconds = (seconds >= 10) ? seconds + "" : "0" + seconds;
                String strMinutes = (minutes >= 10) ? minutes + "" : "0" + minutes;
                tvTimer.setText(strMinutes + ":" + strSeconds);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }
}
