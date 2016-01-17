package kgp.tech.interiit.sos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import kgp.tech.interiit.sos.Utils.Utils;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText login = (EditText) findViewById(R.id.username);
        EditText pass = (EditText) findViewById(R.id.password);
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText address = (EditText) findViewById(R.id.address);
        final EditText phone = (EditText) findViewById(R.id.phone);

        pass.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Button b = (Button) findViewById(R.id.butt);
                    b.performClick();
                    return true;
                }
                return false;
            }
        });

        Button b=(Button)findViewById(R.id.butt);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String susername = login.getText().toString();
                EditText pass = (EditText) findViewById(R.id.password);
                String spassword = pass.getText().toString();
                String semail =email.getText().toString();
                String saddress = address.getText().toString();
                String sphone = phone.getText().toString();
                if (susername.length() == 0 || spassword.length() == 0 || semail.length()==0) {
                    Utils.showDialog(Signup.this, getString(R.string.err_fields_empty));
                    return;
                }

                ParseUser user = new ParseUser();
                user.setUsername(susername);
                user.setPassword(spassword);
                user.setEmail(semail);
                user.put("address", saddress);
                user.put("phone",sphone);

                final ProgressDialog dia = ProgressDialog.show(Signup.this, null, getString(R.string.alert_wait));

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        dia.dismiss();
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            startActivity(new Intent(Signup.this, MapsActivity.class));
                            finish();
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            Utils.showDialog(Signup.this, getString(R.string.err_login) + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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
