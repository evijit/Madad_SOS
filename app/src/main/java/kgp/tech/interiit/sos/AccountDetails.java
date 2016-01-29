package kgp.tech.interiit.sos;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kgp.tech.interiit.sos.Utils.Helper;
import kgp.tech.interiit.sos.Utils.Utils;

public class AccountDetails extends AppCompatActivity {

    int YOUR_SELECT_PICTURE_REQUEST_CODE=1;
    int GET_FROM_GALLERY=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        TextView username= (TextView) findViewById(R.id.username);
        username.setText(ParseUser.getCurrentUser().getString("displayname"));

        TextView phone= (TextView) findViewById(R.id.phone);
        phone.setText(ParseUser.getCurrentUser().getString("phone"));

        TextView email= (TextView) findViewById(R.id.email);
        email.setText(ParseUser.getCurrentUser().getString("email"));

        ImageView image = (ImageView) findViewById(R.id.photo);
        Helper.GetProfilePic(ParseUser.getCurrentUser(), image, this);
    }

    public void photoupload(View v)
    {
//        openImageIntent();
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                double newWidth = 400;
                double newHeight = 400;
                int width = bitmap.getWidth();
                Log.i("Old width......", width + "");
                int height = bitmap.getHeight();
                Log.i("Old height.....", height + "");

                Matrix matrix = new Matrix();
                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;
                matrix.postScale(scaleWidth, scaleHeight);

                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                int newwidth = resizedBitmap.getWidth();
                Log.i("New width......", newwidth + "");
                int newheight = resizedBitmap.getHeight();
                Log.i("New height.....", newheight + "");

                ImageView imgView=(ImageView)findViewById(R.id.photo);

                imgView.setImageBitmap(resizedBitmap);
                final byte[] image = outputStream.toByteArray();

                // Create the ParseFile
                ParseFile file = new ParseFile("profile.jpg", image);
                // Upload the image into Parse Cloud
                file.saveInBackground();

                // Create a New Class called "ImageUpload" in Parse
                ParseUser imgupload = ParseUser.getCurrentUser();

                // Create a column named "ImageFile" and insert the image
                imgupload.put("profilePic", file);
                // Create the class and the columns
                imgupload.saveInBackground();

                ParseQuery<ParseObject> pq = ParseQuery.getQuery("picture");
                pq.whereEqualTo("user", ParseUser.getCurrentUser());
                pq.fromLocalDatastore();
                pq.getFirstInBackground(new GetCallback<ParseObject>() {

                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e != null) {
                            // Saving image locally
                            e.printStackTrace();
                            ParseObject picData = new ParseObject("picture");
                            picData.put("user", ParseUser.getCurrentUser());
                            picData.put("picture", image);
                            picData.pinInBackground();
                            return;
                        }
                        parseObject.put("picture",image);
                        parseObject.saveInBackground();
                    }
                });

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private Uri outputFileUri;



}
