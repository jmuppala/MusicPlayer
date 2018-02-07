package hk.ust.cse.comp4521.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import hk.ust.cse.comp4521.musicplayer.player.MusicPlayer;

// The service is going to be implemented as a Started Service. The activity and fragment will issue their commands
// through the use of startService() specifying an Intent that conveys the action to be taken by the service.
// When an already runnign service is called using startService(), only the onStartCommand() method is executed.
// This method will handle the incoming intents when startService() is called from the activity/fragment

public class MusicController extends Service implements MediaPlayer.OnErrorListener  {

    private static final String TAG = "MusicController";

    MusicPlayer player = null;

    private static int songIndex = 0;

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        // Pop up a message on the screen to show that the service is started
        Toast.makeText(this, "MusicController Service Created", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Service: onCreate()");

        // The music player is implemented as a Java Singleton class so that only one
        // instance of the player is present within the application. The getMusicPlayer()
        // method returns the reference to the instance of the music player class
        // get a reference to the instance of the music player
        // set the context for the music player to be this service
        player = MusicPlayer.getMusicPlayer();
        player.setContext(this);

        startSong(songIndex);

    }

    @Override
    public void onDestroy() {


        // Pop up a message on the screen to show that the service is started
        Toast.makeText(this, "MusicController Service Stopped", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Service: onDestroy()");

        player.reset();
        player = null;

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.i(TAG, "Service: onStartCommand()");

        // Any time startService() is called, the intent is delivered here and can be handled.
        handleIntent(intent);

        return super.onStartCommand(intent, flags, startId);
    }

    // handle the intent delivered to onStartCommand().
    private void handleIntent( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;

        // get the action specified in the intent. The actioins are given in Constants.
        String action = intent.getAction();

        if( action.equalsIgnoreCase( Constants.ACTION_PLAY_PAUSE ) ) {
            play_pause();
        } else if( action.equalsIgnoreCase( Constants.ACTION_PLAY ) ) {
            resume();
        } else if( action.equalsIgnoreCase( Constants.ACTION_PAUSE ) ) {
            play_pause();
        } else if( action.equalsIgnoreCase( Constants.ACTION_FORWARD ) ) {
            forward();
        } else if( action.equalsIgnoreCase( Constants.ACTION_REWIND ) ) {
            rewind();
        } else if( action.equalsIgnoreCase( Constants.ACTION_PREVIOUS ) ) {

        } else if( action.equalsIgnoreCase(Constants.ACTION_NEXT ) ) {

        } else if( action.equalsIgnoreCase( Constants.ACTION_STOP ) ) {
            stop();
        } else if( action.equalsIgnoreCase( Constants.ACTION_RESET ) ) {
            reset();
        } else if( action.equalsIgnoreCase( Constants.ACTION_SONG ) ) {
            reset();
            int id = intent.getIntExtra("Song",0);
            startSong(id);
        } else if( action.equalsIgnoreCase( Constants.ACTION_REPOSITION ) ) {
            int position = intent.getIntExtra("Position",0);
            reposition(position);
        } else if( action.equalsIgnoreCase( Constants.ACTION_COMPLETED ) ) {
            reset();
            startSong(songIndex);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "MusicController Music Player Failed", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Service: Music player failed");

        if (mp != null) {
            try {
                mp.stop();
                mp.release();
            }
            finally {
                mp = null;
            }
        }

        return false;
    }

    public void startSong(int index) {

        Log.i(TAG, "Service: startSong()");

        final String[] songFile = getResources().getStringArray(R.array.filename);
        final String[] songList = getResources().getStringArray(R.array.Songs);

        if (player != null) {
            songIndex = index;

            player.start(getResources().getIdentifier(songFile[index], "raw", getPackageName()), songList[index]);

        }
        else
            Log.i(TAG, "Service: startSong Null Player");
    }

    public void play_pause() {

        if (player != null) {
            // if player is playing, then abandon audio focus. we are pausing playback
            // else, get audio focus before we proceed to play the song.
            if (player.isPlaying()) {

                // update the pause button in the notification to play button
            }
            else {

                // update the pause button in the notification to pause button
            }
            Log.i(TAG, "Service: play_pause()");

            player.play_pause();

            // update notification with song information
        }
        else
            Log.i(TAG, "Service: play_pause() Null Player");
    }

    public void resume() {

        if (player != null) {
            Log.i(TAG, "Service: resume()");

            player.resume();

            // update the pause button in the notification to pause button
        }
        else
            Log.i(TAG, "Service: resume() Null Player");
    }

    public void pause() {

        if (player != null) {
            Log.i(TAG, "Service: pause()");

            player.pause();
            // update the pause button in the notification to play button
        }
        else
            Log.i(TAG, "Service: pause() Null Player");
    }

    public void rewind() {

        if (player != null) {
            Log.i(TAG, "Service: rewind()");
            player.rewind();
        }
        else
            Log.i(TAG, "Service: rewind() Null Player");
    }

    public void forward() {

        if (player != null) {
            Log.i(TAG, "Service: forward()");
            player.forward();
        }
        else
            Log.i(TAG, "Service: forward() Null Player");
    }

    public void stop() {

        if (player != null) {
            Log.i(TAG, "Service: stop()");
            player.stop();
        }
        else
            Log.i(TAG, "Service: stop() Null Player");
    }

    public void reset() {

        if (player != null) {
            Log.i(TAG, "Service: reset()");

            player.reset();
        }
        else
            Log.i(TAG, "Service: reset() Null Player");
    }

    public void reposition(int position) {

        if (player != null) {
            Log.i(TAG, "Service: reposition()");
            player.reposition(position);
        }
        else
            Log.i(TAG, "Service: reposition() Null Player");
    }
}
