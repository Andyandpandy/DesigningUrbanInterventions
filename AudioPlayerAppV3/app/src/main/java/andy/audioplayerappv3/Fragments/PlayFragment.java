package andy.audioplayerappv3.Fragments;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import andy.audioplayerappv3.R;


/**
 * Created by Andy on 2/21/19.
 * Used for P2P
 */
public class PlayFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private String fileName;
    private long startTime;
    private TextView timerTextView;
    private Handler mHandler = new Handler();
    private long mInterval = 1000;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private ArrayList<String> ids;
    private Uri playFileName;
    private boolean readyToPlay = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ids = new ArrayList<>();


        FirebaseApp.initializeApp(getActivity());
        mediaPlayer = new MediaPlayer();
        getSoundFilenames();

        playSound();

    }

    private void getSoundFilenames() {
        mStorageRef = FirebaseStorage.getInstance("gs://urban-inverventions-audio.appspot.com").getReference();
        mDatabase = FirebaseDatabase.getInstance("https://urban-inverventions-audio.firebaseio.com/").getReference();
        Log.d("Play", "file");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("yes", "Lets go");
                DataSnapshot lastData = null;
                ids = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.d("yes", (String) data.getValue());
                    lastData = data;
                    ids.add(data.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("err", databaseError.getMessage());
            }
        });
    }

    private void playSound(){
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                while (true) {
                    Log.d("Ready", readyToPlay + "");
                    if (readyToPlay && !ids.isEmpty()) {
                        readyToPlay = false;
                        generateRandomIndex();

                        mStorageRef.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {

                                playFileName = uri;
                                Log.d("url", playFileName.toString());

                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                try {
                                    mediaPlayer.setDataSource(getActivity().getApplicationContext(), playFileName);
                                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mediaPlayer) {
                                            mediaPlayer.start();

                                            startTimer();
                                        }
                                    });
                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            stopTimer();
                                            mediaPlayer.release();
                                            try {
                                                Thread.sleep(7000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            readyToPlay = true;
                                        }
                                    });
                                } catch (
                                        IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                return null;
            }
        }.execute();
    }

    private void generateRandomIndex() {
        Random random = new Random();
        int idx = random.nextInt(ids.size());
        fileName = ids.get(idx);
        Log.d("new file", idx + "");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflade the fragment layout
        View view = inflater.inflate(R.layout.fragment_play, null, false);
        setHasOptionsMenu(true);

        timerTextView = view.findViewById(R.id.playTimer);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopTimer();
    }



    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {

                updateStatus(); //this function can change value of mInterval.

            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void updateStatus() {
        if (startTime == 0){
            return;
        }
        int seconds = Integer.parseInt(((System.currentTimeMillis() - startTime) / 1000) + "");
        timerTextView.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
    }

    void startTimer() {
        mStatusChecker.run();
        startTime = System.currentTimeMillis();
    }

    void stopTimer() {
        mHandler.removeCallbacks(mStatusChecker);
        startTime = 0;
        timerTextView.setText("00:00");
    }

}
