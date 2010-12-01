//import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import jass.generators.*;
import jass.render.*;
import jass.engine.*;

public class Demo2 extends Thread implements MouseMotionListener, MousePositionServer {
    /** component over which mouse events are gathered */
    Component component;
    /** mouse position */
    int current_x, current_y,old_x,old_y;
    private double oldTime;
    int[] mySize;
    double mouseSamplingRate; // estimated
    int windowHeight = 200;

    Butter2LowFilter butterFilter; // filter mouse

    // JASS stuff
    int buffersize;
    boolean useNative = true;
    int bufferSizeJavaSound = 8*1024;
    int numRtAudioBuffersNative;

    float srate = 44100;
    SourcePlayer sourcePlayer;
    MousedGroove mousedGroove;

    Demo2(String[] args) {
        int horPixels = Integer.parseInt(args[0]);
        
        Frame frame = new Frame ();
        this.component = frame;
        mySize = new int[] {horPixels,windowHeight};
        frame.setSize(new Dimension(mySize[0],mySize[1]));
        frame.setResizable(false);
        // to center (workaround from GNOME bug which misplaces window)
        frame.setLocationRelativeTo(null); 

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
        buffersize = Integer.parseInt(args[1]);
        numRtAudioBuffersNative = 512/buffersize;
        mouseSamplingRate = srate/buffersize;
        butterFilter = new Butter2LowFilter((float)mouseSamplingRate);
        float cutOffFreqMouse = Float.parseFloat(args[2]);
        butterFilter.setCutoffFrequency(cutOffFreqMouse);
        initAudio(args);
    }

    public static void main (String[] args) {
        if(args.length != 4) {
            System.out.println("Usage: java Demo2 horpixels buffersize mouseFreqLowPassCutoff ../data/hello.wav");
            return;
        }
        new Demo2(args);
    }

    private void initAudio(String[] args) {

        sourcePlayer = new SourcePlayer(buffersize,bufferSizeJavaSound,srate);
        sourcePlayer.setUseNativeSound(useNative);
        sourcePlayer.setNumRtAudioBuffersNative(numRtAudioBuffersNative);

        try {
            mousedGroove = new MousedGroove(srate,buffersize,args[3]);
        } catch(Exception e) {
            System.out.println("error MouseGroove create:"+e);
        }
        mousedGroove.setMousePositionServer(this);
        mousedGroove.setXSize(mySize[0]);
        try {
            sourcePlayer.addSource(mousedGroove);
        } catch(Exception e) {}
        sourcePlayer.start();
        float[] audioBuffer = mousedGroove.getGrooveBuffer();
        Graphics g = component.getGraphics();
        g.setColor(Color.red);
        int windSize = 128; // average audio
        float maxval = 0; // max square of audio
        float val = 0;
        if(audioBuffer != null) { // is null for streaming audio file
            for(int i=0;i<audioBuffer.length-windSize;i++) {
                val = 0;
                for(int k=0;k<windSize;k++) {
                    val += audioBuffer[i+k] * audioBuffer[i+k];
                }
                val /= windSize;
                if(val > maxval) {
                    maxval = val;
                }
            }
            
            for(int ix=0;ix<mySize[0];ix++) {
                int bufferIndex = (int)(((audioBuffer.length - windSize)* ((double)ix ))/mySize[0]);
                val = 0;
                for(int k=0;k<windSize;k++) {
                    val += audioBuffer[bufferIndex+k] * audioBuffer[bufferIndex+k];
                }
                val /= windSize;
                int iy = (int)(.6*(val/maxval) * (mySize[1]-1));
                g.fillRect(ix,iy,1,iy);
            }
        }
    }    

    // implement MousePositionServer

    int mouseQueueSize = 2;
    float[] mousePos = new float[mouseQueueSize];
    Rectangle rect = null;

    public double getMouseXPosition() {
        mousePos[0] = (float)old_x;
        mousePos[1] = (float)current_x;
        double oldx = mousePos[1];
        int offset = 0;
        butterFilter.filter(mousePos,mousePos,mouseQueueSize,offset);
        //System.out.println(oldx+" "+mousePos[1]);
        Graphics g = component.getGraphics();
        int thickness = 1;
        g.setXORMode(Color.white);
        if(rect != null) {
            g.fillOval(rect.x,rect.y,rect.width,rect.height);
        } else {
            rect = new Rectangle();
        }
        rect.x = (int)mousePos[1]-thickness/2;
        rect.y = 0;
        rect.width = thickness;
        rect.height= mySize[1];
        g.fillOval(rect.x,rect.y,rect.width,rect.height);
        return mousePos[1];
    }

    ///implement MouseMotionListener

    public void mouseMoved(MouseEvent e) {
        old_x = current_x;
        old_y = current_y;
        current_x = e.getX();
        current_y = e.getY();
        //System.out.println("p1: ("+dt+":"+dx_cm+")=("+old_red+","+current_red+")");
        //System.out.println("(t,v)= ("+time+","+vel+")");
    }
    
    public void mouseDragged(MouseEvent e) {}

}
