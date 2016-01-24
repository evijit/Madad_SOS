package kgp.tech.interiit.sos;

import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import bolts.Task;
import kgp.tech.interiit.sos.Utils.Utils;

public class TrustedActivity extends AppCompatActivity {

    public final int PICK_CONTACT = 2015;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted);

        Button addBtn = (Button) findViewById(R.id.addBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                }
                startActivityForResult(i, PICK_CONTACT);
            }

        });

        final ListView tcList = (ListView) findViewById(R.id.trusted);
        final ListView rcList = (ListView) findViewById(R.id.requested);

        ParseQuery<ParseObject> tquery = ParseQuery.getQuery("Trusted");
        tquery.whereEqualTo("UserId", ParseUser.getCurrentUser());
        tquery.whereEqualTo("accepted", Boolean.TRUE);
        tquery.fromLocalDatastore();
        tquery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ContactAdapter ca = new ContactAdapter(list, TrustedActivity.this);
                    tcList.setAdapter(ca);
                }

                ParseQuery<ParseObject> rquery = ParseQuery.getQuery("Trusted");
                rquery.whereEqualTo("UserId", ParseUser.getCurrentUser());
                rquery.whereEqualTo("accepted", Boolean.FALSE);
                rquery.fromLocalDatastore();
                rquery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            ContactAdapter ca = new ContactAdapter(list, TrustedActivity.this);
                            rcList.setAdapter(ca);
                        }
                    }
                });
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int ncol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
            int phcol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
            int ecol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            final String name = cursor.getString(ncol);
            final String phone = cursor.getString(phcol);
            final String email = cursor.getString(ecol);

            Utils.showDialog(this, R.string.doadd,R.string.yes,R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_NEGATIVE:
                            // int which = -2

                            break;
                        case DialogInterface.BUTTON_POSITIVE:
                            // int which = -1
                            //search the user in data base and add to friend class
                            ParseObject trustedUser = new ParseObject("Trusted");
                            trustedUser.put("Name",name);
                            trustedUser.put("Phone",phone);
                            trustedUser.put("Email",email);
                            trustedUser.put("UserId", ParseUser.getCurrentUser());
                            trustedUser.put("accepted",Boolean.FALSE);
                            trustedUser.pinInBackground("trusted");
                            trustedUser.saveEventually();
                            break;
                    }
                    return;
                }
            });

        }
    }
}

class ContactAdapter extends BaseAdapter{
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.contact_card, parent, false);

        TextView name = (TextView) v.findViewById(R.id.name);
        name.setText(contactList.get(position).getString("Name"));

        TextView phone = (TextView) v.findViewById(R.id.phone);
        phone.setText(contactList.get(position).getString("Phone"));

        TextView email = (TextView) v.findViewById(R.id.email);
        email.setText(contactList.get(position).getString("Email"));

        return v;
    }
}