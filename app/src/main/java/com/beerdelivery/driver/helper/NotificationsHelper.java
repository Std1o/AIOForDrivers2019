package com.beerdelivery.driver.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import androidx.core.app.NotificationCompat;

import com.beerdelivery.driver.R;

/**
 * Created by BeerDelivery on 01.12.2019.
 */

public class NotificationsHelper {

    private static Context appContext; // контекст приложения
    private static int lastNotificationId = 10010; //уин последнего уведомления

    private static NotificationManager manager; // менеджер уведомлений

    // метод инциализации данного хелпера
    public  NotificationsHelper(Context context){

            appContext = context.getApplicationContext(); // на случай инициализации Base Context-ом
            manager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    /**
     * Создает и возвращает общий NotificationCompat.Builder
     * @return
     */


    private static NotificationCompat.Builder getNotificationBuilder(){
        final NotificationCompat.Builder nb = new NotificationCompat.Builder(appContext)
                .setAutoCancel(false) // чтобы уведомление закрылось после тапа по нему
                .setOnlyAlertOnce(true) // уведомить однократно
                .setWhen(System.currentTimeMillis()) // время создания уведомления, будет отображено в стандартном уведомлении справа
                .setContentTitle(appContext.getString(R.string.app_name)); //заголовок
              //  .setDefaults(Notification.DEFAULT_ALL); // alarm при выводе уведомления: звук, вибратор и диод-индикатор - по умолчанию

        return nb;
    }

    // удаляет все уведомления, созданные приложением
    public static void cancelAllNotifications(){
        manager.cancelAll();
    }

    /**
     *
     * @param message  - текст уведомления
     * @param targetActivityClass - класс целевой активити
     * @param iconResId - R.drawable необходимой иконки
     * @return
     */
    public static int createNotification(final String message, final Class targetActivityClass, final int iconResId) {


        // некоторые проверки на null не помешают, зачем нам NPE?
        if (targetActivityClass==null){
            new Exception("createNotification() targetActivity is null!").printStackTrace();
            return -1;
        }
        if (manager==null){
            new Exception("createNotification() NotificationUtils not initialized!").printStackTrace();
            return -1;
        }

        final Intent notificationIntent = new Intent(appContext, targetActivityClass); // интент для запуска указанного Activity по тапу на уведомлении

        final NotificationCompat.Builder nb = getNotificationBuilder() // получаем из хелпера generic Builder, и далее донастраиваем его
                .setContentText(message) // сообщение, которое будет отображаться в самом уведомлении
                .setTicker(message) //сообщение, которое будет показано в статус-баре при создании уведомления, ставлю тот же
                .setSmallIcon(iconResId != 0 ? iconResId : R.mipmap.ic_launcher) // иконка, если 0, то используется иконка самого аппа
                .setContentIntent(PendingIntent.getActivity(appContext, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)); // создание PendingIntent-а


        final Notification notification = nb.build(); //генерируем уведомление, getNotification() - deprecated!

        manager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(lastNotificationId, notification); // "запускаем" уведомление

        return lastNotificationId;




    }
}