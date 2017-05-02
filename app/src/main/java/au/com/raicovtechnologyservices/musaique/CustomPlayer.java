package au.com.raicovtechnologyservices.musaique;

import android.content.Context;
import android.media.MediaPlayer;
import android.renderscript.RSInvalidStateException;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Jamie on 28/04/2017.
 */

public class CustomPlayer extends android.media.MediaPlayer {

    public android.media.MediaPlayer mediaPlayer;
    public Context mContext;

    public CustomPlayer(Context context){
        this.mediaPlayer = new android.media.MediaPlayer();
        this.mContext = context;
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
            mediaPlayer.setOnErrorListener(new OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(mContext, "Media player entered Error state",Toast.LENGTH_SHORT).show();
                    mediaPlayer.reset();
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
