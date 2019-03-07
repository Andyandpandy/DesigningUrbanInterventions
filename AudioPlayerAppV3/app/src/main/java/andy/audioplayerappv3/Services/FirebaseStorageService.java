package andy.audioplayerappv3.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by Andy on 3/7/19.
 * Used for P2P
 */
public class FirebaseStorageService extends Service {


    private IBinder binder = new StorageBinder();
    private String fileName;

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
        fileName = intent.getStringExtra("filename");
        FirebaseReceiver sender = new FirebaseReceiver(fileName);

        sender.execute();
        return super.onStartCommand(intent, flags, startId);
    }
}
