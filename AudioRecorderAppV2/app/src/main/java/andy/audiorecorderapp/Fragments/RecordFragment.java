package andy.audiorecorderapp.Fragments;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;

import andy.audiorecorderapp.R;

/**
 * Created by Andy on 2/21/19.
 * Used for P2P
 */
public class RecordFragment extends Fragment {

    private String fileName;
    private MediaRecorder myAudioRecorder;
    private ImageButton startRecordingBtn;
    private ImageButton stopRecordingBtn;
    private FromRecordToPlay mListenerToPlay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflade the fragment layout
        View view = inflater.inflate(R.layout.fragment_record, null, false);

        startRecordingBtn = view.findViewById(R.id.startRecordingImgBtn);
        stopRecordingBtn = view.findViewById(R.id.stopRecordingImgBtn);

        startRecordingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecordStartClick();
            }
        });

        stopRecordingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecordStopClick();
            }
        });

        return view;
    }

    public void onRecordStartClick() {
        createNewRecorder();
        try {
            myAudioRecorder.prepare();
        } catch (IllegalStateException ise) {
            Log.d("OUT", "SOMETHING WENT WRONG ISE");
        } catch (IOException ioe) {
            Log.d("OUT", "SOMETHING WENT WRONG IO" + ioe.getMessage());
        }
        myAudioRecorder.start();
        startRecordingBtn.setEnabled(false);
        stopRecordingBtn.setEnabled(true);
        actionChange(ACTION.RECORD);
    }

    public void onRecordStopClick(){
        myAudioRecorder.release();
        myAudioRecorder = null;
        startRecordingBtn.setEnabled(true);
        stopRecordingBtn.setEnabled(false);
        actionChange(ACTION.STOP);
        mListenerToPlay.recordToPlay(fileName);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (myAudioRecorder != null) {
            myAudioRecorder.release();
            myAudioRecorder = null;
        }

    }

    private void createUntitledRecording() {
        // Record to the external cache directory for visibility
        int idx = 0;
        boolean isUsed = false;
        while (!isUsed) {
            int local = idx;
            for (File file : getActivity().getExternalCacheDir().listFiles()) {
                if (file.getName().equals("untitled" + idx + ".3gp")) {
                    idx ++;
                    break;
                }
            }
            if (idx == local){
                isUsed = true;
            }
        }
        fileName = getActivity().getExternalCacheDir().getAbsolutePath();
        fileName += "/untitled" + idx + ".3gp";
        Log.d("OUT", fileName);
        myAudioRecorder.setOutputFile(fileName);

    }

    private void createNewRecorder() {
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        createUntitledRecording();
    }

    public void actionChange(RecordFragment.ACTION action){
        switch (action){
            case RECORD:
                startRecordingBtn.setImageResource(R.drawable.recording);
                stopRecordingBtn.setImageResource(R.drawable.stop_idle);
                break;
            case STOP:
                startRecordingBtn.setImageResource(R.drawable.record_idle);
                stopRecordingBtn.setImageResource(R.drawable.stopped);
                break;
        }
    }

    public enum ACTION {
        RECORD, STOP
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListenerToPlay = (FromRecordToPlay) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FromRecordToPlay");
        }
    }
}
