package kgp.tech.interiit.sos;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by sayan on 26/1/16.
 */
public class WidgetSOSClass extends AppWidgetProvider {

    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";

    public static int appid[];
    public static RemoteViews rview;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i("Widget", "Hello SOS Update");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.activity_sos_widget);
        Intent configIntent = new Intent(context, WidgetActivity.class);
        configIntent.putExtra("type","sos");
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //rview.setTextViewText(R.id.sos_text,"Bye!");
        //Toast.makeText(context,"hello sosos",Toast.LENGTH_SHORT).show();
        remoteViews.setOnClickPendingIntent(R.id.sos_button, configPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }
//    @Override
//    public void onReceive(Context paramContext, Intent paramIntent)
//    {
//        Log.i("Widget", "Hello Received");
//        String str = paramIntent.getAction();
//        if (paramIntent.getAction().equals(ACTION_WIDGET_RECEIVER)) {
//            updateWidgetState(paramContext, str);
//        }
//        else
//        {
//            if ("android.appwidget.action.APPWIDGET_DELETED".equals(str))
//            {
//                int i = paramIntent.getExtras().getInt("appWidgetId", 0);
//                if (i == 0)
//                {
//
//                }
//                else
//                {
//                    int[] arrayOfInt = new int[1];
//                    arrayOfInt[0] = i;
//                    onDeleted(paramContext, arrayOfInt);
//                }
//            }
//            super.onReceive(paramContext, paramIntent);
//        }
//    }
//    static void updateWidgetState(Context paramContext, String paramString)
//    {
//        RemoteViews localRemoteViews = buildUpdate(paramContext, paramString);
//        ComponentName localComponentName = new ComponentName(paramContext, WidgetSOSClass.class);
//        AppWidgetManager.getInstance(paramContext).updateAppWidget(localComponentName, localRemoteViews);
//    }
//    private static RemoteViews buildUpdate(Context paramContext, String paramString)
//    {
//        // Toast.makeText(paramContext, "buildUpdate() ::"+paramString, Toast.LENGTH_SHORT).show();
//        rview = new RemoteViews(paramContext.getPackageName(), R.layout.activity_sos_widget);
//        Intent active = new Intent(paramContext, WidgetSOSClass.class);
//        active.setAction(ACTION_WIDGET_RECEIVER);
//        active.putExtra("msg", "Message for Button 1");
//        PendingIntent configPendingIntent = PendingIntent.getActivity(paramContext, 0, active, 0);
//        rview.setOnClickPendingIntent(R.id.sos_button, configPendingIntent);
//        if(paramString.equals(ACTION_WIDGET_RECEIVER))
//        {
//            //your code for update and what you want on button click
//            Log.d("Widget", "Done");
//            rview.setTextViewText(R.id.sos_text,"Bye!");
//            Toast.makeText(paramContext,"hello sosos",Toast.LENGTH_SHORT).show();
//        }
//        return rview;
//    }
}