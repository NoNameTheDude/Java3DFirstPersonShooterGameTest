package audio;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Sound class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Sound {

    private final String resource;
    private byte[] data;

    public Sound(String resource) {
        this.resource = resource;
        try {
            InputStream is = getClass().getResourceAsStream("/res/audio/" + resource);
            InputStream bis = new BufferedInputStream(is);            
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
            if (!ais.getFormat().matches(SoundManager.AUDIO_FORMAT)) {
                throw new RuntimeException("Sound " + resource + " format not compatible !");
            }
            long audioSize = ais.getFrameLength() * ais.getFormat().getFrameSize();
            data = new byte[(int) audioSize];            
            ais.read(data);
            ais.close();
        } catch (Exception ex) {
            Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
            //System.exit(-1);
            data = new byte[1];
        }
    }

    public String getResource() {
        return resource;
    }

    public byte[] getData() {
        return data;
    }
    
    public int getSize() {
        return data.length;
    }
    
}
