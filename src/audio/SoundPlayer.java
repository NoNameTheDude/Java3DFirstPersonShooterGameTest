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
    
    public synchronized void play(Sound sound, boolean loop) {
        this.loop = loop;
        this.sound = sound;
        notify();
    }

    @Override
    public void run() {
        while (soundManager.isRunning()) {
            if (sound != null) {
//                System.out.println("player id=" + id);
//                int remaing = sound.getSize();
//                int start = 0;
//                int size = 400;
//                while (remaing > 0) {
//                    if (sound == null) {
//                        break;
//                    }
//                    line.write(sound.getData(), start, Math.min(size, remaing));
//                    start += size;
//                    remaing -= size;
//                    
//                    try {
//                        Thread.yield();
//                        Thread.sleep(1);
//                    } catch (InterruptedException ex) {
//                    }
//                }
                line.write(sound.getData(), 0, sound.getSize());
                if (!loop) {
                    sound = null;
                }
            }
            
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
//            try {
//                Thread.sleep(5);
//            } catch (InterruptedException ex) {
//            }
        }
    }

    public void stop() {
        sound = null;
        loop = false;
        line.flush();
    }
    
}
