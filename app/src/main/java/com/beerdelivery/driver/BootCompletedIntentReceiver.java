package com.beerdelivery.driver;

/**
 * Created by BeerDelivery on 01.12.2019.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, ServiceSendCoords.class);
            context.startService(pushIntent);
        }
    }
}