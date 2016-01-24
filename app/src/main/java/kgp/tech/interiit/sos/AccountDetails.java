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
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kgp.tech.interiit.sos.Utils.Utils;

public class AccountDetails extends AppCompatActivity {

    int YOUR_SELECT_PICTURE_REQUEST_CODE=1;
    int GET_FROM_GALLERY=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        ParseQuery<ParseObject> pq = new ParseQuery("picture");
        pq.fromLocalDatastore();

        pq.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                // Locate the objectId from the class
                if (e == null && !list.isEmpty()) {
                    Bitmap bmp = BitmapFactory
                            .decodeByteArray(
                                    list.get(0).getBytes("picture"), 0,
                                    list.get(0).getBytes("picture").length);

                    // Get the ImageView from
                    // main.xml
                    ImageView image = (ImageView) findViewById(R.id.photo);

                    // Set the Bitmap into the
                    // ImageView
                    image.setImageBitmap(bmp);
                }
            }
        });


    }

    public void photoupload(View v)
    {
//        openImageIntent();
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
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

                ParseQuery<ParseObject> query = ParseQuery.getQuery("picture");
                query.fromLocalDatastore();
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if(parseObject!=null)
                            parseObject.put("picture", image);
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
