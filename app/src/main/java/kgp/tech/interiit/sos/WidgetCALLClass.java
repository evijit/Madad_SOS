package kgp.tech.interiit.sos;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by sayan on 27/1/16.
 */
public class WidgetCALLClass extends AppWidgetProvider {

    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";

    public static int appid[];
    public static RemoteViews rview;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i("Widget", "Hello CALL Update");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.activity_call_widget);
        Intent configIntent = new Intent(context, WidgetActivity.class);
        configIntent.putExtra("type","call");
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 1, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //rview.setTextViewText(R.id.sos_text,"Bye!");
        //Toast.makeText(context, "hello sosos", Toast.LENGTH_SHORT).show();
        remoteViews.setOnClickPendingIntent(R.id.call_button, configPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
}
