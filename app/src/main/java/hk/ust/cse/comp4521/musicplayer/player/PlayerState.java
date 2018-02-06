package hk.ust.cse.comp4521.musicplayer.player;

/**
 * Created by muppala on 13/2/16.
 */

public enum PlayerState {
    Retrieving, // the MediaRetriever is retrieving music
    Stopped,    // media player is stopped and not prepared to play
    Preparing,  // media player is preparing...
    Ready,     // media player is ready to play
    Playing,    // playback active
    Paused,      // playback paused
    Reset       // Player is reset and null

}