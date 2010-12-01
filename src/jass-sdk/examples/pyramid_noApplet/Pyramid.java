import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import jass.generators.*;
import jass.render.*;
import jass.engine.*;

/**
   Divide window  into four triangles defined  by the 4  corners and the
   middle.  Assign modal  models to  the  vertices in  the order  center
   topleft bottomleft  bottomright topright, and  interpolate the models
   in  each   triangle  using  barycentric   coordinates.   Labeling  of
   triangles and vertices is as follows: u[0],u[1] (x,y) are in range [0
   - 1]
   
   x -->

y 1 _____________ 4
|  |\           /|
|  |  \   0   /  |
V  |    \   /    |
   | 1    0   3  |
   |    /   \    |
   |  /   2   \  |
   |/___________\|
  2               3

  Modal models are assigned on commandline (see makefile for "make run").
  Input is provided through audio-in, a contact mike as a virtual drumstick
  is effective.

*/

public class Pyramid implements MouseListener, MouseMotionListener, ComponentListener {
    public static final int DIM = 2;

    /** component over which mouse events are gathered */
    Component component;

    // scaled mouse position
    float[] uCurrent = new float[] {0,0};

    // scaled mouse click position
    float[] uClick = new float[] {0,0}; 
  
    /** dimension of component, in pixels. Changed if component is resized */
    Dimension size; 

    /** Construct input source, which produces mouse (x,y) positions
     * from dragging on <code>component</code>. 
     *
     * @param component Active component on which to drag
     */
    Pyramid(Component component) {
        this.component = component;
        size = component.getSize();
        drawDisplay();
        start();
    }

    /** Initialize resources and start capture.
        Must call {@link #read} at <code>inputRate</code> to consume
        input data.
        @return true if successfully started.
    */
    public boolean start() {
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
        component.addComponentListener(this);
        return true;
    }

    /** Stop collecting data and free resources */
    public void stop() {
        component.removeMouseListener(this);
        component.removeComponentListener(this);
    }

    /** read next input. A client of this class should arrange to
        read the data out at the inputRate. 
        Returns the latest mouse position.  */
    public void read(float[] u,int x, int y) {
        u[0]  = (float) x / size.width ;
        u[1]  = (float) y / size.height ;
    }

    /** return input dimension */
    public int getDimension() {
        return DIM;
    }

    private void drawDisplay() {
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

    // implement MouseMotionListener
 
    public void mouseDragged(MouseEvent e) {
    }
    
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        read(uCurrent,x,y);
        //System.out.println(uCurrent[0]+","+uCurrent[1]);
        mp.setPoint(uCurrent[0],uCurrent[1]);
    }
    
    // implement MouseListener

    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        read(uClick,x,y);
        //System.out.println("("+uClick[0]+","+uClick[1]+")");
        mp.setPoint(uClick[0],uClick[1]);
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
    }
    
    public void mouseReleased(MouseEvent e) {
    }

    
    static SourcePlayer player;
    static final int nvertices = 5; // must be 5
    static ModalPyramid mp;
    static OneShotBuffer force; // excitation
    static AudioIn audioIn; // alternate excitation
    
    public static void main (String[] args) {
        float srate = 44100;
        int bufferSize = 128;
        int bufferSizeJavaSound = 8*1024;
        if(args.length != 5) {
            System.out.println("Usage: java Pyramid 1.sy 2.sy 3.sy 4.sy 5.sy");
            return;
        }
        player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        ModalModel[] mm = new ModalModel[nvertices]; // a model for each vertex
        try {
            for(int i=0;i<nvertices;i++) {
                mm[i] = new ModalModel(args[i]);
            }
        } catch (java.io.FileNotFoundException ee) {
            System.out.println("A modes file was not found\n");
        }
        mp = new ModalPyramid(mm,srate,bufferSize);
        audioIn = new AudioIn(srate,bufferSize,0);
        try {
            player.addSource(mp);
            mp.addSource(audioIn);
        } catch(SinkIsFullException ee) {
            System.out.println(ee);
            System.exit(0);
        }
        player.start();

        
        //Create the top-level container and add viewer
        JFrame frame = new JFrame() {
            public void paint(Graphics g) {
                int w = getWidth();
                int h = getHeight();
                g.clearRect(0,0,w,h);
                g.drawLine(0,0,w,h);
                g.drawLine(w,0,0,h);
            }
        };
        //Finish setting up the frame, and show it.
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
	    });
        frame.setSize(new Dimension(400,400));
        frame.setVisible(true);        
        Pyramid mi = new Pyramid(frame);
    }
}

class ModalPyramid extends ModalObjectWithOneContact {
    int maxModes = 1000000;
    int nvertices = 5;
    ModalModel mmCurrent;
    ModalModel[] mmT = new ModalModel[3]; // at vertices of triangle
    ModalModel[] allmm; // one for each vertex
    
