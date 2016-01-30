package kgp.tech.interiit.sos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kgp.tech.interiit.sos.Utils.DateFormater;
import kgp.tech.interiit.sos.Utils.Helper;
import kgp.tech.interiit.sos.Utils.Utils;

public class AcceptSOS extends AppCompatActivity {

    private String SOSid;
    private String channelId;
    private String senderId;
    private String displayname;
    private String createdtime;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_sos);
        Intent intent = getIntent();
        try {
            JSONObject data = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.d("AcceptSOS",data.toString());
            SOSid = data.getString("sosId");
            Log.d("AcceptSOS","sosid "+SOSid);
            senderId = data.getString("username");
            channelId = data.getString("chatChannel");
            displayname = data.getString("displayname");
            location = data.getString("location");
            createdtime = DateFormater.formatString(data.getString("ctime"));

            TextView username = (TextView)findViewById(R.id.username);
            username.setText(displayname);

            TextView det = (TextView)findViewById(R.id.detail);
            det.setText(getString(R.string.default_sos));

            TextView loc = (TextView)findViewById(R.id.addr);
            loc.setText(location);

            TextView tim = (TextView)findViewById(R.id.tim);
            tim.setText(createdtime);

            Log.d("AcceptedSOS", displayname);
            Log.d("AcceptedSOS",location);


            final ImageView img = (ImageView) findViewById(R.id.img);
            ParseQuery<ParseUser> pq = ParseUser.getQuery();
            pq.whereEqualTo("username", senderId);
            pq.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> list, ParseException e) {
                    if(e!=null)
                    {
                        e.printStackTrace();
                        return;
                    }
                    Helper.GetProfilePic(list.get(0), img, AcceptSOS.this);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void action_accept_sos(View v)
    {
        ParseQuery<ParseObject> pq = ParseQuery.getQuery("SOS_Users");
        pq.include("SOSid");
        pq.include("SOSid.UserID");
        pq.whereEqualTo("UserID", ParseUser.getCurrentUser());
        ParseObject sos = ParseObject.createWithoutData("SOS", SOSid);
        Log.d("AcceptedSOS","accepting");
        pq.whereEqualTo("SOSid",sos);
        final ProgressDialog dia = ProgressDialog.show(AcceptSOS.this, null, getString(R.string.alert_wait));
        pq.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e!=null)
                {
                    dia.dismiss();
                    Log.d("Sos","Lost");
                    e.printStackTrace();
                    return;
                }

                final ParseObject sos = parseObject.getParseObject("SOSid");
                final ParseUser user = sos.getParseUser("UserID");

                parseObject.put("hasAccepted", true);
                parseObject.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null)
                        {
                            dia.dismiss();
                            Utils.showDialog(AcceptSOS.this,e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                        dia.dismiss();

                        notifySOS();
                        Log.d("AcceptedSOS", "Saved");
                        Intent intent = new Intent(AcceptSOS.this, MessageActivity.class);
                        intent.putExtra("createdAt", DateFormater.formatTimeDate(sos.getCreatedAt()));
                        intent.putExtra("channelID", sos.getString("channelID"));
                        intent.putExtra("username", user.getUsername());
                        intent.putExtra("Description", sos.getString("Description"));
                        intent.putExtra("displayname", user.getString("displayname"));
                        Log.d("",sos.getString("Description"));
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    public void notifySOS() {
        Log.d("AcceptedSOS","notifying");
        // Find users near a given location
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", senderId);

        // Find devices associated with these users
        ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
        pushQuery.whereMatchesQuery("user", userQuery);

        JSONObject jo = new JSONObject();
        try {
            jo.put("title", "Someone is coming to help you!");
            jo.put("alert", "Ya! I'm coming!");
            jo.put("sosId", SOSid);
            jo.put("chatChannel", channelId);
            jo.put("username", ParseUser.getCurrentUser().getUsername());
            jo.put("type", "helping");
            jo.put("displayname", displayname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setData(jo);
        push.sendInBackground();
    }

    public void action_reject_sos(final View v)
    {
//        Utils.showDialog(this, getString(R.string.please_help), R.string.save, R.string.no, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.d("AcceptSOS",which+" whc");
//                switch (which) {
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        Log.d("AcceptSOS", "neg");
//                        Toast.makeText(getApplicationContext(),"Bad person", Toast.LENGTH_SHORT).show();
//                        finish();
//                        break;
//                    case DialogInterface.BUTTON_POSITIVE:
//                        Log.d("AcceptSOS","pos");
//                        action_accept_sos(v);
//                        //TODO call the cloud service and make it check if contact uses the app
//                        break;
//                }
//                return;
//            }
//        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.please_help)
                .setCancelable(false)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        action_accept_sos(v);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        AcceptSOS.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accept_so, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
