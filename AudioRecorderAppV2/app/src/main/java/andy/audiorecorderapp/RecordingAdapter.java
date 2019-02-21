package andy.audiorecorderapp;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Created by Andy on 2/14/19.
 * Used for P2P
 */
public class RecordingAdapter extends ArrayAdapter<File> {
    private List<File> objects;
    private MainActivity activity = null;

    public RecordingAdapter(@NonNull Context context, int resource, @NonNull List<File> objects, MainActivity activity) {
        super(context, resource, objects);
        this.objects = objects;
        this.activity = activity;
    }

    public RecordingAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<File> objects) {
        super(context, resource, textViewResourceId, objects);
        this.objects = objects;
    }

    public RecordingAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public RecordingAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Inflate the layout for the list item.
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.recording_list_item, null);
        }


        //define your listener on inner items

        //define your global listener
        convertView.setOnClickListener(new View.OnClickListener(){
            private MediaPlayer mediaPlayer;

            public void onClick(View v) {
                File item = (File) v.getTag();
                mediaPlayer = new MediaPlayer();
                try {

                    mediaPlayer.setDataSource(item.getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(activity, "Playing audio", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    // make something
                }
            }
        });

        // Get the component
        final File file = getItem(position);
        // If not null insert all information from the component.
        if (file != null) {
            final TextView nameOfRecording = convertView.findViewById(R.id.nameOfRecording);
            final Button deleteBtn = convertView.findViewById(R.id.deleteRecording);
            final EditText renameText = convertView.findViewById(R.id.editName);
            final Button acceptEditingBtn = convertView.findViewById(R.id.acceptEditing);
            final Button renameBtn = convertView.findViewById(R.id.renameRecording);
            Log.d("hello", file.getName().split("\\.")[0] + "");
            nameOfRecording.setText(file.getName().split("\\.")[0]);
            renameText.setText(file.getName().split("\\.")[0]);
            renameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    renameBtn.setVisibility(View.INVISIBLE);
                    nameOfRecording.setVisibility(View.INVISIBLE);
                    deleteBtn.setVisibility(View.INVISIBLE);
                    acceptEditingBtn.setVisibility(View.VISIBLE);
                    renameText.setVisibility(View.VISIBLE);
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    objects.remove(file);
                    file.delete();
                    notifyDataSetChanged();
                }
            });

            acceptEditingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    File from = new File(activity.getExternalCacheDir(), activity.getExternalCacheDir().getAbsolutePath() + renameText.getText().toString() + ".3gp");

                    if (file.renameTo(from)){
                        Log.d("Renamed", "TRUE");
                    }else {
                        Log.d("DIDNT", "CHANGE");
                    }
                    nameOfRecording.setText(renameText.getText().toString());
                    renameBtn.setVisibility(View.VISIBLE);
                    nameOfRecording.setVisibility(View.VISIBLE);
                    deleteBtn.setVisibility(View.VISIBLE);
                    acceptEditingBtn.setVisibility(View.INVISIBLE);
                    renameText.setVisibility(View.INVISIBLE);
                    hideKeyboard(activity);
                }
            });

            convertView.setTag(file);
        }
        return convertView;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }




}
