package andy.audioplayerappv3.Fragments;

import android.media.MediaPlayer;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
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

    private enum ACTION {
        PLAY, PAUSE, STOP
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        files = getActivity().getExternalCacheDir().listFiles();
        Log.d("amount", files.length + "");

        mStorageRef = FirebaseStorage.getInstance("gs://urban-inverventions-audio.appspot.com").getReference();
        final long ONE_MEGABYTE = 1024 * 1024;
        mStorageRef.getRoot().getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    private void generateRandomIndex() {
        Random random = new Random();
        int idx = random.nextInt(files.length);
        fileName = files[idx].getAbsolutePath();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflade the fragment layout
        View view = inflater.inflate(R.layout.fragment_play, null, false);
        setHasOptionsMenu(true);

        timerTextView = view.findViewById(R.id.playTimer);

        startPlayingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlayClick();
            }
        });

        pausePlayingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPauseClick();
            }
        });

        stopPlayingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStopClick();
            }
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(bundle != null){
            onPlayClick();
        }
    }

    public void onPlayClick() {
        if (mediaPlayer != null){
            if (paused){
                mediaPlayer.start();
                paused = false;
                return;
            }
        }
        if (!justRecorded){
            generateRandomIndex();
        }
        justRecorded = false;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (Exception e) {
            // make something
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

    public void onStopClick() {

        try {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception e) {
            // make something
        }
        stopTimer();
        resetTimer();
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
    }

    void resetTimer() {
        timerTextView.setText("00:00");
    }
}
