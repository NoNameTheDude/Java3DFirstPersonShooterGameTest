package audio;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;

/**
 * Music class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Music {
    
    private static Sequencer sequencer;
    private static boolean musicInitialized;

    public static void start() {
        initialize();
    }
    
    public static void initialize() {
        try {
            // setup sequencer
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            musicInitialized = true;
        } catch (Exception ex) {
            Logger.getLogger(Music.class.getName()).log(Level.SEVERE, null, ex);
            musicInitialized = false;
        }
    }
    
    public static void play(String musicName) {
        if (!musicInitialized) {
            return;
        }
        try {
            sequencer.setSequence(Music.class.getResourceAsStream("/res/audio/" + musicName));
            sequencer.stop();
            sequencer.setTickPosition(0);
            sequencer.start();
        } 
        catch (Exception ex) {
            Logger.getLogger(Music.class.getName()).log(Level.SEVERE, null, ex);
            musicInitialized = false;
        }
    }

}