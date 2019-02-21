package andy.audiorecorderapp.Fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.Random;

import andy.audiorecorderapp.R;

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

    private enum ACTION {
        PLAY, PAUSE, STOP
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        files = getActivity().getExternalCacheDir().listFiles();
        Log.d("amount", files.length + "");
        bundle = getArguments();
        if (bundle != null) {
            if (bundle.getString("file") != null) {
                fileName = bundle.getString("file");
                justRecorded = true;
            } else {
                generateRandomIndex();
            }
        }else {
            generateRandomIndex();
        }
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

        startPlayingBtn = view.findViewById(R.id.startPlayingImgBtn);
        pausePlayingBtn = view.findViewById(R.id.pausePlayingImgBtn);
        stopPlayingBtn = view.findViewById(R.id.stopPlayingImgBtn);

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
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    actionChange(ACTION.STOP);
                }
            });
        } catch (Exception e) {
            // make something
        }
        actionChange(ACTION.PLAY);
    }

    public void onPauseClick() {
        paused = true;
        try {
            mediaPlayer.pause();
        } catch (Exception e) {
            // make something
        }
        actionChange(ACTION.PAUSE);
    }

    public void onStopClick() {

        try {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception e) {
            // make something
        }
        actionChange(ACTION.STOP);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void actionChange(ACTION action){
        switch (action){
            case PLAY:
                startPlayingBtn.setImageResource(R.drawable.playing);
                startPlayingBtn.setEnabled(false);
                pausePlayingBtn.setImageResource(R.drawable.pause_idle);
                pausePlayingBtn.setEnabled(true);
                stopPlayingBtn.setImageResource(R.drawable.stop_idle);
                stopPlayingBtn.setEnabled(true);
                break;
            case PAUSE:
                startPlayingBtn.setImageResource(R.drawable.play_idle);
                startPlayingBtn.setEnabled(true);
                pausePlayingBtn.setImageResource(R.drawable.paused);
                pausePlayingBtn.setEnabled(false);
                stopPlayingBtn.setImageResource(R.drawable.stop_idle);
                stopPlayingBtn.setEnabled(true);
                break;
            case STOP:
                startPlayingBtn.setImageResource(R.drawable.play_idle);
                startPlayingBtn.setEnabled(true);
                pausePlayingBtn.setImageResource(R.drawable.paused);
                pausePlayingBtn.setEnabled(false);
                stopPlayingBtn.setImageResource(R.drawable.stopped);
                stopPlayingBtn.setEnabled(false);
                break;
        }
    }
}
