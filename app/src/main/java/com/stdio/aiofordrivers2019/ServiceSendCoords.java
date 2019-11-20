package com.stdio.aiofordrivers2019;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Calendar;



public class ServiceSendCoords extends Service {


    Calendar cur_cal = Calendar.getInstance();
    int t=0;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();




        Intent intent = new Intent(this, GPSTracker.class);

        PendingIntent pintent = PendingIntent.getService(getApplicationContext(),
                0, intent, 0);




        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cur_cal.getTimeInMillis(),
                120000, pintent);



        Log.e("666", "ServiceSendCoords -Создание службы" );
    }



    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);


        t++;
        getApplicationContext().startService(new Intent(getApplicationContext(), GPSTracker.class));
        Log.e("666", "ServiceSendCoords -Создание службы " + t);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy()
    {
        Log.e("ServiceSendCoords", "Служба остановлена");


        Intent intent = new Intent(this, ServiceSendCoords.class);
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(),
                0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        alarm.cancel(pintent);
    }

}
