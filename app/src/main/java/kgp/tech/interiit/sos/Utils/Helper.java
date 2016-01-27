package kgp.tech.interiit.sos.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by akshaygupta on 27/01/16.
 */
public class Helper {
    public static void GetProfilePic(final ParseUser user,final View v, final Context context)
    {
        ParseQuery<ParseObject> pq = new ParseQuery("picture");
        pq.whereEqualTo("user",user);
        pq.fromLocalDatastore();
        pq.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    // Locate the objectId from the class
                    Log.i("Helper", "Local Pic "+ parseObject.getBytes("picture").length);
                    Bitmap bmp = BitmapFactory
                            .decodeByteArray(
                                    parseObject.getBytes("picture"), 0,
                                    parseObject.getBytes("picture").length);

                    // Get the ImageView from
                    // main.xml
                    ImageView image = (ImageView) v;
                    // Set the Bitmap into the
                    // ImageView
                    image.setImageBitmap(bmp);
                }
                else
                {
                    ParseFile fileObject =  user.getParseFile("profilePic");
                    if(fileObject!=null)
                    {
                        fileObject.getDataInBackground(new GetDataCallback() {

                            public void done(final byte[] data,
                                             ParseException e) {
                                if (e == null) {
                                    Log.i("Helper", "Downloading Pic " + data.length + " " + user.getUsername() + " " + context.toString());
                                    ParseObject picData = new ParseObject("picture");
                                    picData.put("user", user);
                                    picData.put("picture", data);
                                    picData.pinInBackground();

                                    ImageView image = (ImageView) v;
                                    Bitmap bmp = BitmapFactory
                                            .decodeByteArray(
                                                    data, 0,
                                                    data.length);
                                    image.setImageBitmap(bmp);
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
