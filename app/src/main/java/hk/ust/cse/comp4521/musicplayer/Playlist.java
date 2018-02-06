package hk.ust.cse.comp4521.musicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Playlist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // create a string array and initialize it with string array resources from strings.xml
        final String[] songList = getResources().getStringArray(R.array.Songs);

        // create a list adapter and supply it to the listview so that the list of songs can
        // be displayed in the listview

        ListView playListView = (ListView) findViewById(R.id.playListView);
        playListView.setAdapter(new ArrayAdapter<String>(this, R.layout.playlist_item, R.id.songlist, songList));

        playListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                        // position gives the index of the song selected by the user
                        // return the information about the selected song to MusicActivity
                        Intent in = new Intent(getApplicationContext(), MusicActivity.class);
                        in.putExtra("songIndex", position);

                        // return the same return code 100 that MusicActivity used to start this activity
                        setResult(100, in);

                        // exit from this activity
                        finish();
                    }
                }
        );

    }
}
