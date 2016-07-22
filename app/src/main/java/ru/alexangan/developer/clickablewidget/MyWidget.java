package ru.alexangan.developer.clickablewidget;

/**
 * Created by Administrator on 16.07.16.
 */
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class MyWidget extends AppWidgetProvider {

    final static String LOG_TAG = "myLogs";
    final String UPDATE_ALL_WIDGETS = "update_all_widgets";
    static String[] answers;
    static ArrayList<String> arrayAnswers;
    static String prevTypedPhrase = "null";
    static int i = 0;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.d(LOG_TAG, "On update");
        // обновляем все экземпляры
        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        // Удаляем Preferences
        Editor editor = context.getSharedPreferences(
                ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE).edit();
        for (int widgetID : appWidgetIds) {
            editor.remove(ConfigActivity.WIDGET_TIME_FORMAT + widgetID);
            //editor.remove(ConfigActivity.WIDGET_COUNT + widgetID);
        }
        editor.commit();
    }

    static void updateWidget(Context ctx, AppWidgetManager appWidgetManager,
                             int widgetID) {

        Log.d(LOG_TAG, "update widget");

        SharedPreferences sp = ctx.getSharedPreferences(
                ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);

        // Читаем формат времени и определяем текущее время
        String timeFormat = sp.getString(ConfigActivity.WIDGET_TIME_FORMAT + widgetID, null);

        //Log.d(LOG_TAG, "timeFormat= " + timeFormat);

        // Читаем счетчик
        //String count = String.valueOf(sp.getInt(ConfigActivity.WIDGET_COUNT + widgetID, 0));

        // Помещаем данные в текстовые поля
        RemoteViews widgetView = new RemoteViews(ctx.getPackageName(),
                R.layout.widget);

        if (answers == null) {

            answers = ctx.getResources().getStringArray(R.array.answers);

            arrayAnswers = new ArrayList();

            for (String item : answers) {
                arrayAnswers.add(item);
            }
        }


        if(timeFormat!= null && timeFormat != prevTypedPhrase && timeFormat.length() > 7)
        {
            //widgetView.setTextViewText(R.id.tvTime, timeFormat);
            prevTypedPhrase = timeFormat;
            arrayAnswers.add(i, timeFormat);

            //Log.d(LOG_TAG, "updatedAnswers= " + arrayAnswers.get(i));
        }

            widgetView.setTextViewText(R.id.tvTime, arrayAnswers.get(i++));

            if (i == arrayAnswers.size())
                i = 0;


        //widgetView.setTextViewText(R.id.tvCount, count);

        // Конфигурационный экран (первая зона)
        Intent configIntent = new Intent(ctx, ConfigActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        PendingIntent pIntent = PendingIntent.getActivity(ctx, widgetID,
                configIntent, 0);


        sp.edit().putString(ConfigActivity.WIDGET_TIME_FORMAT + widgetID, arrayAnswers.get(i-1)).commit();

        widgetView.setOnClickPendingIntent(R.id.tvTime, pIntent);

        /*
        // Обновление виджета (вторая зона)
        Intent updateIntent = new Intent(ctx, MyWidget.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                new int[] { widgetID });
        pIntent = PendingIntent.getBroadcast(ctx, widgetID, updateIntent, 0);
        widgetView.setOnClickPendingIntent(R.id.tvPressUpdate, pIntent);

        // Счетчик нажатий (третья зона)
        Intent countIntent = new Intent(ctx, MyWidget.class);
        countIntent.setAction(ACTION_CHANGE);
        countIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        pIntent = PendingIntent.getBroadcast(ctx, widgetID, countIntent, 0);
        widgetView.setOnClickPendingIntent(R.id.tvPressCount, pIntent);
*/
        // Обновляем виджет
        appWidgetManager.updateAppWidget(widgetID, widgetView);

        Log.d(LOG_TAG, "widget updated");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context, MyWidget.class);
        intent.setAction(UPDATE_ALL_WIDGETS);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),
                60000, pIntent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context, MyWidget.class);
        intent.setAction(UPDATE_ALL_WIDGETS);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equalsIgnoreCase(UPDATE_ALL_WIDGETS)) {

            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);

            for (int appWidgetID : ids) {
                updateWidget(context, appWidgetManager, appWidgetID);
            }
        }
    }

    /*
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        // Проверяем, что это intent от нажатия на третью зону
        if (intent.getAction().equalsIgnoreCase(ACTION_CHANGE)) {

            // извлекаем ID экземпляра
            int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mAppWidgetId = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);

            }
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                // Читаем значение счетчика, увеличиваем на 1 и записываем
                SharedPreferences sp = context.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
                int cnt = sp.getInt(ConfigActivity.WIDGET_COUNT + mAppWidgetId,  0);
                sp.edit().putInt(ConfigActivity.WIDGET_COUNT + mAppWidgetId,
                        ++cnt).commit();

                // Обновляем виджет
                updateWidget(context, AppWidgetManager.getInstance(context),
                        mAppWidgetId);
            }
        }
    }
    */

}
