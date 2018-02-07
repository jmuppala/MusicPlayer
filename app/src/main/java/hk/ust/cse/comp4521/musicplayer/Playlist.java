package hk.ust.cse.comp4521.musicplayer;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Playlist extends Fragment {

    private static final String TAG = "Playlist";
    // the host activity should register itself as a listener and implement the interface methods
    // this variable keeps track of the reference to the host activity
    OnSongSelectedListener mListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        // Create a layout for the fragment with the buttons:
        // and set it to the view of this fragment
        View view = inflater.inflate(R.layout.activity_playlist, container, false);
        Log.i(TAG, "onCreateView()");

        // create a string array and initialize it with string array resources from strings.xml
        final String[] songList = getResources().getStringArray(R.array.Songs);

        // create a list adapter and supply it to the listview so that the list of songs can
        // be displayed in the listview

        ListView playListView = (ListView) view.findViewById(R.id.playListView);
        playListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.playlist_item, R.id.songlist, songList));

        playListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                        // position gives the index of the song selected by the user
                        // return the information about the selected song to MusicActivity
                        mListener.onSongSelected(position);
                    }
                }
        );

        return view;

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof OnSongSelectedListener) {
            mListener = (OnSongSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnSongSelectedListener) {
            mListener = (OnSongSelectedListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // the interface that must be implemented by the host activity for communicating from the
    // fragment to the activity
    public interface OnSongSelectedListener {
        public void onSongSelected(int id);
    }
}
