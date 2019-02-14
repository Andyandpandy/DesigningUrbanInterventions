package andy.audiorecorderapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private Button playBtn, stopBtn, recordBtn;
    private MediaRecorder myAudioRecorder;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String fileName;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_recorder_layout);

        // Record to the external cache directory for visibility
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERM", "NOT");

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_AUDIO_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else {
            Log.d("PERM", "GRANTED");
        }

        playBtn = findViewById(R.id.playRecording);
        stopBtn = findViewById(R.id.stopRecordingBtn);
        recordBtn = findViewById(R.id.recordBtn);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        Log.d("OUT", fileName);

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(fileName);
    }

    public void onRecordClick(View btn) {
        Log.d("OUT", "CLICKED");
        try {
            myAudioRecorder.prepare();
            Log.d("OUT", "PREPARING");

        } catch (IllegalStateException ise) {
            Log.d("OUT", "SOMETHING WENT WRONG ISE");
        } catch (IOException ioe) {
            Log.d("OUT", "SOMETHING WENT WRONG IO" + ioe.getMessage());
        }
        Log.d("OUT", "Starting");
        myAudioRecorder.start();
        Log.d("OUT", "Started");
        recordBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
    }

    public void onStopClick(View v) {
        myAudioRecorder.release();
        myAudioRecorder = null;
        recordBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Audio Recorder stopped", Toast.LENGTH_LONG).show();
    }

    public void onPlayClick(View v) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // make something
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }


    @Override
    public void onStop() {
        super.onStop();
        if (myAudioRecorder != null) {
            myAudioRecorder.release();
            myAudioRecorder = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
