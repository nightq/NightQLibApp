package freedom.nightq.baselibrary.utils;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.threadPool.NormalEngine;

/**
 * Created by Nightq on 15/10/15.
 */
public class RingtoneUtils {

    private static MediaPlayer player;
    private static Object playerLock = new Object();

     /**
     *
     * @return
     */
    public static void playRingtone (final String assertFileName, final long ringTime) {
        NormalEngine.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                AssetFileDescriptor afd = null;
                try {
                    afd = NightQAppLib.getAppContext()
                            .getAssets().openFd(assertFileName);
                } catch (Exception e) {

                }
                if (afd != null) {
                    playRingtoneThread(afd, ringTime);
                }
            }
        });
    }

    /**
     *
     * @return
     */
    private static void playRingtoneThread (AssetFileDescriptor afd, final long ringTime){
//        Uri uri = Uri.parse("file:///android_asset/6deg_tips.mp3");
        try {
            synchronized (playerLock) {
                if (player != null && player.isPlaying()) {
                    player.stop();
                    player.release();
                }
                player = new MediaPlayer();
            }
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.setAudioStreamType(AudioManager.STREAM_RING);
            player.prepare();
            player.setLooping(false);
            player.start();

            String vendor = Build.MANUFACTURER;
            // for samsung S3, we meet a bug that the phone will
            // continue ringtone without stop
            // so add below special handler to stop it after 3s if
            // needed
            if (vendor != null && vendor.toLowerCase().contains("samsung")) {
                final MediaPlayer tmpPlayer = player;
                NormalEngine.getInstance().submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(ringTime == 0 ? 3000 : ringTime);
                            synchronized (playerLock) {
                                if (tmpPlayer != null && tmpPlayer.isPlaying()) {
                                    tmpPlayer.stop();
                                    tmpPlayer.release();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
            }
        } catch (Exception e) {

        }
    }


}
