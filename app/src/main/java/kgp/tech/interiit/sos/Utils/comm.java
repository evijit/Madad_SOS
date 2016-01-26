package kgp.tech.interiit.sos.Utils;

import android.content.Context;

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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import kgp.tech.interiit.sos.Message;
import kgp.tech.interiit.sos.MessageActivity;

/**
 * Created by akshaygupta on 26/01/16.
 */
public class comm {

    public static String sendSOS() {
            /* Publish a simple message to the demo_tutorial channel */
        final Pubnub pubnub = new Pubnub("pub-c-f9d02ea4-19f1-4737-b3e1-ef2ce904b94f", "sub-c-3d547124-be29-11e5-8a35-0619f8945a4f");
        pubnub.setUUID(ParseUser.getCurrentUser().toString());

        try {
            //generate channel name
            final ParseObject sos = new ParseObject("SOS");
            sos.put("UserID", ParseUser.getCurrentUser());
            final String channelName = UUID.randomUUID().toString();
            sos.put("channelID", channelName);
            sos.put("isActive", true);

            sos.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        try {
                            HashMap<String, Object> params = new HashMap<>();
                            params.put("username", ParseUser.getCurrentUser().getUsername());
                            params.put("channel", channelName);
                            params.put("sosid", sos.getObjectId());

                            ParseCloud.callFunctionInBackground("sendSOS", params, new FunctionCallback<Float>() {
                                @Override
                                public void done(Float fLoat, ParseException e) {
                                    if (e == null) {
                                        System.out.println("YAAAY!!!");
                                        sendMessage(channelName, "Help me");
                                    }
                                }
                            });
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }

                    } else
                        System.out.println(e.toString());
                }
            });
            return channelName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendMessage(String channelName,String message)
    {
        final Pubnub pubnub = new Pubnub("pub-c-f9d02ea4-19f1-4737-b3e1-ef2ce904b94f", "sub-c-3d547124-be29-11e5-8a35-0619f8945a4f");
        final JSONObject data = new JSONObject();
        try {
            data.put("username",ParseUser.getCurrentUser().getUsername());
            data.put("message",message);
            pubnub.publish(channelName, data, new Callback() {
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
