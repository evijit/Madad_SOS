package kgp.tech.interiit.sos.Utils;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.net.URL;
import java.util.List;

/**
 * Created by akshaygupta on 22/01/16.
 */
public class UserData{
    private ParseQuery<ParseObject> trustedQuery;
    public List<ParseObject> trustedList;
    private ParseQuery<ParseObject> requestQuery;
    public List<ParseObject> requestList;
    private ParseQuery<ParseObject> allSosQuery;
    public List<ParseObject> allSosList;
    private ParseQuery<ParseObject> mySosQuery;
    public List<ParseObject> mySos;

    public UserData(){
        trustedQuery = ParseQuery.getQuery("Trusted");
        trustedQuery.whereEqualTo("", "");

        requestQuery = ParseQuery.getQuery("Trusted");
        requestQuery.whereEqualTo("accepted", "");

        allSosQuery = ParseQuery.getQuery("SOS_users");

        mySosQuery = ParseQuery.getQuery("SOS");
        mySosQuery.whereEqualTo("UserId", ParseUser.getCurrentUser());
    }
    public void update() {
        try {
            trustedList = trustedQuery.find();
            Log.d("Thread", String.valueOf(trustedList));
            requestList = requestQuery.find();

            allSosList = allSosQuery.find();

            requestList = requestQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //th.start();
    }
    Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                trustedList = trustedQuery.find();
                Log.d("Thread", String.valueOf(trustedList));
                requestList = requestQuery.find();

                allSosList = allSosQuery.find();

                requestList = requestQuery.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    });
}


