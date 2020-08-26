package audio;

import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;

/**
 * SoundManager class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class SoundManager {
    
    public static final AudioFormat AUDIO_FORMAT 
            = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, 11025, 8, 1, 1, 11025, true);
    
    public static final int MAX_SIMULTANEOUS_SOUNDS = 10;
    private boolean running = false;
    
    private final List<SoundPlayer> soundPlayers = new ArrayList<>();

    private static SoundManager instance;
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
            instance.start();
        }
        return instance;
    }
    
    private SoundManager() {
    }

    public boolean isRunning() {
        return running;
    }
    
    public void start() {
        running = true;
        for (int s = 0; s < MAX_SIMULTANEOUS_SOUNDS; s++) {
            SoundPlayer soundPlayer = new SoundPlayer(s + 1, this);
            if (soundPlayer.start()) {
                soundPlayers.add(soundPlayer);
            }
        }
    }

    public void play(Sound sound) {
        play(sound, false);
    }

    public void play(Sound sound, boolean loop) {
        for (SoundPlayer soundPlayer : soundPlayers) {
            if (soundPlayer.isAvailable()) {
                soundPlayer.play(sound, loop);
                break;
            }
        }
    }
    
    public void stop(Sound sound) {
        for (SoundPlayer soundPlayer : soundPlayers) {
            if (soundPlayer.getSound() == sound) {
                soundPlayer.stop();
            }
        }
    }

    public void stopAll() {
        soundPlayers.forEach((soundPlayer) -> {
            soundPlayer.stop();
        });
    }

}
