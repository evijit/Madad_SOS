package kgp.tech.interiit.sos;

/**
 * Created by akshaygupta on 26/01/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.IOException;

import kgp.tech.interiit.sos.Utils.Utils;


public class RecordFragment extends Activity
{
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private Button mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private Button   mPlayButton = null;
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

    public RecordFragment() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.fragment_record);

        mRecordButton = (Button) findViewById(R.id.record);
        mRecordButton.setText("Start recording");
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

        mPlayButton = (Button) findViewById(R.id.play);
        mPlayButton.setText("Start playing");
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
        Button skip = (Button) findViewById(R.id.skip);
        Button save = (Button) findViewById(R.id.save);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordFragment.this, AnimatedButtons.class));
                finish();
            }

        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mTextBox.getText().toString();
                if (message.length() == 0) {
                    Utils.showDialog(RecordFragment.this, getString(R.string.err_fields_empty));
                    return;
                }
                ParseObject parseObject = new ParseObject("SOS");
                parseObject.put("Description", message);
                ParseFile audio = new ParseFile();

                parseObject.put("audio",audio);
                parseObject.saveInBackground();
                startActivity(new Intent(RecordFragment.this, AnimatedButtons.class));
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