package kgp.tech.interiit.sos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.text.ParseException;

import kgp.tech.interiit.sos.Utils.Utils;

public class ForgotPass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        EditText email = (EditText) findViewById(R.id.email);

        Button bConfirm = (Button)findViewById(R.id.confirm);

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.email);
                String emailid = email.getText().toString();
                if (emailid.length() == 0) {
                    Utils.showDialog(ForgotPass.this, getString(R.string.err_fields_empty));
                    return;
                }

                final ProgressDialog dialog = ProgressDialog.show(ForgotPass.this, null, getString(R.string.alert_wait));

                ParseUser.requestPasswordResetInBackground(emailid,
                        new RequestPasswordResetCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if (e == null) {
                                    startActivity(new Intent(ForgotPass.this, Login.class));
                                    finish();
                                } else {
                                    Utils.showDialog(ForgotPass.this, getString(R.string.err_login) + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        });


    }
}
