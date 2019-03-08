package andy.audiorecorderappv3.Fragments;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;

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
    private int timeBeforeReturning = 5000;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private PathInterpolator pathInterpolator;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflade the fragment layout
        View view = inflater.inflate(R.layout.fragment_send, null, false);
        setHasOptionsMenu(true);

        ImageView img = view.findViewById(R.id.sendImage);
        img.setRotation(10);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Path path = new Path();

            path.quadTo(img.getX(), img.getY(), img.getX() - 40, img.getY());
            path.quadTo(img.getX() - 50, img.getY() , img.getX() - 80, img.getY() +  2);
            path.quadTo(img.getX() - 80, img.getY() + 2, img.getX() - 140, img.getY() +  4);
            path.quadTo(img.getX() - 140, img.getY() + 4, img.getX() - 200, img.getY() +  8);
            path.quadTo(img.getX() - 200, img.getY() + 8, img.getX() - 260, img.getY() +  24);
            path.quadTo(img.getX() - 260, img.getY() + 24, img.getX() - 300, img.getY() +  60);
            path.quadTo(img.getX()-300, img.getY()+60, img.getX() - 290, img.getY() +  123);
            path.quadTo(img.getX()-290, img.getY()+123, img.getX()-260, img.getY() +  143);
            path.quadTo(img.getX()-260, img.getY()+143, img.getX()-228, img.getY() +  154);
            path.quadTo(img.getX()-228, img.getY()+154, img.getX() -194, img.getY() +  163);
            path.quadTo(img.getX()-194, img.getY()+163, img.getX() -150, img.getY() +  158);
            path.quadTo(img.getX()-150, img.getY()+158, img.getX() -113, img.getY() +  153);
            path.quadTo(img.getX()-113, img.getY()+153, img.getX() -70, img.getY() +  143);
            path.quadTo(img.getX()-70, img.getY()+143, img.getX() -26, img.getY() +  120);
            path.quadTo(img.getX()-26, img.getY()+120, img.getX() + 26, img.getY() +  96);
            path.quadTo(img.getX()+26, img.getY()+96, img.getX() + 78, img.getY() +  70);
            path.quadTo(img.getX()+78, img.getY()+70, img.getX() + 148, img.getY() +  43);
            path.quadTo(img.getX()+228, img.getY()+10, img.getX() + 600, img.getY() - 71);
            ObjectAnimator animator = ObjectAnimator.ofFloat(img, "translationX", "translationY" , path);

            animator.setDuration(4000);

            animator.start();

        } else {
            // Create animator without using curved path
        }
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
