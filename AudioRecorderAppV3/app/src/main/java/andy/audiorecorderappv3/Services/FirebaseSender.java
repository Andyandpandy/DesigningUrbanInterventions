package andy.audiorecorderappv3.Services;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by Andy on 3/7/19.
 * Used for P2P
 */
class FirebaseSender extends AsyncTask<Void, Void, Void> {

    private final String filename;
    private final Context context;
    private StorageReference mStorageRef;
    private StorageMetadata downloadUrl;

    public FirebaseSender(String filename, Context context) {
        this.filename = filename;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        FirebaseApp.initializeApp(this.context);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(filename));
        StorageReference riversRef = mStorageRef.child(filename);
        Log.d("TAG", "ENTERED");
        Log.d("TAG", filename);
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        downloadUrl = taskSnapshot.getMetadata();
                        Log.d("TAG", downloadUrl.getPath());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("TAG", "UNSUCCESSFUL");
                    }
                });




        return null;
    }
}
