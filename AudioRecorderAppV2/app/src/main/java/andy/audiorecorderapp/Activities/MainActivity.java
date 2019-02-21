package andy.audiorecorderapp.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

import andy.audiorecorderapp.Fragments.FromHomeToPlay;
import andy.audiorecorderapp.Fragments.FromHomeToRecord;
import andy.audiorecorderapp.Fragments.FromRecordToPlay;
import andy.audiorecorderapp.Fragments.HomeFragment;
import andy.audiorecorderapp.Fragments.PlayFragment;
import andy.audiorecorderapp.Fragments.RecordFragment;
import andy.audiorecorderapp.R;

public class MainActivity extends AppCompatActivity implements
        FromHomeToRecord,
        FromHomeToPlay,
        FromRecordToPlay {


    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;

    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_recorder_layout);

        ensurePermissionGranted();

        fm = getSupportFragmentManager();

        openFragment(new HomeFragment(), true);

        // Kill switch
        // clearDirectoryOfSoundFiles();
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragment.setArguments(bundle);
        openFragment(fragment, true);
    }

    @Override
    public void homeToPlay() {
        Fragment fragment = new PlayFragment();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        openFragment(fragment, true);
    }

    @Override
    public boolean onSupportNavigateUp(){
        for (int i = 1; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return true;
    }

    @Override
    public void recordToPlay(String path) {
        for (int i = 1; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        Fragment fragment = new PlayFragment();
        Bundle bundle = new Bundle();
        bundle.putString("file", path);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragment.setArguments(bundle);
        openFragment(fragment, true);
    }


    private void clearDirectoryOfSoundFiles(){
        File[] files = getExternalCacheDir().listFiles();
        for (int i = files.length-1; i >= 0; i--){
            boolean resp = files[i].delete();
            if (resp){
                Log.d("File","Deleted");
            }
        }
    }
}
