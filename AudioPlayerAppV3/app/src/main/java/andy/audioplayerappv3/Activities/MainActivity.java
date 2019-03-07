package andy.audioplayerappv3.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

import andy.audioplayerappv3.Fragments.FromPlayToSend;
import andy.audioplayerappv3.Fragments.PlayFragment;
import andy.audioplayerappv3.R;


public class MainActivity extends AppCompatActivity implements
        FromPlayToSend {

    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_recorder_layout);

        fm = getSupportFragmentManager();

        openFragment(new PlayFragment(), true);

        // Kill switch
        clearDirectoryOfSoundFiles();
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
    public void playToSend() {
        Fragment fragment = new PlayFragment();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        openFragment(fragment, true);
    }

    @Override
    public boolean onSupportNavigateUp(){
        for (int i = 1; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        return true;
    }

    private void clearDirectoryOfSoundFiles(){
        if (getExternalCacheDir() != null) {
            File[] files = getExternalCacheDir().listFiles();

            for (int i = files.length - 1; i >= 0; i--) {
                boolean resp = files[i].delete();
                if (resp) {
                    Log.d("File", "Deleted");
                }
            }
        }
    }
}
