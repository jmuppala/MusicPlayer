package hk.ust.cse.comp4521.musicplayer;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Playlist extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private static final String TAG = "Playlist";
    // the host activity should register itself as a listener and implement the interface methods
    // this variable keeps track of the reference to the host activity
    OnSongSelectedListener mListener = null;

    SimpleCursorAdapter mAdapter;

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


        // create an empty adapter we will use to display the data from MediaStore content provider
        // to be displayed in the listview
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.playlist_item, null,
                new String[] { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST},
                new int[] { R.id.songlist, R.id.songArtist }, 0);

        // prepare the loader. Either re-connect to an existing one, or start a new one
        getLoaderManager().initLoader(0, null, (LoaderManager.LoaderCallbacks<Cursor>) this);


        // create a list adapter and supply it to the listview so that the list of songs can
        // be displayed in the listview

        ListView playListView = (ListView) view.findViewById(R.id.playListView);
        playListView.setAdapter(mAdapter);

        playListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                        // position gives the index of the song selected by the user
                        // return the information about the selected song to MusicActivity
                        mListener.onSongSelected(id);
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
        public void onSongSelected(long id);
    }



    // These are the MediaStore columns that we will retrieve.
    static final String[] MUSIC_SUMMARY_PROJECTION = new String[] {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME, // name of the music file
            MediaStore.Audio.Media.TITLE, // title of the song
            MediaStore.Audio.Media.ARTIST //Artist's name
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new loader needs to be created. This case
        // has only one loader so we don't care about loader ID.
        // First, pick the base URI to use

        Uri baseUri;
        String select;
        CursorLoader curLoader;

        baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Log.i(TAG, "onCreateLoader");

        // now create the cursor loader and return it so that the loader will take care of
        // creating the Cursor for the data being displayed

        select = "((" + MediaStore.Audio.Media.DISPLAY_NAME + " NOTNULL) AND ("
                + MediaStore.Audio.Media.DISPLAY_NAME + " != '' ) AND ("
                + MediaStore.Audio.Media.IS_MUSIC + " != 0))";

        curLoader = new CursorLoader(getActivity(), baseUri, MUSIC_SUMMARY_PROJECTION, select,
                null, MediaStore.Audio.Media.ARTIST + " COLLATE LOCALIZED ASC" );

        return curLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // swap the new cursor in. The old cursor will be closed by the framework.

        Log.i(TAG, "onLoadFinished, starting");

        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // This is called when the last Cursor provided to onLoadFinished() above is
        // to be clased. We need to make sure we are no longer using it.

        mAdapter.swapCursor(null);

    }

}
