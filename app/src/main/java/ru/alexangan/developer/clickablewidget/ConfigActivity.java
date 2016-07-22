package ru.alexangan.developer.clickablewidget;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ConfigActivity extends Activity {

    final static String LOG_TAG = "myLogs";

    public final static String WIDGET_PREF = "widget_pref";
    public static String WIDGET_TIME_FORMAT = "Type your phrase_";
    public final static String WIDGET_COUNT = "widget_count_";

    int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    Intent resultValue;
    SharedPreferences sp;
    EditText etFormat;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // извлекаем ID конфигурируемого виджета
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // и проверяем его корректность
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // формируем intent ответа
        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

        // отрицательный ответ
        setResult(RESULT_CANCELED, resultValue);

        setContentView(R.layout.config);

        sp = getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
        etFormat = (EditText) findViewById(R.id.etFormat);

        //Log.d(LOG_TAG, "curReceivedAnswer= " + ConfigActivity.WIDGET_TIME_FORMAT + widgetID);

        etFormat.setText(sp.getString(ConfigActivity.WIDGET_TIME_FORMAT + widgetID, "Start"));


        //int cnt = sp.getInt(ConfigActivity.WIDGET_COUNT + widgetID, -1);
        //if (cnt == -1) sp.edit().putInt(WIDGET_COUNT + widgetID, 0);
    }

    public void onClick(View v){

        sp.edit().putString(WIDGET_TIME_FORMAT + widgetID, etFormat.getText().toString()).commit();

        MyWidget.updateWidget(this, AppWidgetManager.getInstance(this), widgetID);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