    public ModalPyramid(ModalModel[] mm,float srate,int bufferSize) {
        super(mm[0],srate,bufferSize);
        allmm = mm;
        nvertices = mm.length;
        for(int i=0;i<nvertices;i++) {
            if(mm[i].nfUsed < maxModes) {
                maxModes = mm[i].nfUsed;
            }
        }
        System.out.println("maxModes="+maxModes);
        // set number of modes to smalles occurring in all models
        setNf(maxModes);
        // create a modalmodel which will be recomputed all the time in setPoint
        mmCurrent = new ModalModel(maxModes,1); // one location
        
        this.modalModel = mmCurrent;
    }

    // tmap[t][k] is kth vertex of triangle t
    int[][] tmap = {
        {0,4,1},
        {0,1,2},
        {0,2,3},
        {0,3,4}
    };

    // q[v][]  is (x,y) vector of vth vertex
    float[][] q = {
        {.5f,.5f},
        {0,0},
        {0,1},
        {1,1},
        {1,0}
    };

    // compute from (x,y) in [0 1]X[0 1] the triangle and
    // the barycentric coordinates of (x,y) in that triangle
    public void setPoint(float x,float y) {
        int t; // triangle index
        float foo = x+y;
        if(x > y) { // is 0 or 3
            if(foo > 1) {
                t = 3;
            } else {
                t = 0;
            }
        } else { // is 1 or 2
            if(foo > 1) {
                t = 2;
            } else {
                t = 1;
            }
        }
        // have triangle t. Compute barycentric coordinates
        // call (x,y) p. Let the 3 triangle vertices be q1 q2 q3,
        // and the barycentric coordinates b1 b2 b3. p,q 2-vectors,
        // b are scalars. 2 equations for b are:
        // (q1-q3)b1+(q2-q3)b2 = p-q3
        // b3 = 1-b1-b2
        // define:
        // r1 = q1-q3,
        // r2=q2-q3,
        // r3=p-q3,
        // rr = (r1.r2)^2 - (r1.r1)*(r2.r2)
        // then
        // b1 = ((r1.r2)*r2-(r2.r2)*r1).r3 /rr
        // b2 = ((r1.r2)*r1-(r1.r1)*r2).r3 /rr
        // or, defining
        // w1 = (r1.r2)*r2-(r2.r2)*r1)/rr
        // w2 = (r1.r2)*r1-(r1.r1)*r2)/rr
        // b1 = w1.r3 
        // b2 = w2.r3

        // init r1 r2 r3 as q1 q2 q3
        float[] r1 = {q[tmap[t][0]][0],q[tmap[t][0]][1]};
        float[] r2 = {q[tmap[t][1]][0],q[tmap[t][1]][1]};
        float[] r3 = {q[tmap[t][2]][0],q[tmap[t][2]][1]};
        /*
        System.out.println("q1= "+r1[0] +" "+r1[1]);
        System.out.println("q2= "+r2[0] +" "+r2[1]);
        System.out.println("q3= "+r3[0] +" "+r3[1]);        
        */
        r1[0] = r1[0] - r3[0];
        r1[1] = r1[1] - r3[1];
        r2[0] = r2[0] - r3[0];
        r2[1] = r2[1] - r3[1];
        r3[0] = x - r3[0];
        r3[1] = y - r3[1];
        // got r's now
        // inner products
        float r11 = r1[0]*r1[0] + r1[1]*r1[1];
        float r22 = r2[0]*r2[0] + r2[1]*r2[1];
        float r12 = r1[0]*r2[0] + r1[1]*r2[1];
        float rr = r12*r12 - r11*r22;
        float[] w1 = {(r12*r2[0]-r22*r1[0])/rr,(r12*r2[1]-r22*r1[1])/rr};
        float[] w2 = {(r12*r1[0]-r11*r2[0])/rr,(r12*r1[1]-r11*r2[1])/rr};
        float[] b = {
            w1[0]*r3[0] + w1[1]*r3[1],
            w2[0]*r3[0] + w2[1]*r3[1],
            0
        };
        b[2] = 1 - b[0] - b[1];
        // got barycentric coordinates.
        // now load mmT with the correct 3 models corresponding to the vertices
        mmT[0] = allmm[tmap[t][0]];
        mmT[1] = allmm[tmap[t][1]];
        mmT[2] = allmm[tmap[t][2]];
        setModel(b);
        //System.out.println("t="+t+ " b= "+b[0] +" "+b[1]+" "+b[2]);        
    }

    private void setModel(float[] b) {
        for(int i=0;i< maxModes;i++) {
            mmCurrent.f[i] = (float)(b[0]*mmT[0].f[i]+b[1]*mmT[1].f[i]+b[2]*mmT[2].f[i]);
            mmCurrent.d[i] = (float)(b[0]*mmT[0].d[i]+b[1]*mmT[1].d[i]+b[2]*mmT[2].d[i]);
            mmCurrent.a[0][i] = (float)(b[0]*mmT[0].a[0][i]+b[1]*mmT[1].a[0][i]+b[2]*mmT[2].a[0][i]);
        }
        this.computeFilter();
    }
}


