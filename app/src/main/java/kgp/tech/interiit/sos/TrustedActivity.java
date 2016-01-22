package kgp.tech.interiit.sos;

import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.ParseObject;
import com.parse.ParseUser;

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
                    i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Contactables.CONTENT_URI);
                }
                startActivityForResult(i, PICK_CONTACT);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int ncol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DISPLAY_NAME);
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
                            trustedUser.put("email",email);
                            trustedUser.put("UserId", ParseUser.getCurrentUser());
                            trustedUser.put("accepted",Boolean.FALSE);

                            trustedUser.saveInBackground();
                            break;
                    }
                    return;
                }
            });

        }
    }
}
