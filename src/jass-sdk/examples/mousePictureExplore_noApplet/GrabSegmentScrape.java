import java.awt.*;
import jass.engine.*;
import jass.generators.*;
import jass.render.*;
import jass.patches.*;

/**
   Grab a line segment based on current and previous mouse position and make sound based on that
   @author Kees van den Doel (kvdoel@cs.ubc.ca)
*/

public class GrabSegmentScrape extends Out {
    private SegmentDataProvider segmentDataProvider;
    private float[] hsb = new float[3];
    private float[] tmprgb = new float[3];
    private float[] rgb = new float[3];
    private float[] lch = new float[3];
    protected float srate;
    private String syfile;
    private String wavfile;
    private LowpassColorSonificator colorSonificator;
    private ModalModel resonantSurfaceModes;
    private ModalObjectWithOneContact resonantSurface;
    private boolean haveSy = true; // use sy file or not
    private RandOut excitation;
    private Butter2LowFilter butterFilterX; // filter mouseX
    private Butter2LowFilter butterFilterY; // filter mouseY
    private float mouseSamplingRate;
    private float cutOffFreqMouse=1; // determines smoothness/latency
    private float pixelsPerMeter;
    private float unitScrapeVelocity = .01f; // in m/s, can be 2.5 as fast
    
    public GrabSegmentScrape(int bufferSize, float srate, SegmentDataProvider sdp,String syfile,float pixelsPerCm) {
        super(bufferSize);
        this.pixelsPerMeter = 100*pixelsPerCm;
        this.segmentDataProvider = sdp;
        this.srate = srate;
        this.syfile=syfile;
        createPatch();
    }

    /** Set center speed in m/s, can be up to twice as fast
        @param v center speed
    */
    public void setUnitScrapeVelocity(float v) {
        unitScrapeVelocity = v;
    }

    /** Get center speed in m/s, can be up to twice as fast
        @return  center speed
    */
    public float getUnitScrapeVelocity() {
        return unitScrapeVelocity;
    }

    /**
       Set lowpass mouse motion freq. cutoff
       @param f lowpass cutoff for mouse in Hetrz
    */
    public void setLowPassMouseMotionFilterFrequencyCutoff(float f) {
        cutOffFreqMouse = f;
        butterFilterX.setCutoffFrequency(cutOffFreqMouse);
        butterFilterY.setCutoffFrequency(cutOffFreqMouse);

    }

    /**
       Get lowpass mouse motion freq. cutoff
       @return lowpass cutoff for mouse in Hetrz
    */
    public float getLowPassMouseMotionFilterFrequencyCutoff() {
        return cutOffFreqMouse;
    }

    private void createPatch() {
        colorSonificator = new LowpassColorSonificator(srate,bufferSize);
        excitation = new RandOut(bufferSize);
        mouseSamplingRate = srate/getBufferSize();

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
        butterFilterX = new Butter2LowFilter((float)mouseSamplingRate);
        butterFilterX.setCutoffFrequency(cutOffFreqMouse);
        butterFilterY = new Butter2LowFilter((float)mouseSamplingRate);
        butterFilterY.setCutoffFrequency(cutOffFreqMouse);
    }

    private float[] pixel2hsb(int pixel) {
        int red   = (pixel >> 16) & 0xff;
        int green = (pixel >>  8) & 0xff;
        int blue  = (pixel      ) & 0xff;
        java.awt.Color.RGBtoHSB(red,green,blue,hsb);
        return hsb;
    }

    private float[] pixel2rgb(int pixel) {
        int red   = (pixel >> 16) & 0xff;
        int green = (pixel >>  8) & 0xff;
        int blue  = (pixel      ) & 0xff;
        tmprgb[0] = red;
        tmprgb[1] = green;
        tmprgb[2] = blue;
        return tmprgb;
    }

    private int[] mousepos = new int[2];
    private Dimension imageDimension;
    private int bufsz;
    private int[] imagePixels;
    private int current_pixel;
    private float[] mouseX = new float[2];
    private float[] mouseY = new float[2];
    private int mouseQueueSize = 2;
    
    protected void computeBuffer() {
        bufsz = getBufferSize();
        mousepos = segmentDataProvider.getMousePosition();
        mouseX[0] = mouseX[1];
        mouseY[0] = mouseY[1];
        mouseX[1] = mousepos[0]/pixelsPerMeter;
        mouseY[1] = mousepos[1]/pixelsPerMeter;
        int offset = 0;
        butterFilterX.filter(mouseX,mouseX,mouseQueueSize,offset);
        butterFilterY.filter(mouseY,mouseY,mouseQueueSize,offset);
        mousepos[0] = (int)(pixelsPerMeter*mouseX[1]);
        mousepos[1] = (int)(pixelsPerMeter*mouseY[1]);
        float vx = (mouseX[1]-mouseX[0])*mouseSamplingRate;
        float vy = (mouseY[1]-mouseY[0])*mouseSamplingRate;
        float vSlide2 = vx*vx+vy*vy;
        float vSlide = (float)Math.sqrt(vSlide2);
        
        imagePixels = segmentDataProvider.getImagePixels();
        imageDimension = segmentDataProvider.getImageDimension();
        // if changing filter at run time mousepos[] may overflow
        // remove  pixels from sides so can average over 9 pixels
        final int npixelsaround = 3;
        final int npixels = (1 + 2*npixelsaround)*(1 + 2*npixelsaround);
        if(mousepos[0]>=imageDimension.width-npixelsaround) {
            mousepos[0]=imageDimension.width-npixelsaround-1;
        } else if(mousepos[0]<npixelsaround) {
            mousepos[0] = npixelsaround;
        }
        if(mousepos[1]>=imageDimension.height-npixelsaround) {
            mousepos[1]=imageDimension.height-npixelsaround-1;
        } else if(mousepos[1]<npixelsaround) {
            mousepos[1] = npixelsaround;
        }
        
        rgb[0] = rgb[1] = rgb[2] = 0;

        for(int x = mousepos[0]-npixelsaround;x<=mousepos[0]+npixelsaround;x++) {
            for(int y = mousepos[1]-npixelsaround;y<=mousepos[1]+npixelsaround;y++) {
                current_pixel = imagePixels[y * imageDimension.width + x];
                tmprgb = pixel2rgb(current_pixel);
                rgb[0] += tmprgb[0];
                rgb[1] += tmprgb[1];
                rgb[2] += tmprgb[2];
            }
        }
        rgb[0] /= npixels;
        rgb[1] /= npixels;
        rgb[2] /= npixels;
        float v = vSlide/unitScrapeVelocity;

        if(segmentDataProvider.isMousePressed()) {
            v = 1;
        }

        java.awt.Color.RGBtoHSB((int)rgb[0],(int)rgb[1],(int)rgb[2],hsb);
        float pitch = (float)jass.utils.PitchMap.hue2pitch((double)hsb[0]);
        colorSonificator.setHSB_V(pitch,hsb[1],hsb[2],v);
        /* 
        // get lch luminance chromaticity and hue from L*a*b* space
        rgb[0] /= 255;
        rgb[1] /= 255;
        rgb[2] /= 255;
        lch = jass.utils.Color.Rgb2lch(rgb);
        colorSonificator.setHSB_V(lch[2],lch[1],lch[0],v);
        //System.out.println("hsb=("+hsb[0]+","+hsb[1]+","+hsb[2]+")");
        */
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
