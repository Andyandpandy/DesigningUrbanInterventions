package andy.audiorecorderapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import andy.audiorecorderapp.R;

/**
 * Created by Andy on 2/21/19.
 * Used for P2P
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {

    }

    private FromHomeToRecord mListenerToRecord;
    private FromHomeToPlay mListenerToPlay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflade the fragment layout
        View view = inflater.inflate(R.layout.fragment_home, null, false);

        ImageButton playBtn = view.findViewById(R.id.playImgBtn);
        ImageButton recordBtn = view.findViewById(R.id.recordImgBtn);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListenerToPlay.homeToPlay();
            }
        });

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListenerToRecord.homeToRecord();
            }
        });

        return view;
    }

    /**
     * Get the Listener so we can callback to the MainActivity.
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListenerToRecord = (FromHomeToRecord) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FromHomeToRecord");
        }
        try {
            mListenerToPlay = (FromHomeToPlay) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FromHomeToPlay");
        }
    }
}
