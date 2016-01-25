package kgp.tech.interiit.sos.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by nishantiam on 24-01-2016.
 */
public class SOSService extends Service {
    public SOSService() {
        super();
    }

    public void channelListener(){
        Log.e("channelListener", "Listen start");
        ParseQuery<ParseObject> pqpq = new ParseQuery<ParseObject>("SOS_Users");
        pqpq.whereEqualTo("UserID", ParseUser.getCurrentUser());
        pqpq.whereEqualTo("hasAccepted", true);

        pqpq.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(!list.isEmpty() && e == null){
                    for(int i = 0; i < list.size(); i++){
                        Log.e("List size", String.valueOf(list.size()));
                        final ParseObject list_parsed = list.get(i);

                        final ParseObject pob = list.get(i).getParseObject("SOSid");

                        Log.e("SOS Object ID", pob.getObjectId().toString());

                        ParseQuery<ParseObject> sos_query = ParseQuery.getQuery("SOS");
                        //sos_query.whereEqualTo("isActive",true);
                        sos_query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> list, ParseException e) {
                                Log.e("Retrieved len",String.valueOf(list.size()));
                                for(int i=0;i<list.size();i++) {
                                    // Do something here to move the mountains
                                    String str1 = pob.getObjectId();
                                    String str2 = list.get(i).getObjectId();
                                    Log.e("The strings: ", str1 + " " + str2);
                                    if (pob.getObjectId().compareTo(list.get(i).getObjectId()) == 0)
                                        Log.e("ch id", list.get(i).getString("channelID"));
                                }
                            }


                        });

                    }
                }
            }
        });
    }


    public void listenChannel (String channelID) {
        Log.e("listenChannel",channelID);
        final Pubnub pubnub = new Pubnub("pub-c-f9d02ea4-19f1-4737-b3e1-ef2ce904b94f", "sub-c-3d547124-be29-11e5-8a35-0619f8945a4f");
        try {
            pubnub.subscribe(channelID, new Callback() {
                public void successCallback(String channel, Object message) {
                    System.out.println(message);
                    Log.e("SOS", message.toString());
                }

                public void errorCallback(String channel, PubnubError error) {
                    System.out.println(error.getErrorString());
                }
            });

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("SOSService", "onDestroy");
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.e("SOSS", "Created");

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e("SOSService", "onStartCommand");
        // Listens to all the channels it needs to.
        channelListener();
            /* Publish a simple message to the demo_tutorial channel */
       /* final JSONObject data = new JSONObject();
        pubnub.setUUID(ParseUser.getCurrentUser().toString());

        try {
            //generate channel name
            final ParseObject obj = new ParseObject("SOS");
            final String channelName = UUID.randomUUID().toString();

            obj.put("UserID", ParseUser.getCurrentUser());
            obj.put("channelID", channelName);
            obj.put("isActive", true);
            obj.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        String id = obj.getObjectId();
                        try {
                            data.put("userid", ParseUser.getCurrentUser());
                            data.put("channel", channelName);

                            HashMap<String, Object> params = new HashMap<>();
                            params.put("username", ParseUser.getCurrentUser().getUsername());
                            params.put("channel", channelName);
                            params.put("sosid", id);

                            ParseCloud.callFunctionInBackground("sendSOS", params, new FunctionCallback<Float>() {
                                @Override
                                public void done(Float fLoat, ParseException e) {
                                    if (e == null) {
                                        System.out.println("YAAAY!!!");

                                    }

                                }
                            });
                            Log.e("SOS",channelName);
                            //TODO: channelName
                            pubnub.publish("ag04qto2e", "BACHAO!!!", new Callback() {
                            });



                            pubnub.subscribe("ag04qto2e", new Callback() {
                                public void successCallback(String channel, Object message) {
                                    System.out.println(message);

                                    Log.e("SOS", message.toString());
                                }

                                public void errorCallback(String channel, PubnubError error) {
                                    System.out.println(error.getErrorString());
                                }
                            });

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    } else
                        System.out.println(e.toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return START_STICKY;
    }


}
