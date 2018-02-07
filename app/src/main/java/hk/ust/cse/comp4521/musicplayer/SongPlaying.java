package hk.ust.cse.comp4521.musicplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import hk.ust.cse.comp4521.musicplayer.player.MusicPlayer;
import hk.ust.cse.comp4521.musicplayer.player.PlayerState;

public class SongPlaying extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Observer{

    private static final String TAG = "SongPlaying";
    private static ImageButton playerButton, rewindButton, forwardButton;
    public static Handler handler;
    private TextView songTitleText;
    private static int songIndex = 0;

    /*
     * Class Name: MusicPlayer
     *
     *    This class implements support for playing a music file using the MediaPlayer class in Android.
     *    It supports the following methods:
     *
     *    play_pause(): toggles the player between playing and paused states
     *    resume(): resume playing the current song
     *    pause(): pause the currently playing song
     *    rewind(): rewind the currently playing song by one step
     *    forward(): forward the currently playing song by one step
     *    stop(): stop the currently playing song
     *    reset(): reset the music player and release the MediaPlayer associated with it
     *    reposition(value): repositions the playing position of the song to value% and resumes playing
     *
     *    progress(): returns the percentage of the playback completed. useful to update the progress bar
     *    completedTime(): Amount of the song time completed playing
     *    remainingTime(): Remaining time of the song being played
     *
     *    You should use these methods to manage the playing of the song.
     *
     */
    private MusicPlayer player;

    private SeekBar songProgressBar;
    private TextView complTime, remTime;

    public SongPlaying() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        handler = new Handler();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_playing, container, false);

        // create a new instance of the music player
        player = MusicPlayer.getMusicPlayer();

        player.addObserver(this);

        Log.i(TAG, "onCreateView()");
        playerButton = (ImageButton) view.findViewById(R.id.play);
        playerButton.setOnClickListener(this);

        rewindButton = (ImageButton) view.findViewById(R.id.rewind);
        rewindButton.setOnClickListener(this);

        forwardButton = (ImageButton) view.findViewById(R.id.forward);
        forwardButton.setOnClickListener(this);

        songTitleText = (TextView) view.findViewById(R.id.songTitle);

        // get reference to the SeekBar, completion time and remaining time.
        songProgressBar = (SeekBar) view.findViewById(R.id.songProgessBar);

        //set max to 100, means that complete song has been played
        songProgressBar.setMax(100);
        //initializing SeekBarChangeListener
        songProgressBar.setOnSeekBarChangeListener(this);

        complTime = (TextView) view.findViewById(R.id.songCurrentDurationLabel);
        remTime = (TextView) view.findViewById(R.id.songRemainingDurationLabel);

        if(player.isPlaying()){
            updateSongProgress();
        }

        // shows the current progress of the player
        songProgressBar.setProgress(player.progress());
        complTime.setText(player.completedTime());
        remTime.setText("-" + player.remainingTime());

        return view;
    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "onDestroy()");

        handler.removeCallbacks(songProgressUpdate);

        player.deleteObserver(this);
        handler = null;
        player = null;

        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        Log.i(TAG, "onAttach");

    }

    @Override
    public void onDetach() {

        super.onDetach();
        Log.i(TAG, "onDetach");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

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
    public void update(Observable observable, Object o) {
        // The update method is called whenever the Music Player experiences
        // change of state
        // arg1 returns the current state of the player in the form of
        // PlayerState enum variable
        // Use the switch to recognize which state the player just entered and
        // take appropriate
        // action to handle the change of state. Here we update the play/pause
        // button accordingly

        switch ((PlayerState) o) {
            case Ready:
                Log.i(TAG, "Activity: Player State Changed to Ready");
                songTitleText.setText(player.getSongTitle());
                playerButton.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
                songProgressBar.setProgress(player.progress());
                complTime.setText(player.completedTime());
                remTime.setText("-" + player.remainingTime());
                break;
            case Paused:
                Log.i(TAG, "Activity: Player State Changed to Paused");
                playerButton.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
                songProgressBar.setProgress(player.progress());
                complTime.setText(player.completedTime());
                remTime.setText("-" + player.remainingTime());
                break;
            case Stopped:
                Log.i(TAG, "Activity: Player State Changed to Stopped");
                cancelUpdateSongProgress();
                break;
            case Playing:
                Log.i(TAG, "Activity: Player State Changed to Playing");
                updateSongProgress();
                break;
            case Reset:
                Log.i(TAG, "Activity: Player State Changed to Reset");
                cancelUpdateSongProgress();
                break;
            default:
                break;
        }
    }

    public void updateSongProgress() {
        handler.postDelayed(songProgressUpdate, 500);
        playerButton.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
    }

    private Runnable songProgressUpdate = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            // initialize the Progress bar and the status of TextView
            // We want to modify the progress bar, but w can do it only
            // from the UI thread, To do this, we make use of the handler
            songProgressBar.setProgress(player.progress());
            complTime.setText(player.completedTime());
            remTime.setText("-" + player.remainingTime());
            // schedule another update for every 500 msec later
            handler.postDelayed(songProgressUpdate, 500);
        }
    };
    public void cancelUpdateSongProgress(){
        // cancel all callbacks that are already in the handler queue
        handler.removeCallbacks(songProgressUpdate);
        playerButton.setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        cancelUpdateSongProgress();
        if (fromUser && player.isPlaying())
            player.reposition(progress);
        updateSongProgress();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

