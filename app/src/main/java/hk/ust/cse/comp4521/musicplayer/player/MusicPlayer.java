package hk.ust.cse.comp4521.musicplayer.player;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.Observable;

import hk.ust.cse.comp4521.musicplayer.Constants;
import hk.ust.cse.comp4521.musicplayer.MusicController;

public class MusicPlayer extends Observable {

    private static final String TAG = "Music Player";
    MediaPlayer player = null;
    private int position = 0;
    private int mSong = -1;
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

        restoreSongInfo();
        position = getPosition();

        start(mSong,mSongTitle);

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

            if (mSong >= 0) {

                saveSongInfo(mSong, mSongTitle);
                player = MediaPlayer.create(mContext, song);
                player.setLooping(false); // Set looping
                totalDuration = player.getDuration();
                player.seekTo(position);

                player.setOnCompletionListener(new OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        Intent intent = new Intent(mContext, MusicController.class);
                        intent.setAction(Constants.ACTION_COMPLETED);
                        mContext.startService(intent);

                    }
                });
                setState(PlayerState.Ready);
            }
        }
        else
            Log.i(TAG, "Start: Wrong Player State");


    }

    private void saveSongInfo(int index, String songTitle) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefed = prefs.edit();
        prefed.putInt("SongID", index);
        prefed.putString("Song Title", songTitle);
        prefed.commit();

    }

    private void restoreSongInfo() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSong = prefs.getInt("SongID", -1);
        mSongTitle = prefs.getString("Song Title", "");

    }

    public String getSongTitle() { return mSongTitle; }

    private void savePosition(int pos) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor prefed = prefs.edit();
        prefed.putInt("Position", pos);
        prefed.commit();
    }

    private int getPosition() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return prefs.getInt("Position", 0);
    }

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
            savePosition(position);
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
            savePosition(position);
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
            savePosition(position);
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
