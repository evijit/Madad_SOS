package kgp.tech.interiit.sos.Utils;

import android.content.Context;
import android.content.ContextWrapper;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by akshaygupta on 27/01/16.
 */
public class Helper {
    private static  LruCache<String, Bitmap> mMemoryCache;

    private static String saveToInternalStorage(Bitmap bitmapImage, String userName, Context context){
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir

        File directory = context.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory,userName + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    private static void loadImageFromStorage(String path, String userName, ImageView v) throws FileNotFoundException {

        File f=new File(path, userName + ".jpg");
        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            ImageView img=(ImageView)findViewById(R.id.imgPicker);
        v.setImageBitmap(b);

    }

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

            try {
                Log.i("Helper", "Local storage " + user.getUsername());

//                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                // path to /data/data/yourapp/app_data/imageDir

                File directory = context.getDir("imageDir", Context.MODE_PRIVATE);
                loadImageFromStorage(directory.getPath(), user.getUsername(), (ImageView)v);
            }
            catch (Exception e){
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
                                addBitmapToMemoryCache(user.getUsername(), bmp);

                                image.setImageBitmap(bmp);
                                saveToInternalStorage(bmp, user.getUsername(), context);
                            }
                        }
                    });
                }
            }
        }
    }
}
