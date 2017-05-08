package au.com.raicovtechnologyservices.musaique;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Jamie on 28/04/2017.
 */

public class CustomPlayer extends android.media.MediaPlayer{

    public android.media.MediaPlayer mediaPlayer;
    public Context mContext;



    public CustomPlayer(Context context){
        this.mediaPlayer = new android.media.MediaPlayer();
        this.mContext = context;
    };

    public boolean playSong(String path, final Activity activity){
        try {
            if(this.isPlaying()){
                this.reset();
            }

            this.setDataSource(path);
            this.prepare();
            this.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(android.media.MediaPlayer mp) {
                    mp.start();
                    FloatingActionButton fab = (FloatingActionButton)activity.findViewById(R.id.np_play_pause);
                    fab.setImageResource(R.drawable.ic_pause_white_48dp);
                    fab.setClickable(true);
                    ProgressBar pb = (ProgressBar) activity.findViewById(R.id.now_playing_music_progress);
                    pb.setProgress(0);
                    pb.setMax(mp.getDuration());
                }
            });

            this.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();

                }
            });
            this.setOnErrorListener(new OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(mContext, "Media player entered Error state ",Toast.LENGTH_SHORT).show();
                    mp.reset();
                    return false;
                }
            });

        }catch(IOException e){
            e.printStackTrace();
            return false;
        }catch(IllegalArgumentException e){
            e.printStackTrace();
            return false;
        }


        return true;
    }

}
