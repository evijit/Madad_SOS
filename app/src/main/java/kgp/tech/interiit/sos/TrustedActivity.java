package kgp.tech.interiit.sos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import kgp.tech.interiit.sos.Utils.Utils;
import kgp.tech.interiit.sos.Utils.comm;

public class TrustedActivity extends AppCompatActivity {

    public final int PICK_CONTACT = 2015;
    private List<ParseObject> ptlist;
    private List<ParseObject> prlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        final ListView tcList = (ListView) findViewById(R.id.trusted);
        final ListView rcList = (ListView) findViewById(R.id.requested);

        tcList.setEmptyView(findViewById(R.id.emptyviewtxt));

        ParseQuery<ParseObject> tquery = ParseQuery.getQuery("Trusted");
        tquery.whereEqualTo("UserId", ParseUser.getCurrentUser());
        //tquery.whereEqualTo("accepted", Boolean.TRUE);
        Log.d("Trusted","Fetching");
        tquery.fromLocalDatastore();
        tquery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ptlist = list;
                    ContactAdapter ca = new ContactAdapter(ptlist, TrustedActivity.this);
                    tcList.setAdapter(ca);
                }
                Log.d("Trusted","Fetched");

//                ParseQuery<ParseObject> rquery = ParseQuery.getQuery("Trusted");
//                rquery.whereEqualTo("UserId", ParseUser.getCurrentUser());
//                rquery.whereEqualTo("accepted", Boolean.FALSE);
//                rquery.fromLocalDatastore();
//                rquery.findInBackground(new FindCallback<ParseObject>() {
//                    @Override
//                    public void done(List<ParseObject> list, ParseException e) {
//                        if (e == null) {
//                            prlist = list;
//                            ContactAdapter ca = new ContactAdapter(prlist, TrustedActivity.this);
//                            rcList.setAdapter(ca);
//                        }
//                    }
//                });
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            final Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int ncol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
            int phcol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
            int ecol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            final String name = cursor.getString(ncol);
            final String phone = cursor.getString(phcol);
            final String email = cursor.getString(ecol);

            Utils.showDialog(this, getString(R.string.add)+" "+ name + " " + getString(R.string.add1), R.string.yes, R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_NEGATIVE:
                            // int which = -2

                            break;
                        case DialogInterface.BUTTON_POSITIVE:
                            // int which = -1
                            //search the user in data base and add to friend class
                            final ParseObject trustedUser = new ParseObject("Trusted");
                            trustedUser.put("Name", name);

                            trustedUser.put("Phone", PhoneNumberUtils.normalizeNumber(phone));

                            trustedUser.put("Email", email);
                            trustedUser.put("UserId", ParseUser.getCurrentUser());
                            trustedUser.put("accepted", Boolean.FALSE);
                            trustedUser.pinInBackground("trusted");

                            trustedUser.saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    comm.checkIfUser(trustedUser.getObjectId());
                                    Log.d("TRUSTED", trustedUser.getObjectId());
                                }
                            });

                            ptlist.add(trustedUser);
                            ContactAdapter ca = (ContactAdapter)((ListView) findViewById(R.id.trusted)).getAdapter();
                            ca.notifyDataSetChanged();



                            //TODO call the cloud service and make it check if contact uses the app
                            break;
                    }
                    return;
                }
            });

        }
    }

    public void addTrusted(View v)
    {
        Intent i = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        }
        startActivityForResult(i, PICK_CONTACT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;
//
//            case R.id.action_add:
//                // User chose the "Favorite" action, mark the current item
//                // as a favorite...
//                Log.d("trusted","add");
//                Intent i = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                    i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
//                }
//                startActivityForResult(i, PICK_CONTACT);
//                return true;
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//    }
}

class ContactAdapter extends BaseAdapter {
    private List<ParseObject> contactList;
    private Context context;

    ContactAdapter(List<ParseObject> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.trusted_contact_item, parent, false);

        TextView name = (TextView) v.findViewById(R.id.name);
        name.setText(contactList.get(position).getString("Name"));

        TextView phone = (TextView) v.findViewById(R.id.phone);
        phone.setText(contactList.get(position).getString("Phone"));

        TextView email = (TextView) v.findViewById(R.id.email);
        email.setText(contactList.get(position).getString("Email"));

        LinearLayout notuserlayout = (LinearLayout) v.findViewById(R.id.notuserlayout);
        if(false) //TODO
            notuserlayout.setVisibility(View.GONE);
        else
            notuserlayout.setVisibility(View.VISIBLE);

        TextView notuser = (TextView) v.findViewById(R.id.notuser);
        notuser.setText(contactList.get(position).getString("Name") + " " + context.getString(R.string.notuser));

        ImageButton remindBtn = (ImageButton) v.findViewById(R.id.rembtn);
        remindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                String number = contactList.get(position).getString("Phone");
                String message = context.getString(R.string.download_app);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number,null,message,null,null);
                //Intent intent = new Intent(Intent.ACTION_, Uri.parse(url));

                //context.startActivity(intent);
            }
        });


        ImageButton callBtn = (ImageButton) v.findViewById(R.id.callbtn);
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "tel:" + contactList.get(position).getString("Phone");
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(intent);
            }
        });

        ImageButton delBtn = (ImageButton) v.findViewById(R.id.delbtn);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showDialog(context, contactList.get(position).getString("Name") + " " + context.getString(R.string.deleteContact), R.string.yes, R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_NEGATIVE:
                                // int which = -2

                                break;
                            case DialogInterface.BUTTON_POSITIVE:
                                // int which = -1
                                //search the user in data base and add to friend class
                                contactList.get(position).deleteEventually();
                                contactList.remove(position);
                                notifyDataSetChanged();
                                //TODO call the cloud service and make it check if contact uses the app
                                break;
                        }
                        return;
                    }
                });
            }
        });

        return v;
    }
}