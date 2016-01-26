package kgp.tech.interiit.sos;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class MessageActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    private View mImageView;
    private View mOverlayView;
    private View mListBackgroundView;
    private TextView mTitleView;
    private View mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private boolean mFabIsShown;

    ArrayList<Message> messages;
    AwesomeAdapter adapter;
    EditText text;
    static Random rand = new Random();
    static String sender;
    private Toolbar toolbar;
    static String message_incoming = "";
    private ObservableListView listView;
    static UUID uuid_this = UUID.randomUUID();
    final Pubnub pubnub = new Pubnub("pub-c-f9d02ea4-19f1-4737-b3e1-ef2ce904b94f", "sub-c-3d547124-be29-11e5-8a35-0619f8945a4f");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        if(toolbar!=null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        }
        //toolbar.setVisibility(View.INVISIBLE);
        pubnub.setUUID(uuid_this);
        try {
            pubnub.subscribe("Channel-ag04qto2e", new Callback() {

                        @Override
                        public void successCallback(String channel, Object message) {
                            JSONObject jj = (JSONObject)message;
                            try {
                                UUID uu = UUID.fromString(jj.get("uuid").toString());
                                if(uu.compareTo(uuid_this) != 0) {
                                    message_incoming = jj.get("text").toString();
                                    System.out.println("SUBSCRIBE : " + channel + " : "
                                            + message.getClass() + " : " + message.toString());
                                    new SendMessage().execute();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }

        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        String j =(String) b.get("name");
       // TextView title= (TextView)findViewById(R.id.name);
       // title.setText(j);
        sender=j;
        listView = (ObservableListView) findViewById(R.id.list);
        text = (EditText) this.findViewById(R.id.text);
        messages = new ArrayList<Message>();

        messages.add(new Message("Hello", false));
        messages.add(new Message("Hi!", true));
        messages.add(new Message("Wassup??", false));
        messages.add(new Message("nothing much, working on speech bubbles.", true));
        messages.add(new Message("you say!", true));
        messages.add(new Message("oh thats great. how are you showing them", false));


        adapter = new AwesomeAdapter(this, messages);
        listView.setAdapter(adapter);
        addNewMessage(new Message("mmm, well, using 9 patches png to show them.", true));


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
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(j);
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

        JSONObject jj = new JSONObject();
        try {
            jj.put("uuid", uuid_this);
            jj.put("text", newMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                System.out.println(response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error.toString());
            }
        };
        pubnub.publish("Channel-ag04qto2e", jj, callback);
        if(newMessage.length() > 0) {
            text.setText("");
            addNewMessage(new Message(newMessage, true));
            //new SendMessage().execute();
        }
    }
    private class SendMessage extends AsyncTask<Void, String, String>
    {
        String sender="Somebody";
        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(2000); //simulate a network call
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.publishProgress(String.format("%s started writing", sender));
            try {
                Thread.sleep(2000); //simulate a network call
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.publishProgress(String.format("%s has entered text", sender));
            try {
                Thread.sleep(3000);//simulate a network call
            }catch (InterruptedException e) {
                e.printStackTrace();
            }


            return Random_Message.messages[rand.nextInt(Random_Message.messages.length-1)];


        }
        @Override
        public void onProgressUpdate(String... v) {

            if(messages.get(messages.size()-1).isStatusMessage)//check wether we have already added a status message
            {
                messages.get(messages.size()-1).setMessage(v[0]); //update the status for that
                adapter.notifyDataSetChanged();
                listView.setSelection(messages.size()-1);
            }
            else{
                addNewMessage(new Message(true,v[0])); //add new message, if there is no existing status message
            }
        }
        @Override
        protected void onPostExecute(String text) {
            if(messages.get(messages.size()-1).isStatusMessage)//check if there is any status message, now remove it.
            {
                messages.remove(messages.size()-1);
            }


            addNewMessage(new Message(message_incoming, false)); // add the orignal message from server.
        }


    }
    void addNewMessage(Message m)
    {
        messages.add(m);
        adapter.notifyDataSetChanged();
        listView.setSelection(messages.size()-1);
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
}