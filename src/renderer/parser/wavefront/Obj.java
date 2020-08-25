package renderer.parser.wavefront;

import java.util.ArrayList;
import java.util.List;
import renderer.core.Material;
import renderer.parser.wavefront.WavefrontParser.Face;

/**
 *
 * @author leonardo
 */
public class Obj {
    
    public List<Face> faces = new ArrayList<Face>();
    public Material material;
    
}
