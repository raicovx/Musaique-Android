package au.com.raicovtechnologyservices.musaique;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Jamie on 28/04/2017.
 */

public class CustomPlayer extends android.media.MediaPlayer {

    public android.media.MediaPlayer mediaPlayer;


    public CustomPlayer(){
        mediaPlayer = new android.media.MediaPlayer();
    };

    public boolean playSong(String path){
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(android.media.MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.reset();
                }
            });
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }


        return true;
    }
}
