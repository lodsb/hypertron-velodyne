import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import jass.generators.*;
import jass.render.*;
import jass.engine.*;

/**
   Capture mouse speed, filter it to remove worst jitter, and
   use vertical and horizontal components of the mouse speed
   to drive a modal resonance model. This is a prototype of
   an inhomogeneous scratchable surface.
   I experimented with various Ramez filters and left traces of
   these experiments in the source code.
 */

public class DemoMouseScrape extends Thread
    implements MouseMotionListener, ComponentListener
{
    public static final int DIM = 2;

    /** component over which mouse events are gathered */
    Component component;

    /** current mouse position */
    int x = 0, y = 0;
  
    /** dimension of component, in pixels. Changed if component is resized */
    Dimension size; 

    /** true if input is scaled to [0,1] */
    boolean scaled =  true;

    /** Contruct input source, which produces mouse (x,y) positions
     * from dragging on <code>component</code>. 
     *
     * @param component Active component on which to drag
     * @param scaled if true, input is scaled to [0,1] in the component
     */
    DemoMouseScrape( Component component, boolean scaled) {
        this.component = component;
        this.scaled = scaled;
        size = component.getSize();
        drawDisplay();
    }

    /** Initialize resources and start capture.
        Must call {@link #read} at <code>inputRate</code> to consume
        input data.
      
        @return true if successfully started.
    */
    public boolean mystart() {
        component.addMouseMotionListener(this);
        if (scaled) {
            component.addComponentListener(this);
        }
        return true;

    }

    /** Stop collecting data and free resources
    public void stop() {
        component.removeMouseMotionListener(this);
        if (scaled) {
            component.removeComponentListener(this);
        }
    }
*/
    
    /** read next input. A client of this class should arrange to
        read the data out at the inputRate. 
        Returns the latest mouse position.  */
    public void read(float[] u) {
        if (scaled) {
            u[0]  = (float) x / size.width ;
            u[1]  = (float) y / size.height ;
        }
        else {
            u[0]  = x;
            u[1]  = y;
        }
    }

    /** return raw mouse position, even if scaled values
        are being returned by read. Note, this is the current
        value and may be different than what is returned by read */
    public void readRaw(float[] rawU) {
        rawU[0]  = x;
        rawU[1]  = y;
    }


    /** return input dimension */
    public int getDimension() {
        return DIM;
    }

    private void drawDisplay() {
        Graphics g = component.getGraphics();
        int nhor=35,nvert=35,x1,x2,y1,y2;
        int wi=size.width/nhor,hi=size.height/nvert;
        y2= size.height;
        y1=0;
        for(int i=0;i<=nhor;i++) {
            x1=x2=i*(size.width/nhor);
            g.drawLine(x1,y1,x2,y2);
        }
        //System.out.println("drawDisplay\n");
    }

    ////////////////////////////////////////
    ///Implement ComponentListener
    public void componentResized(ComponentEvent e) {
        size = component.getSize();
        drawDisplay();
    }

    public void componentHidden(ComponentEvent e) {
        drawDisplay();
    }
    
    public void componentMoved(ComponentEvent e) {
        drawDisplay();
    }
    
    public void componentShown(ComponentEvent e) {
        drawDisplay();
    }



    ////////////////////////////////////////
    ///implement MouseMotionListener

 
    /** save current mouse position */
    public void mouseDragged(MouseEvent e) {
        //x = e.getX();
        //y = e.getY();
        //System.out.println("drag\n");
        //drawDisplay();
    }

    //int lastx=0,lasty=0;
    //long lastt=0;
    //float vx,vy;
    
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();

    }


    static ModalObjectWithOneContact sob;
    static LoopNBuffers af;
    static float vvx=0;
    static float vvy=0;
    static float vvv=0;
    
    public static void main (String[] args) {

        float srate = 44100;
        int bufferSize = 32;
        int bufferSizeJavaSound = 8*1024;
        if(args.length != 4) {
            System.out.println("Usage: java DemoMouseScrape 30 ../data/stick.sy ../data/grid.wav ../data/white.wav");
            return;
        }
        
        final SourcePlayer sp = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        sp.setUseNativeSound(true);
        sp.setNumRtAudioBuffersNative(512/bufferSize);
        try {
            sob = new ModalObjectWithOneContact(new ModalModel(args[1]),srate,bufferSize);
        } catch (java.io.FileNotFoundException ee) {
            System.out.println("Modes file not found\n");
        }
        af = new LoopNBuffers(srate,bufferSize,new String[] {args[2],args[3]});
        try {
            sob.addSource(af);
            sp.addSource(sob);
        } catch(SinkIsFullException ee) {
            System.out.println(ee);
            System.exit(0);
        }
        sp.start();
        
        final int rate = Integer.parseInt(args[0]); // Hz
        
        //Create the top-level container and add viewer
        JFrame frame = new JFrame();
        
        //Finish setting up the frame, and show it.
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("Stopping SourcePlayer");
                sp.stopPlaying();
                try{
                    sleep(500);
                } catch(Exception e3) {
                }
                System.exit(0);
            }
	    });
        frame.setSize(new Dimension(400,400));
        frame.setVisible(true);
        
        final DemoMouseScrape mi = new DemoMouseScrape(frame, true);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {                
            float[] u = new float[mi.getDimension()];
            int ibegin=0,iend=0; // begin and end pointers in ubuf
            float[] xbuf;
            float[] ybuf;
            final float[] bRamez20 = {
                0.3831f,
                0.0407f,
                0.0436f,
                0.0485f,
                0.0557f,
                0.0659f,
                0.0814f,
                0.1075f,
                0.1600f,
                0.3187f,
                0,
                -0.3187f,
                -0.1600f,
                -0.1075f,
                -0.0814f,
                -0.0659f,
                -0.0557f,
                -0.0485f,
                -0.0436f,
                -0.0407f,
                -0.3831f
            };
            final float[] bRamez10 = {
                0.4033f,
                0.0877f,
                0.1108f,
                0.1627f,
                0.3205f,
                0,
                -0.3205f,
                -0.1627f,
                -0.1108f,
                -0.0877f,
                -0.4033f,
            };
            final float[] bRamez5 = {
                0.4436f,
                0.2839f,
                0.2225f,
                -0.2225f,
                -0.2839f,
                -0.4436f
            };
		
            final float[] bRamez4 = {
                0.4678f,
                0.3278f,
                0,
                -0.3278f,
                -0.4678f
            };

            final float[] bRamez3 = {
                0.5913f,
                0.2137f,
                -0.2137f,
                -0.5913f
            };

            float[] bRamez;

            void insert(float x, float y) {
                int newend = iend+1;
                if(newend>=bRamez.length) {
                    newend = 0;
                }
                if(newend == ibegin) { // was full
                    ibegin++;
                    if(ibegin>=bRamez.length) {
                        ibegin = 0;
                    }
                    iend = newend;
                } else { // was not full
                    iend = newend;
                }
                xbuf[iend] = x;
                ybuf[iend] = y;
            }

            float firDiff(float[] x) {
                int j=0;
                float ret=0;
                if(ibegin<iend) {
                    for(int i=iend;i>=ibegin;i--) {
                        ret += bRamez[j]*x[i];
                        //System.out.println(j);
                        j++;
                    }
                } else {
                    for(int i=iend;i>=0;i--) {
                        ret += bRamez[j]*x[i];
                        //System.out.println(j);
                        j++;
                    }
                    for(int i=bRamez.length-1;i>=ibegin;i--) {
                        ret += bRamez[j]*x[i];
                        //System.out.println(j);
                        j++;
                    }
                }
                return ret;
            }
                
            public void clearHist() {
                for(int i=0;i<bRamez.length;i++) {
                    xbuf[i] = ybuf[i] = 1.f;
                }
            }

            { 
                bRamez = bRamez3;
                xbuf = new float[bRamez.length];
                ybuf = new float[bRamez.length];
                clearHist();
            }
                
            public void run() {
                float[] v = {1,1};
                mi.read(u);
                insert(u[0],u[1]);
                vvx = firDiff(xbuf);
                vvy = firDiff(ybuf);
                vvv = (float)(5*Math.sqrt(vvx*vvx+vvy*vvy));
                v[0] = (float)(5*Math.abs(vvx));
                v[1] = (float)(5*Math.abs(vvy));
                double realTime = jass.render.MicroTime.getTime();
                System.out.println(realTime);
                af.setSpeed(v);
            }
	    };
        mi.mystart();
        timer.scheduleAtFixedRate(task, 0, (long) (1000.0/rate));
    }


    
}


