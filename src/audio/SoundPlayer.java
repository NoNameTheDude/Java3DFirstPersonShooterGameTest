package audio;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

/**
 * SoundPlayer class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class SoundPlayer implements Runnable {
    
    private final int id;
    private final SoundManager soundManager;
    private Thread thread;
    private SourceDataLine line;
    private Sound sound;
    private boolean loop;
    
    public SoundPlayer(int id, SoundManager soundManager) {
        this.id = id;
        this.soundManager = soundManager;
    }
    
    private void createLine() throws Exception {
        Mixer mixer = AudioSystem.getMixer(null);
        SourceDataLine.Info sourceDataLineInfo 
                = new DataLine.Info(SourceDataLine.class, SoundManager.AUDIO_FORMAT);
        line = (SourceDataLine) mixer.getLine(sourceDataLineInfo);
    }

    public Sound getSound() {
        return sound;
    }
    
    public boolean start() {
        try {
            createLine();
            line.open();
            line.start();
            thread = new Thread(this);
            thread.start();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean isAvailable() {
        return sound == null;
    }
    
    public void play(Sound sound, boolean loop) {
        synchronized (this) {
            this.loop = loop;
            this.sound = sound;
            notify();
        }
    }

    @Override
    public void run() { 
        synchronized (this) {
            while (soundManager.isRunning()) {
                if (sound != null) {
                    line.write(sound.getData(), 0, sound.getSize());
                    line.drain(); // This method blocks until the draining is complete
                    if (!loop) {
                        sound = null;
                    }
                }
                if (sound == null) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }
    }

    public void stop() {
        sound = null;
        loop = false;
        line.flush();
    }
    
}
