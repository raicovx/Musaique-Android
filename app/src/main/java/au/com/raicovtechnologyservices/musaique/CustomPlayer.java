package au.com.raicovtechnologyservices.musaique;

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
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
