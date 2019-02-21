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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private Button stopBtn, recordBtn;
    private ListView recordingHistory;
    private MediaRecorder myAudioRecorder;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String fileName;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_recorder_layout);

        ensurePermissionGranted();

        stopBtn = findViewById(R.id.stopRecordingBtn);
        recordBtn = findViewById(R.id.recordBtn);
        recordingHistory = findViewById(R.id.recordingHistory);
        stopBtn.setEnabled(false);

        createNewRecorder();

        updateListView();

        recordingHistory.setItemsCanFocus(true);

    }

    public void onRecordClick(View btn) {
        createNewRecorder();
        try {
            myAudioRecorder.prepare();
        } catch (IllegalStateException ise) {
            Log.d("OUT", "SOMETHING WENT WRONG ISE");
        } catch (IOException ioe) {
            Log.d("OUT", "SOMETHING WENT WRONG IO" + ioe.getMessage());
        }
        myAudioRecorder.start();
        recordBtn.setEnabled(false);
        recordBtn.setBackgroundColor(getResources().getColor(R.color.grey));
        stopBtn.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
    }

    public void onStopClick(View v) {
        myAudioRecorder.release();
        myAudioRecorder = null;
        recordBtn.setEnabled(true);
        recordBtn.setBackgroundColor(getResources().getColor(R.color.red));
        stopBtn.setEnabled(false);
        Toast.makeText(getApplicationContext(), "Audio Recorder stopped", Toast.LENGTH_LONG).show();
        updateListView();
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



    private void ensurePermissionGranted() {
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
    }

    private void updateListView() {
        File[] list = getExternalCacheDir().listFiles();
        Log.d("Length", list.length + "");
        ArrayList<File> fileNames = new ArrayList<>();
        for (File file : list) {
            Log.d("IterFiles", file.getName());
            fileNames.add(file);
        }

        recordingHistory.setAdapter(new RecordingAdapter(this, R.layout.audio_recorder_layout, new ArrayList<File>(fileNames),this));

    }

    private void createUntitledRecording() {
        // Record to the external cache directory for visibility
        int idx = 0;
        boolean isUsed = false;
        while (!isUsed) {
            int local = idx;
            for (File file : getExternalCacheDir().listFiles()) {
                if (file.getName().equals("untitled" + idx + ".3gp")) {
                    idx ++;
                    break;
                }
            }
            if (idx == local){
                isUsed = true;
            }
        }
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/untitled" + idx + ".3gp";
        Log.d("OUT", fileName);
        myAudioRecorder.setOutputFile(fileName);

    }

    private void createNewRecorder() {
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        createUntitledRecording();
    }
}
