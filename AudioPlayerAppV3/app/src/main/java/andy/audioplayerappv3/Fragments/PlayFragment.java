package andy.audioplayerappv3.Fragments;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
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
    private ImageButton startPlayingBtn;
    private ImageButton pausePlayingBtn;
    private ImageButton stopPlayingBtn;
    private File[] files;
    private Bundle bundle;
    private boolean paused;
    private boolean justRecorded;
    private long startTime;
    private TextView timerTextView;
    private Handler mHandler = new Handler();
    private long mInterval = 1000;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private ArrayList<String> ids;
    private Uri playFileName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ids = new ArrayList<>();

        files = getActivity().getExternalCacheDir().listFiles();
        Log.d("amount", files.length + "");

        FirebaseApp.initializeApp(getActivity());

        playSoundFile();

    }

    private void playSoundFile() {
        mStorageRef = FirebaseStorage.getInstance("gs://urban-inverventions-audio.appspot.com").getReference();
        mDatabase = FirebaseDatabase.getInstance("https://urban-inverventions-audio.firebaseio.com/").getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot lastData = null;
                ids = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Log.d("yes", (String) data.getValue());
                    lastData = data;
                    ids.add(data.getValue().toString());
                }
                if (!ids.isEmpty()) {
                    generateRandomIndex();
                    final long ONE_MEGABYTE = 1024 * 1024;
                    mStorageRef.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            playFileName = uri;
                            Log.d("url", playFileName.toString());
                            Log.d("Hello", "pls");
                            String url = "gs://urban-inverventions-audio.appspot.com"; // your URL here
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
                                            Thread.sleep(4000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        playSoundFile();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else {
                    try {
                        Thread.sleep(10000);
                        playSoundFile();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void generateRandomIndex() {
        Random random = new Random();
        int idx = random.nextInt(ids.size());
        fileName = ids.get(idx);

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

    public void onPlayClick() {
        if (mediaPlayer != null){
                mediaPlayer.start();
        }
        Log.d("new", "media");
        mediaPlayer = new MediaPlayer();
        try {
            Log.d("we try", "yes");
            mediaPlayer.setDataSource(files[0].getAbsolutePath());
            Log.d("we try", "no");
            mediaPlayer.prepare();
            Log.d("we try", "maybe");
            // Play video when the media source is ready for playback.
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Log.d("we try", "lets see");
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    onPlayClick();
                }
            });

        } catch (Exception e) {
            // make something
            Log.d("Exce", e.getMessage());
        }
        startTimer();
    }

    public void onPauseClick() {
        paused = true;
        try {
            mediaPlayer.pause();
        } catch (Exception e) {
            // make something
        }
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
        if (paused){
            startTime += 1000;
        }else {
            int seconds = Integer.parseInt(((System.currentTimeMillis() - startTime) / 1000) + "");
            timerTextView.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
        }
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
