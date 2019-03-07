package andy.audiorecorderappv3.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

/**
 * Created by Andy on 3/7/19.
 * Used for P2P
 */
public class FirebaseStorageService extends Service {


    private IBinder binder = new StorageBinder();

    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Get a DatabaseConnection.
     */
    public class StorageBinder extends Binder {
        public FirebaseStorageService getService(){
            return FirebaseStorageService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("No", "hello");
        FirebaseSender sender = new FirebaseSender(intent.getStringExtra("filename"), getApplicationContext());

        sender.execute();
        return START_NOT_STICKY;
    }
}
