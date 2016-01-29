package kgp.tech.interiit.sos.Utils;

import android.content.Context;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import kgp.tech.interiit.sos.Message;
import kgp.tech.interiit.sos.MessageActivity;

/**
 * Created by akshaygupta on 26/01/16.
 */
public class comm {

    public static boolean internetConnected()
    {
        return true;
    }

    private static comm _instance = null;
    public static comm getInstance(){
        if(_instance == null)
            _instance= new comm();
        return _instance;
    }
    private comm(){} //Making the constructor private, so no 2 object can be created

    public static ParseObject sendSOS(String sos_message) {
            /* Publish a simple message to the demo_tutorial channel */
        final Pubnub pubnub = new Pubnub("pub-c-f9d02ea4-19f1-4737-b3e1-ef2ce904b94f", "sub-c-3d547124-be29-11e5-8a35-0619f8945a4f");
        pubnub.setUUID(ParseUser.getCurrentUser().toString());

        try {
            //generate channel name
            final ParseObject sos = new ParseObject("SOS");
            sos.put("UserID", ParseUser.getCurrentUser());
            final String channelName = UUID.randomUUID().toString();
            sos.put("channelID", channelName);
            sos.put("Description",sos_message);
            sos.put("isActive", true);
            sos.pinInBackground();
            // Log.d("comm", sos.getObjectId());
//            sos.saveInBackground(new SaveCallback() {
//                public void done(ParseException e) {
//                    if (e == null) {
//                        try {
//                            Log.d("comm-func", sos.getObjectId());
//                            HashMap<String, Object> params = new HashMap<>();
//                            params.put("username", ParseUser.getCurrentUser().getUsername());
//                            params.put("channel", channelName);
//                            params.put("sosid", sos.getObjectId());
//
//                            ParseCloud.callFunctionInBackground("sendSOS", params, new FunctionCallback<Float>() {
//                                @Override
//                                public void done(Float fLoat, ParseException e) {
//                                    if (e == null) {
//                                        System.out.println("YAAAY!!!");
//                                        sendMessage(channelName, "Help me");
//                                    }
//                                }
//                            });
//                        } catch (Exception e1) {
//                            e1.printStackTrace();
//                        }
//
//                    } else
//                        System.out.println(e.toString());
//                }
//            });
            sos.save();
            Log.d("comm", sos.getObjectId());
            try {
                Log.d("comm-func", sos.getObjectId());
                HashMap<String, Object> params = new HashMap<>();
                params.put("username", ParseUser.getCurrentUser().getUsername());
                params.put("channel", channelName);
                params.put("sosid", sos.getObjectId());
                params.put("type", "sos");
                params.put("displayname", ParseUser.getCurrentUser().get("displayname"));
                params.put("ctime", (new Date()).toString());

                ParseCloud.callFunctionInBackground("sendSOS", params, new FunctionCallback<Float>() {
                    @Override
                    public void done(Float fLoat, ParseException e) {
                        if (e == null) {
                            System.out.println("YAAAY!!!");
                            sendMessage(channelName, "Help me");
                        }
                        else{
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return sos;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void checkIfUser(String trustedid)
    {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("trustedid", trustedid);

            ParseCloud.callFunctionInBackground("checkifuser", params, new FunctionCallback<Float>() {
                @Override
                public void done(Float fLoat, ParseException e) {
                    if (e == null) {
                    }
                    else{
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String channelName,String message)
    {
        final Pubnub pubnub = new Pubnub("pub-c-f9d02ea4-19f1-4737-b3e1-ef2ce904b94f", "sub-c-3d547124-be29-11e5-8a35-0619f8945a4f");
        final JSONObject data = new JSONObject();
        try {
            data.put("username",ParseUser.getCurrentUser().getUsername());
            data.put("message",message);
            data.put("displayname",ParseUser.getCurrentUser().getString("displayname"));
            pubnub.publish(channelName, data, new Callback() {
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
