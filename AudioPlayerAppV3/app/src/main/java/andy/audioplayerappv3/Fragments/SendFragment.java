package andy.audioplayerappv3.Fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.Random;

import andy.audioplayerappv3.R;

/**
 * Created by Andy on 3/7/19.
 * Used for P2P
 */
public class SendFragment extends Fragment {


    private long startTime;
    private Handler mHandler = new Handler();
    private long mInterval = 1000;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
