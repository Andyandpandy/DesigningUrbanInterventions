package andy.audiorecorderappv3.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

import andy.audiorecorderappv3.R;
import andy.audiorecorderappv3.Services.FirebaseStorageService;


/**
 * Created by Andy on 3/7/19.
 * Used for P2P
 */
public class SendFragment extends Fragment {


    private FirebaseStorageService mService;
    private boolean mBound;
    private Intent intent;
    private Bundle bundle;
    private String fileName;
    private Handler mHandler;
    private int mInterval = 1000;
    private long startTime = 0;
    private FromSendToRecord mListenerToSend;
    private int timeBeforeReturning = 10000;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle != null) {
            if (bundle.getString("filename") != null) {
                fileName = bundle.getString("filename");
                Log.d("success", fileName + "");
            }else {
                Log.d("failed", "no filename");
            }
        }

        mHandler = new Handler();

        intent = new Intent(getActivity(), FirebaseStorageService.class);
        intent.putExtra("filename", fileName);
        getActivity().startService(intent);
        startTimer();
        FirebaseApp.initializeApp(getActivity());

        mStorageRef = FirebaseStorage.getInstance("gs://urban-inverventions-audio.appspot.com").getReference();
        mDatabase = FirebaseDatabase.getInstance("https://urban-inverventions-audio.firebaseio.com/").getReference();
        Uri file = Uri.fromFile(new File(fileName));
        final String id = UUID.randomUUID().toString();
        StorageReference riversRef = mStorageRef.child(id);
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        final StorageMetadata downloadUrl = taskSnapshot.getMetadata();
                        Log.d("TAG", downloadUrl.getPath());
                        mDatabase.push().setValue(downloadUrl.getPath());
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("TAG", "UNSUCCESSFUL");
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflade the fragment layout
        View view = inflater.inflate(R.layout.fragment_send, null, false);
        setHasOptionsMenu(true);




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }


    @Override
    public void onStop() {
        super.onStop();
        stopTimer();
    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            FirebaseStorageService.StorageBinder binder =
                    (FirebaseStorageService.StorageBinder) service;
            mService = binder.getService();
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListenerToSend = (FromSendToRecord) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FromRecordToPlay");
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("tag", System.currentTimeMillis() - startTime + "");

                if (System.currentTimeMillis() - startTime > timeBeforeReturning){
                    Log.d("hl", "DONE");
                    mListenerToSend.returnToRecord();
                }


            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startTimer() {
        startTime = System.currentTimeMillis();
        mStatusChecker.run();
    }

    void stopTimer() {
        mHandler.removeCallbacks(mStatusChecker);
        startTime = 0;
    }

}
