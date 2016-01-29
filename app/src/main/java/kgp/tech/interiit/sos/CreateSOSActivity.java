package kgp.tech.interiit.sos;

/**
 * Created by akshaygupta on 26/01/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.support.design.widget.FloatingActionButton;
import android.text.format.DateUtils;
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
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
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
import java.util.Date;

import kgp.tech.interiit.sos.Utils.DateFormater;
import kgp.tech.interiit.sos.Utils.Utils;
import kgp.tech.interiit.sos.Utils.comm;


public class CreateSOSActivity extends Activity
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

    private ParseObject sos = null;
    public CreateSOSActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    private void uploadAudioToParse(String message, File audioFile, String columnName){

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
            sos.put(columnName, file);
            sos.put("Description", message);

            // Upload the file into Parse Cloud
            file.saveInBackground();
            Log.d("Details",message);
            sos.saveInBackground();
        }
    }
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.d("CreateSOS","Creating sos");
        sos = comm.sendSOS(getString(R.string.default_sos));
        if(sos==null)
        {
            Utils.showDialog(this,"Please try again.");
            return;
        }
        Toast.makeText(this, "SOS Signal has been sent", Toast.LENGTH_LONG).show();
        SharedPreferences sp = getSharedPreferences("SOS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("sosID", sos.getObjectId());
        editor.putString("username", ParseUser.getCurrentUser().getUsername());
        editor.putString("channelID", sos.getString("channelID"));
        editor.putString("Description", sos.getString("Description"));
        editor.putString("displayname", ParseUser.getCurrentUser().getString("displayname"));
        Log.d("CreateSOS", sos.getString("channelID"));
        Log.d("CreateSOS",(new Date()).toString());
        editor.putString("createdAt", DateFormater.formatTimeDate(new Date()));
        editor.commit();
        Log.d("SPCSA", sos.getObjectId());
        Log.d("SPCSA", String.valueOf(sp.contains("sosID")));

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
        Button save = (Button) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mTextBox.getText().toString();
                if (message.length() == 0) {
                    Utils.showDialog(CreateSOSActivity.this, getString(R.string.err_fields_empty));
                    return;
                }
                if(sos==null)
                {
                    Utils.showDialog(CreateSOSActivity.this,"Please try again.");
                    return;
                }
                File audioFile = new File(mFileName);
                uploadAudioToParse(message, audioFile,"audio");
                Log.d("audioUpload", message);
                startSOS();
                finish();
            }

        });
    }

    void startSOS()
    {
        Intent intent = new Intent(CreateSOSActivity.this, MessageActivity.class);
        intent.putExtra("sosID", sos.getObjectId());
        intent.putExtra("channelID", sos.getString("channelID"));
        intent.putExtra("createdAt", DateFormater.formatTimeDate(sos.getCreatedAt()));
        intent.putExtra("username", ParseUser.getCurrentUser().getUsername());
        intent.putExtra("Description", sos.getString("Description"));
        intent.putExtra("displayname", ParseUser.getCurrentUser().getString("displayname"));
        startActivity(intent);
        finish();
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