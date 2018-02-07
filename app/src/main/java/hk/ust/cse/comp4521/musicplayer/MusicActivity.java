package hk.ust.cse.comp4521.musicplayer;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import hk.ust.cse.comp4521.musicplayer.player.MusicPlayer;
import hk.ust.cse.comp4521.musicplayer.player.PlayerState;

public class MusicActivity extends AppCompatActivity implements Playlist.OnSongSelectedListener  {


    private static final String TAG = "MusicActivity";
    private static int songIndex = 0;

    private MusicPlayer player;

    // indicates if the player is running on a small screen device (false) or tablet (true)
    private boolean dualview = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i(TAG, "Activity: onCreate()");

        // Get the references to the buttons from the layout of the activity


        //	get	a	reference	to	the	song	title	TextView	in	the	UI


        // create a new instance of the music player
        player = MusicPlayer.getMusicPlayer();
        player.setContext(this);

        startSong(songIndex);

        // If the view being used contains the SongPlaying fragment in the layout, then
        // we are using dualview layout and the screen size is large. So both fragments
        // are on the screen. Set dualview to true
        if (findViewById(R.id.song) != null)
            dualview = true;

        if (!dualview) {
            if (findViewById(R.id.fragment_container) != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment firstFragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.NowPlaying));
                if (firstFragment == null) {
                    firstFragment = new SongPlaying();
                    ft.add(R.id.fragment_container, firstFragment, getResources().getString(R.string.NowPlaying));
                }
                else
                    ft.replace(R.id.fragment_container, firstFragment);
                ft.commit();
                Log.i(TAG, "First Fragment: " + firstFragment.getTag() + " Res ID: " + firstFragment.getId());
            }
        }

    }

    private	void	startSong(int	index){
        final String[] songFile	= getResources().getStringArray(R.array.filename);
        final String[] songList = getResources().getStringArray(R.array.Songs);

        player.start(getResources().getIdentifier(songFile[index], "raw", getPackageName()), songList[index]);

    }


    @Override
    protected void onDestroy() {

        Log.i(TAG, "Activity: onDestroy()");

        // reset the music player and release the media player
        player.reset();

        super.onDestroy();
    }

    @Override
    protected void onPause() {

        super.onPause();
        Log.i(TAG, "Activity: onPause()");
    }

    @Override
    protected void onRestart() {

        super.onRestart();
        Log.i(TAG, "Activity: onRestart()");
    }

    @Override
    protected void onResume() {

        super.onResume();
        Log.i(TAG, "Activity: onResume()");
    }

    @Override
    protected void onStart() {

        super.onStart();
        Log.i(TAG, "Activity: onStart()");
    }

    @Override
    protected void onStop() {

        super.onStop();
        Log.i(TAG, "Activity: onStop()");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                player.play_pause();
                break;
            case R.id.forward:
                player.forward();
                break;
            case R.id.rewind:
                player.rewind();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_playlist) {
            if (!dualview) {

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment secondFragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.SongList));
                if (secondFragment == null) {
                    secondFragment = new Playlist();
                }
                ft.replace(R.id.fragment_container, secondFragment, getResources().getString(R.string.SongList));
                ft.addToBackStack(null);
                ft.commit();
                Log.i(TAG, "Second Fragment: " + secondFragment.getTag() + " Res ID: " + secondFragment.getId());
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSongSelected(int id) {
        // This method is for the OnSongSelectedListener interface. When the user selects a song in the
        // play list, then this method is invoked
        player.reset();

        songIndex = id;
        startSong(id);

        if (!dualview) {

            Fragment firstFragment = getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.NowPlaying));
            Log.i(TAG, "First Fragment: " + firstFragment.getTag() + " Res ID: " + firstFragment.getId());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, firstFragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}
