package andy.audioplayerappv3.Services;

import android.net.Uri;
import android.os.AsyncTask;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by Andy on 3/7/19.
 * Used for P2P
 */
class FirebaseReceiver extends AsyncTask<Void, Void, Void> {

    private final String filename;
    private StorageReference mStorageRef;

    public FirebaseReceiver(String filename) {
        this.filename = filename;
    }

    @Override
    protected Void doInBackground(Void... voids) {



        return null;
    }
}
