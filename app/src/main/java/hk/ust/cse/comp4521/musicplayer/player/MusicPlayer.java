package hk.ust.cse.comp4521.musicplayer.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import java.util.Observable;

/**
 * Created by muppala on 13/2/16.
 */
public class MusicPlayer extends Observable {

    private static final String TAG = "Music Player";
    MediaPlayer player = null;
    private int position = 0;
    private int mSong;
    private String mSongTitle;
    private int rewforwTime = 5000; // ms

    private int currentDuration, totalDuration;

    PlayerState mState = PlayerState.Reset;

    Context mContext;

    private static final MusicPlayer _instance = new MusicPlayer();

    private MusicPlayer(){

    }

    public static synchronized MusicPlayer getMusicPlayer(){
        return _instance;
    }

    public void setContext(Context c){

        mContext = c;

    }

    private void setState(PlayerState m){
        mState = m;
        setChanged();
        notifyObservers(mState);
    }

    public boolean isPlaying() {
        if (player != null)
            return player.isPlaying();
        else
            return false;
    }

    public void start(int song, String title){

        Log.i(TAG, "Start");

        if (mState == PlayerState.Reset){

            mSong = song;
            mSongTitle = title;

            player = MediaPlayer.create(mContext, song);
            player.setLooping(false); // Set looping
            totalDuration = player.getDuration();

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                public void onCompletion(MediaPlayer mp) {
                    reset();
                    start(mSong, mSongTitle);
                }
            });
            setState(PlayerState.Ready);
        }
        else
            Log.i(TAG, "Start: Wrong Player State");


    }

    public String getSongTitle() { return mSongTitle; }

    public void play_pause() {
        Log.i(TAG, "Play_pause");

        if (mState == PlayerState.Paused || mState == PlayerState.Ready){
            resume();
        }
        else if(mState == PlayerState.Playing) {
            pause();
        }
        else
            Log.i(TAG, "play_pause: Wrong Player State");

    }

    public void resume()
    {

        Log.i(TAG, "Resume");

        if (mState == PlayerState.Paused || mState == PlayerState.Ready){
            player.seekTo(position);
            player.start();
            setState(PlayerState.Playing);
        }
        else
            Log.i(TAG, "Resume: Wrong Player State");
    }

    public void pause()
    {
        Log.i(TAG, "Pause");

        if(mState == PlayerState.Playing)
        {
            player.pause();
            position = player.getCurrentPosition();
            setState(PlayerState.Paused);
        }
        else
            Log.i(TAG, "Pause: Wrong Player State");
    }

    public void rewind(){

        Log.i(TAG, "Rewind");

        if(mState == PlayerState.Playing)
        {
            player.pause();
            position = player.getCurrentPosition();
            player.seekTo((position - rewforwTime) < 0 ? 0: (position - rewforwTime));
            player.start();
        }
        else
            Log.i(TAG, "Rewind: Wrong Player State");
    }

    public void forward(){

        Log.i(TAG, "Forward");

        if(mState == PlayerState.Playing)
        {
            player.pause();
            position = player.getCurrentPosition();
            player.seekTo((position + rewforwTime) > totalDuration ? totalDuration-1000 : (position + rewforwTime));
            player.start();

        }
        else
            Log.i(TAG, "Forward: Wrong Player State");
    }

    public void stop()
    {
        if(mState == PlayerState.Playing)
        {
            Log.i(TAG, "Stop");
            player.stop();
            position = player.getCurrentPosition();
            player.release();
            player = null;
            setState(PlayerState.Stopped);
        }

    }

    public void reset()
    {
        if(player != null){
            Log.i(TAG, "Reset");
            player.stop();
            player.reset();
            player.release();
            position = 0;
            player = null;
            setState(PlayerState.Reset);
        }
    }

    public void reposition(int value) {

        Log.i(TAG, "Reposition "+value+"%");
        if(mState == PlayerState.Playing)
        {
            pause();
            position = (int) ((double) value*totalDuration / 100 ) ;
            resume();
        }

    }

    public int progress() {
        Double percentage = (double) 0;

        if (mState == PlayerState.Reset)
            return 0;

        currentDuration = player.getCurrentPosition();

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double) currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();

    }

    public String completedTime() {

        return milliSecondsToTimer (currentDuration);

    }

    public String remainingTime() {

        return milliSecondsToTimer (totalDuration - currentDuration);

    }

    private String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }
}
