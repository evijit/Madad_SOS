package kgp.tech.interiit.sos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import kgp.tech.interiit.sos.Utils.Utils;

public class Login extends AppCompatActivity {

    private String access;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String access = pref.getString("access", null);
        String token = pref.getString("token", null);

        EditText login = (EditText) findViewById(R.id.username);
        EditText pass = (EditText) findViewById(R.id.password);

        Button blogin=(Button)findViewById(R.id.butt);
        Button bsignup = (Button) findViewById(R.id.up);

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

        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText login = (EditText) findViewById(R.id.username);
                EditText pass = (EditText) findViewById(R.id.password);
                String username = login.getText().toString();
                String password = pass.getText().toString();
                if (username.length() == 0 || password.length() == 0) {
                    Utils.showDialog(Login.this, getString(R.string.err_fields_empty));
                    return;
                }

                final ProgressDialog dia = ProgressDialog.show(Login.this, null, getString(R.string.alert_wait));

                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        dia.dismiss();
                        if (user != null) {
                            startActivity(new Intent(Login.this, MapsActivity.class));
                            finish();
                        } else {
                            Utils.showDialog(Login.this, getString(R.string.err_login) + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        bsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Signup.class));
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_login, menu);
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
