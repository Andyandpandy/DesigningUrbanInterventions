package andy.audiorecorderapp.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import andy.audiorecorderapp.Fragments.FromHomeToPlay;
import andy.audiorecorderapp.Fragments.FromHomeToRecord;
import andy.audiorecorderapp.Fragments.HomeFragment;
import andy.audiorecorderapp.Fragments.PlayFragment;
import andy.audiorecorderapp.Fragments.RecordFragment;
import andy.audiorecorderapp.R;

public class MainActivity extends AppCompatActivity implements FromHomeToRecord, FromHomeToPlay {


    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
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

        createNewRecorder();

        FragmentManager fm = getSupportFragmentManager();

        openFragment(new HomeFragment(), true);
        //fm.popBackStack();
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
        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
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


    /**
     * Helper method for fragment replacing/adding
     * @param selectedFragment Fragment
     * @param addToBackstack boolean
     */
    private void openFragment(Fragment selectedFragment, boolean addToBackstack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (addToBackstack){
            transaction.addToBackStack(selectedFragment.getClass().getSimpleName());
        }
        transaction.replace(R.id.fragment_container, selectedFragment, selectedFragment.getClass().getSimpleName());
        transaction.commit();
    }

    @Override
    public void homeToRecord() {
        Fragment fragment = new RecordFragment();
        Bundle bundle = new Bundle();

        fragment.setArguments(bundle);
        openFragment(fragment, true);
    }

    @Override
    public void homeToPlay() {
        Fragment fragment = new PlayFragment();
        Bundle bundle = new Bundle();

        fragment.setArguments(bundle);
        openFragment(fragment, true);
    }
}
