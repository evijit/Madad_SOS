package kgp.tech.interiit.sos.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
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
    private static  LruCache<String, Bitmap> mMemoryCache;


    static void init() {
        if(mMemoryCache!=null)
            return;
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public static void GetProfilePic(final ParseUser user,final View v, final Context context)
    {
        init();
        Bitmap bmp = getBitmapFromMemCache(user.getUsername());
        if(bmp!=null)
        {
            Log.i("Helper", "Cache Pic " + user.getUsername());
            ImageView image = (ImageView) v;
            image.setImageBitmap(bmp);
        }
        else {
            //If not in cache, check local storage or download
            ParseQuery<ParseObject> pq = new ParseQuery("picture");
            pq.whereEqualTo("user", user);
            pq.fromLocalDatastore();
            pq.getFirstInBackground(new GetCallback<ParseObject>() {

                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e == null) {
                        // Photo available in local storage

                        // Locate the objectId from the class
                        Log.i("Helper", "LocalDB Pic " + parseObject.getBytes("picture").length);
                        Bitmap bmp = BitmapFactory
                                .decodeByteArray(
                                        parseObject.getBytes("picture"), 0,
                                        parseObject.getBytes("picture").length);
                        addBitmapToMemoryCache(user.getUsername(),bmp);
                        // Get the ImageView from
                        // main.xml
                        ImageView image = (ImageView) v;
                        // Set the Bitmap into the
                        // ImageView
                        image.setImageBitmap(bmp);
                    } else {
                        ParseFile fileObject = user.getParseFile("profilePic");
                        if (fileObject != null) {
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
                                        addBitmapToMemoryCache(user.getUsername(),bmp);
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
}
