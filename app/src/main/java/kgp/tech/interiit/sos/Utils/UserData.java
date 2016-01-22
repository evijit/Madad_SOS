package kgp.tech.interiit.sos.Utils;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    public void update()
    {

        trustedQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e==null)
                {
                    trustedList = list;
                }
                else
                {
                    Log.d("Trust","Error: " + e.getMessage());
                }
            }
        });

        requestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e==null)
                {
                    requestList = list;
                }
                else
                {
                    Log.d("Request","Error: " + e.getMessage());
                }
            }
        });

        allSosQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e==null)
                {
                    allSosList = list;
                }
                else
                {
                    Log.d("AllSos","Error: " + e.getMessage());
                }
            }
        });

        requestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e==null)
                {
                    mySos = list;
                }
                else
                {
                    Log.d("MySos","Error: " + e.getMessage());
                }
            }
        });
    }
}
