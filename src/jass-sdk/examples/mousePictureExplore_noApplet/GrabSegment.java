import java.awt.*;
import jass.engine.*;
import jass.generators.*;
import jass.render.*;
import jass.patches.*;

/**
   Grab a line segment based on current and previous mouse position and make sound based on that
   @author Kees van den Doel (kvdoel@cs.ubc.ca)
*/

public class GrabSegment extends Out {
    private SegmentDataProvider segmentDataProvider;
    private float[] hsb = new float[3];
    protected float srate;
    private String syfile;
    private String wavfile;
    private LowpassColorSonificator colorSonificator;
    private ModalModel resonantSurfaceModes;
    private ModalObjectWithOneContact resonantSurface;
    private boolean haveSy = true; // use sy file or not
    //private ConstantLoopBuffer excitation;
    private RandOut excitation;

    public GrabSegment(int bufferSize, float srate, SegmentDataProvider sdp,String syfile) {
        super(bufferSize);
        this.segmentDataProvider = sdp;
        this.srate = srate;
        this.syfile=syfile;
        createPatch();
    }

    private void createPatch() {
        colorSonificator = new LowpassColorSonificator(srate,bufferSize);
        excitation = new RandOut(bufferSize);

        try {
            resonantSurfaceModes = new ModalModel(syfile);
            haveSy = true;
        } catch(Exception e) {
            haveSy = false;
            // System.out.println("Can't load sy file "+e);
        }
        if(haveSy) {
            resonantSurface = new ModalObjectWithOneContact(resonantSurfaceModes,srate,bufferSize);
        }
        try {
            colorSonificator.addSource(excitation);
            if(haveSy) {
                resonantSurface.addSource(colorSonificator);
            }
        } catch(Exception e) {
        }
    }

    private float[] pixel2hsb(int pixel) {
        int red   = (pixel >> 16) & 0xff;
        int green = (pixel >>  8) & 0xff;
        int blue  = (pixel      ) & 0xff;
        java.awt.Color.RGBtoHSB(red,green,blue,hsb);
        return hsb;
    }

    protected void computeBuffer() {
        int bufsz = getBufferSize();
        int[] mousepos = segmentDataProvider.getMousePosition();
        int[] imagePixels = segmentDataProvider.getImagePixels();
        Dimension imageDimension = segmentDataProvider.getImageDimension();
        int current_pixel = imagePixels[mousepos[1] * imageDimension.width + mousepos[0]];
        hsb = pixel2hsb(current_pixel);
        //System.out.println("hsb=("+hsb[0]+","+hsb[1]+","+hsb[2]+")");
        colorSonificator.setHSB(hsb[0],hsb[1],hsb[2]);
        try {
            if(haveSy) {
                buf = resonantSurface.getBuffer(getTime());
            } else {
                buf = colorSonificator.getBuffer(getTime());
            }
        } catch(BufferNotAvailableException e) {
            System.out.println(this+" "+e);            
        }
    }

    
}
