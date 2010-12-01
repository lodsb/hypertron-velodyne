import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.net.*;

import jass.generators.*;
import jass.render.*;
import jass.engine.*;

public class DemoMousePictureExplore extends Thread implements  MouseMotionListener,  MouseListener, SegmentDataProvider {
    /** component over which mouse events are gathered */
    Component component;
    /** mouse position */
    int current_x, current_y,old_x,old_y;
    private double oldTime=0;
    private double oldTimeEvent=0;
    Image myImage = null;
    String myImageName = "tmp.jpg";
//    URL myImageName;
    Dimension myImageSize = new Dimension();
    int [] imagePixels; // pixel map
    double pixelsPerCm;
    // JASS stuff
    SourcePlayer sourcePlayer;
    GrabSegment grabSegment;
    GrabSegmentScrape grabSegmentScrape;
    int buffersize = 64;
    int nBuffersRtAudio = 512/buffersize;
    int jsBuffersize = 10*1024;
    float srate = 44100;
    boolean nativeSound = false;
    boolean scrapeMode = true;
    boolean useControlPanel = false;

    DemoMousePictureExplore(String[] args) {
        //      try {1
        //  myImageName = new URL(args[0]);
        //} catch(Exception e) {
        //   System.out.println("Error loading URL "+e);
        //}
        myImageName = args[0];
        pixelsPerCm = Double.parseDouble(args[1]);
        int nrt = Integer.parseInt(args[3]);
        if(nrt == -1) {
            nativeSound = false;
        } else {
            // nativeSound = true;
             nativeSound = false;
            nBuffersRtAudio = nrt/buffersize;
        }
        
        Frame frame = new Frame () {
            public void paint(Graphics g) {
                g.drawImage(myImage,0,0,component);
            }
	    };
        this.component = frame;
        myImage = Toolkit.getDefaultToolkit().getImage(myImageName);
        getPixels();

        frame.setSize(myImageSize);
        frame.setResizable(false);
        //frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("Close handler called");
                sourcePlayer.stopPlaying();
                try{
                    sleep(500);
                } catch(Exception e3) {
                }
                System.exit(0);
            }
	    });
        
        component.addMouseMotionListener(this);
        component.addMouseListener(this);

        initAudio(args);
    }

    public static void main (String[] args) {
        if(args.length != 4) {
            System.out.println("Usage: java DemoMousePictureExplore image.jpg pix_per_cm ../data/stick.sy rtbuffersize (-1 for javasound)");
            return;
        }
        new DemoMousePictureExplore(args);
    }

    private void initAudio(String[] args) {
        if(!nativeSound) {
            sourcePlayer = new SourcePlayer(buffersize,jsBuffersize,srate);
            sourcePlayer.setUseNativeSound(false);
        } else {
            sourcePlayer = new SourcePlayer(buffersize,nBuffersRtAudio,srate);
            sourcePlayer.setUseNativeSound(true);
            sourcePlayer.setNumRtAudioBuffersNative(nBuffersRtAudio);
        }
        grabSegmentScrape = new GrabSegmentScrape(buffersize,srate,this,args[2],(float)pixelsPerCm);
        try {
            sourcePlayer.addSource(grabSegmentScrape);
        } catch(Exception e) {}

        addControlPanel();
   
        sourcePlayer.AGCOff();
        sourcePlayer.start();
    }    

    private void addControlPanel() {
        // Add control panel
	
        String[] sliderNames =          {"vmax ", "mouseF ", "loglevel "};
        final double[] sliderValues =   {.05,       2,         3};
        double[] minSliderValues =      {.005,     .3,        2};
        double[] maxSliderValues =      {.1,       20,        10};
        int nbuttons = 4;
        
        Controller a_controlPanel = new Controller(new java.awt.Frame ("TestColorSonificator"),false,sliderValues.length,nbuttons) {
		
            public void onButton(int k) {
                switch(k) {
                    case 0:
                    sourcePlayer.resetAGC();
                    break;
                    case 1: {
                        FileDialog fd = new FileDialog(new Frame(),"Save");
                        fd.setMode(FileDialog.SAVE);
                        fd.setVisible(true);
                        saveToFile(fd.getFile());
                    }
                    break;
                    case 2: {
                        FileDialog fd = new FileDialog(new Frame(),"Load");
                        fd.setMode(FileDialog.LOAD);
                        fd.setVisible(true);
                        loadFromFile(fd.getFile());
                        break;
                    }
                    case 3:
                    System.out.println("maxsignal = "+sourcePlayer.getAGC());
                    break;
                } 
            }
            
            public void onSlider(int k) {
                sliderValues[k]=this.val[k];
                switch(k) {
                    case 0:
                    grabSegmentScrape.setUnitScrapeVelocity((float)sliderValues[k]);
                    break;
                    case 1:
                    grabSegmentScrape.setLowPassMouseMotionFilterFrequencyCutoff((float)sliderValues[k]);
                    break;
                    case 2:
                    float level = (float)Math.pow(10,sliderValues[k]);
                    sourcePlayer.setAGC(level);
                    break;
                }
            }
		
	    };
        
        a_controlPanel.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("Close handler called");
                sourcePlayer.stopPlaying();
                try{
                    sleep(500);
                } catch(Exception e3) {
                }
                System.exit(0);
            }
	    });
	
        a_controlPanel.setSliders(sliderValues,minSliderValues,maxSliderValues,sliderNames);
        a_controlPanel.setButtonNames (new String[] {"Reset","Save","Load","Show Level"});

        if(useControlPanel) {
            a_controlPanel.setVisible(true);
        }
        for(int i=0;i<sliderNames.length;i++) {
            a_controlPanel.onSlider(i);
        }

    }

    // implement SegmentDataProvider
    private int[] pos = {0,0};
    private boolean mouseButtonIsDown = false;

    /**
       @return int[] array of image pixels
    */
    public int[] getImagePixels() {
        return imagePixels;
    }
    
    /**
       @return if mouse button is down
    */
    public boolean isMousePressed() {
        return mouseButtonIsDown;
    }

    /**
       @return Dimension of image in pixels
    */
    public Dimension getImageDimension() {
        return myImageSize;
    }
    
    public int[] getMousePosition() {
        pos[0] = this.current_x;
        pos[1] = this.current_y;
        return pos;
    }
    
    ///implement MouseMotionListener
    
    public void mouseMoved(MouseEvent e) {
        current_x = e.getX();
        current_y = e.getY();
    }

    // implement MouseListener
    
    public void mouseClicked(MouseEvent e) {}
    
    public void mouseDragged(MouseEvent e) {
        current_x = e.getX();
        current_y = e.getY();
    }
    
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    
    public void mousePressed(MouseEvent e) {
        mouseButtonIsDown = true;
    }
    
    public void mouseReleased(MouseEvent e) {
        mouseButtonIsDown = false;
    }

    private void getPixels() {
        do {
            myImageSize.width = myImage.getWidth(this.component);
            myImageSize.height = myImage.getHeight(this.component);
            try {
                sleep(100);
            } catch(Exception e){}
        } while(myImageSize.width<0 ||  myImageSize.height<0);
        System.out.println("imagesize= "+myImageSize);
        imagePixels = new int[myImageSize.width * myImageSize.height];
        PixelGrabber pg = new PixelGrabber(myImage, 0, 0, myImageSize.width, myImageSize.height, imagePixels, 0, myImageSize.width);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.out.println("interrupted waiting for pixels!");
        }
    }
    
}
