package kgp.tech.interiit.sos;

/**
 * Created by akshaygupta on 26/01/16.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.widget.ImageButton;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import kgp.tech.interiit.sos.Utils.Utils;


public class AddSOSDetailActivity extends Activity
{
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private ImageButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private ImageButton mPlayButton = null;
    private MediaPlayer   mPlayer = null;

    boolean mStartRecording = true;
    boolean mStartPlaying = true;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public AddSOSDetailActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

//    public ParseObject uploadToParse(File audioFile, ParseObject po, String columnName){
        private final ParseObject uploadAudioToParse(String message, File audioFile, ParseObject po, String columnName){

            if(audioFile != null){
                Log.d("EB", "audioFile is not NULL: " + audioFile.toString());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                BufferedInputStream in = null;
                try {
                    in = new BufferedInputStream(new FileInputStream(audioFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                int read;
                byte[] buff = new byte[1024];
                try {
                    while ((read = in.read(buff)) > 0)
                    {
                        out.write(buff, 0, read);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] audioBytes = out.toByteArray();

                // Create the ParseFile
                ParseFile file = new ParseFile(audioFile.getName() , audioBytes);
                po.put(columnName, file);
                po.put("Description", message);

                // Upload the file into Parse Cloud
                file.saveInBackground();
                po.saveInBackground();
            }
            return po;
    }
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);



        setContentView(R.layout.activity_record);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mRecordButton = (ImageButton) findViewById(R.id.record);
        //mRecordButton.setText("Start recording");
        mRecordButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    ((Button) v).setText("Stop recording");
                } else {
                    ((Button) v).setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        });

        mPlayButton = (ImageButton) findViewById(R.id.play);
        //mPlayButton.setText("Start playing");
        mPlayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    ((Button)v).setText("Stop playing");
                } else {
                    ((Button)v).setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        });

        final EditText mTextBox = (EditText) findViewById(R.id.message);
        FloatingActionButton skip = (FloatingActionButton) findViewById(R.id.skip);
        FloatingActionButton save = (FloatingActionButton) findViewById(R.id.save);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSOS();
                finish();
            }

        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mTextBox.getText().toString();
                if (message.length() == 0) {
                    Utils.showDialog(AddSOSDetailActivity.this, getString(R.string.err_fields_empty));
                    return;
                }
                ParseObject parseObject = new ParseObject("SOS");
                File audioFile = new File(mFileName);
                ParseObject audio = uploadAudioToParse(message, audioFile, parseObject,"audio");
//                parseObject.put("objectName", "Sos AUdio");

//                parseObject.put("audio",audio);

//                ParseFile audio = new ParseFile();
                Log.d("audioUpload", message);
//                parseObject.saveInBackground();

                startSOS();
                finish();
            }

        });
    }

    void startSOS()
    {

        ParseQuery<ParseObject> pq = ParseQuery.getQuery("SOS");
        pq.include("UserID");
        pq.whereEqualTo("channelID",getIntent().getStringExtra("channelID"));
        final ProgressDialog dia = ProgressDialog.show(AddSOSDetailActivity.this, null, getString(R.string.starting_sos));
        pq.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject parseObject, ParseException e) {
                ParseObject sos = parseObject;
                ParseUser user = parseObject.getParseUser("UserID");

                Intent intent = new Intent(AddSOSDetailActivity.this, MessageActivity.class);

                intent.putExtra("channelID", getIntent().getStringExtra("channelID"));
                intent.putExtra("mysos", true);

                intent.putExtra("channelID", sos.getString("channelID"));
                intent.putExtra("username", user.getUsername());
                intent.putExtra("Description", user.getString("Description"));
                dia.dismiss();
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}