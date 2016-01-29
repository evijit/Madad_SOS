package kgp.tech.interiit.sos;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

import kgp.tech.interiit.sos.Utils.Helper;
import kgp.tech.interiit.sos.Utils.comm;

public class MessageActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    private View mImageView;
    private View mOverlayView;
    private View mListBackgroundView;
    private RelativeLayout mTitleView;
    private View mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private boolean mFabIsShown;

    ArrayList<Message> messages;
    AwesomeAdapter adapter;
    EditText text;
    TextView desc;
    TextView time;
    static Random rand = new Random();
    static String sender;
    private Toolbar toolbar;
    static String message_incoming = "";
    private ObservableListView listView;

    String channelID;
    String sos_creater = "";
    private TextView mTitlehead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        if(toolbar!=null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        listView = (ObservableListView) findViewById(R.id.list);
        //text = (EditText) this.findViewById(R.id.text);
        messages = new ArrayList<Message>();

        adapter = new AwesomeAdapter(this, messages);
        listView.setAdapter(adapter);


        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();
        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        //ObservableListView listView = (ObservableListView) findViewById(R.id.list);
        listView.setScrollViewCallbacks(this);

        // Set padding view for ListView. This is the flexible space.
        View paddingView = new View(this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                mFlexibleSpaceImageHeight);
        paddingView.setLayoutParams(lp);

        // This is required to disable header's list selector effect
        paddingView.setClickable(true);

        listView.addHeaderView(paddingView);
        //setDummyData(listView);le
        mTitlehead = (TextView) findViewById(R.id.titlehead);
        mTitleView = (RelativeLayout) findViewById(R.id.title);


        setTitle(null);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MessageActivity.this, "FAB is clicked", Toast.LENGTH_SHORT).show();
            }
        });
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        ViewHelper.setScaleX(mFab, 0);
        ViewHelper.setScaleY(mFab, 0);

        // mListBackgroundView makes ListView's background except header view.
        mListBackgroundView = findViewById(R.id.list_background);

        text = (EditText)findViewById(R.id.text);
        desc = (TextView)findViewById(R.id.desc);
        time = (TextView)findViewById(R.id.time);

        //setting data
        Log.d("Message",getIntent().getStringExtra("username"));
        channelID = getIntent().getStringExtra("channelID");
        sos_creater = getIntent().getStringExtra("username");
        ParseUser user = new ParseUser();
        user.setUsername(sos_creater);
        Helper.GetProfilePic(user, mImageView, MessageActivity.this);
        desc.setText(getIntent().getStringExtra("Description"));
        time.setText(getString(R.string.started_at) +" "+ getIntent().getStringExtra("createdAt"));
        mTitlehead.setText(sos_creater);
        sender = sos_creater;

        history(channelID);
        recieveMessage(channelID);

        SharedPreferences sp = getSharedPreferences("SOS", Context.MODE_APPEND | Context.MODE_PRIVATE);

        if(sp.getString("sosID", null)!=null)
        {
            Log.d("Message","SOS active");
            setcolor(R.color.red);
        }

    }

//    @Override
//    protected int getLayoutResId() {
//        return 0;
//    }
//
//    @Override
//    protected ObservableListView createScrollable() {
//        return null;
//    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Translate list background
        ViewHelper.setTranslationY(mListBackgroundView, Math.max(0, -scrollY + mFlexibleSpaceImageHeight));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));
        //ViewHelper.setAlpha(toolbar, ScrollUtils.getFloat((float) scrollY / flexibleRange, 1, 0));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        setPivotXToTitle();
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
        } else {
            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
            ViewHelper.setTranslationY(mFab, fabTranslationY);
        }

        // Show FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
            //toolbar.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP) {
            // TODO show or hide the ActionBar
            toolbar.setVisibility(View.VISIBLE);
        } else if (scrollState == ScrollState.DOWN) {
            // TODO show or hide the ActionBar
            toolbar.setVisibility(View.INVISIBLE);

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle() {
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mTitleView, findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mTitleView, 0);
        }
    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
           // toolbar.setVisibility(View.INVISIBLE);
            mFabIsShown = true;
            mTitleView.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(new SpannableString(""));


        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            //toolbar.setVisibility(View.VISIBLE);
            mTitleView.setVisibility(View.INVISIBLE);
            getSupportActionBar().setTitle(new SpannableString(sender));
            mFabIsShown = false;
        }
    }

    public void sendMessage(View v)
    {
        String newMessage = text.getText().toString().trim();
        comm.sendMessage(channelID,newMessage);
        text.setText("");
    }

    void recieveMessage(String channelName)
    {
        try {
            final Pubnub pubnub = new Pubnub("pub-c-f9d02ea4-19f1-4737-b3e1-ef2ce904b94f", "sub-c-3d547124-be29-11e5-8a35-0619f8945a4f");

            pubnub.subscribe(channelName, new Callback() {
                public void successCallback(String channel, Object mes) {

                    try {
                        JSONObject json_mes = new JSONObject(mes.toString());

                        String message = json_mes.getString("message");
                        String username = json_mes.getString("username");

                        final Message m = new Message(username,message,false);
                        if(username.equals(ParseUser.getCurrentUser().getUsername()))
                            m.isMine = true;

                        MessageActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addNewMessage(m);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                public void errorCallback(String channel, PubnubError error) {
                    System.out.println(error.getErrorString());
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    public void history(String channelName){
        final Pubnub pubnub = new Pubnub("pub-c-f9d02ea4-19f1-4737-b3e1-ef2ce904b94f", "sub-c-3d547124-be29-11e5-8a35-0619f8945a4f");
        pubnub.history(channelName,100,false,new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                try {
                    JSONArray json = (JSONArray) message;
                    Log.d("History", json.toString());
                    final JSONArray messages = json.getJSONArray(0);

                    MessageActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < messages.length(); i++)
                            {
                                try {
                                    JSONObject jsonMsg = messages.getJSONObject(i);

                                    String message = jsonMsg.getString("message");
                                    String username = jsonMsg.getString("username");

                                    final Message m = new Message(username, message, false);
                                    if (username.equals(ParseUser.getCurrentUser().getUsername()))
                                        m.isMine = true;

                                    addNewMessage(m);
                                }catch (JSONException e) { // Handle errors silently
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.d("History", error.toString());
            }
        });
    }

    void addNewMessage(Message m)
    {
        messages.add(m);
        adapter.notifyDataSetChanged();
        listView.setSelection(messages.size() - 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    String FilePath = data.getData().getPath();
                    //textFile.setText(FilePath);
                    Toast.makeText(getApplicationContext(), FilePath,
                            Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    void setcolor(int colres)//use setcolor R.color.red for Self SOS
    {
        Log.d("Message", "Changing color");
        mOverlayView.setBackgroundColor(colres);
        toolbar.setBackgroundColor(colres);
        //mFab.setBackgroundTintList(ColorStateList.valueOf(colres));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    public void openMap(View v)
    {
        action_openMap();
    }

    void action_openMap()
    {

        SharedPreferences sp = getSharedPreferences("SOS", Context.MODE_APPEND | Context.MODE_PRIVATE);
        if(sp.getString("sosID", null)!=null)
        {
            Intent intent = new Intent(MessageActivity.this, FullMap.class);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(MessageActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                // User chose the "Settings" item, show the app settings UI...
                action_openMap();
                return true;

            case R.id.action_voice:

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}