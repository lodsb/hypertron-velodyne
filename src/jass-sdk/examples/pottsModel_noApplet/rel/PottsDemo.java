import jass.render.*;
import jass.engine.*;
import jass.patches.*;
import jass.generators.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

/**
 */

public class PottsDemo extends Thread {

    //PottsDemo.Draw draw;
    
    public static void main (String args[]) throws Exception {
        float srate = 11025.f;
        int bufferSize = 64;
        int bufferSizeJavaSound = 1024*1;
        int nchannels = 2;
        int N; //grid
        if(args.length != 3) {
            System.out.println("Usage: java PottsDemo clap11025.wav q N");
            return;
        }
        N = Integer.parseInt(args[2]);
        final SourcePlayer player;
        player = new SourcePlayer(bufferSize*nchannels,bufferSizeJavaSound,srate);
        player.setNChannels(nchannels);

        //final RandOut source = new RandOut(bufferSize);
        String audioFile = args[0];
        int nLoopers = N*N;
        final LoopBuffer[] sources = new LoopBuffer[nLoopers];
        final Mixer mixer = new Mixer(bufferSize*nchannels,nLoopers,nchannels);
        sources[0] = new LoopBuffer(srate,bufferSize,audioFile);
        mixer.addSource(sources[0]);
        float[] loopBuf = sources[0].getLoopBuffer();
        for(int i=1;i<nLoopers;i++) {
            sources[i] = new LoopBuffer(srate,bufferSize,loopBuf);
            mixer.addSource(sources[i]);
        }
        
        for(int i=0;i<nLoopers;i++) {
            double r = Math.random();
            sources[i].setSpeed((float)(.8+r/4));
            double pan = r;
            mixer.setPan(i,(float)pan);
            mixer.setGain(i,(float)1);
        }        int nReflections = 6;
        final CombReverb reverb = new CombReverb(bufferSize*nchannels,srate,nReflections,nchannels);
        reverb.addSource(mixer);
        reverb.setDryToWet((float).7);
        player.addSource(reverb);
        double temperature = .1;
        double bias = 0;
        double rev = .7;
        double q = Double.parseDouble(args[1]);
        Draw draw = new Draw(400,400,(int)Math.round(q),N);
                

        final PottsModel pottsModel = new PottsModel(N,q);
        pottsModel.setT(temperature);
        // Add control panel

        String[] names = {"T ","damp ","reverb "};
        double[] val =   {temperature,bias,rev};
        double[] min =   {0.01,0,0};
        double[] max =   {5,10,1};
        int nbuttons = 1;
        Controller a_controlPanel = new Controller(new java.awt.Frame ("PottsDemo"),
                                                   false,val.length,nbuttons) {

                public void onButton(int k) {
                    switch(k) {
                    case 0:
                        pottsModel.resetRunningAverage();
                        System.out.println("reset M");
                        break;
                    } 
                }
            
                public void onSlider(int k) {
                    switch(k) {
                    case 0:
                        pottsModel.setT(this.val[k]);
                        break;
                    case 1:
                        pottsModel.setBias(this.val[k]);
                        break;
                    case 2:
                        reverb.setDryToWet(1-(float)this.val[k]);
                        break;
                    }
                }
            
            };

        a_controlPanel.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.out.println("Close handler called");
                    player.stopPlaying();
                    try{
                        sleep(500);
                    } catch(Exception e3) {
                    }
                    System.exit(0);
                }
            });
	
        a_controlPanel.setSliders(val,min,max,names);
        a_controlPanel.setButtonNames (new String[] {"Reset"});
        a_controlPanel.setVisible(true);

        player.start();

        for(int k=0;k<N*N;k++) {
            mixer.setGain(k,1);
        }
        try {
            Thread.sleep(1000);
        } catch(Exception e) {}


        while(true) {
            int sleepT=200;
            pottsModel.sweep();
            int n = pottsModel.getN();
            double [][]s = pottsModel.getState();
            int k=0;
            for(int i=0;i<n;i++) {
                for(int j=0;j<n;j++) {
                    float gain = (float)(1+s[i][j]);
                    mixer.setGain(k,gain);
                    double pan = ((double)i)/N;
                    mixer.setPan(k,(float)pan);
                    k++;
                    draw.displayPottsState(s[i][j],i,j);
                }
            }
            draw.show();

            try {
                Thread.sleep(sleepT);
                draw.setTitle("M="+pottsModel.getMagnetization());
                //System.out.println(pottsModel.getMagnetization() );
            } catch(Exception e) {}
        }


    }

}


class Draw {
    private int q;
    private int N;
    private int width;
    private int height;
    // default colors
    public final Color DEFAULT_PEN_COLOR   = Color.BLACK;
    public final Color DEFAULT_CLEAR_COLOR = Color.WHITE;
    // show we draw immediately or wait until next show?
    private boolean defer = false;
    // current pen color
    private Color penColor;
    // boundary of drawing canvas, 5% border
    private final double BORDER = 0.05;
    private final double DEFAULT_XMIN = 0.0;
    private final double DEFAULT_XMAX = 1.0;
    private final double DEFAULT_YMIN = 0.0;
    private final double DEFAULT_YMAX = 1.0;
    private double xmin, ymin, xmax, ymax;
    // default font
    private final Font DEFAULT_FONT = new Font("Serif", Font.PLAIN, 16);
    // current font
    private Font font;
    // double buffered graphics
    private final BufferedImage offscreenImage, onscreenImage;
    private final Graphics2D offscreen, onscreen;
    // the frame for drawing to the screen
    private JFrame frame = new JFrame();
    // the label
    private JLabel draw;

    Color[] colors;
        
    // write the given string in the current font
    public void setFont() { setFont(DEFAULT_FONT); }
    public void setFont(Font f) { font = f; }

    // change the user coordinate system
    public void setXscale() { setXscale(DEFAULT_XMIN, DEFAULT_XMAX); }
    public void setYscale() { setYscale(DEFAULT_YMIN, DEFAULT_YMAX); }
        
    public void setXscale(double min, double max) {
        double size = max - min;
        xmin = min - BORDER * size;
        xmax = max + BORDER * size;
    }
        
    public void setYscale(double min, double max) {
        double size = max - min;
        ymin = min - BORDER * size;
        ymax = max + BORDER * size;
    }
    
    // helper functions that scale from user coordinates to screen coordinates and back
    private double scaleX (double x) { return width  * (x - xmin) / (xmax - xmin); }
    private double scaleY (double y) { return height * (ymax - y) / (ymax - ymin); }
    private double factorX(double w) { return w * width  / Math.abs(xmax - xmin);  }
    private double factorY(double h) { return h * height / Math.abs(ymax - ymin);  }
    private double userX  (double x) { return xmin + x * (xmax - xmin) / width;    }
    private double userY  (double y) { return ymax - y * (ymax - ymin) / height;   }

    // draw one pixel at (x, y)
    private void pixel(double x, double y) {
        offscreen.fillRect((int) Math.round(scaleX(x)), (int) Math.round(scaleY(y)), 1, 1);
    }
    
    public void clear() { clear(DEFAULT_CLEAR_COLOR); }
    public void clear(Color color) {
        offscreen.setColor(color);
        offscreen.fillRect(0, 0, width, height);
        offscreen.setColor(penColor);
        show();
    }
    
    // display on screen and pause for t miliseconds
    public void show(int t) {
        defer = true;
        onscreen.drawImage(offscreenImage, 0, 0, null);
        frame.repaint();
        try { Thread.currentThread().sleep(t); }
        catch (InterruptedException e) { System.out.println("Error sleeping"); }
    }
    
    // view on-screen, creating new frame if necessary
    public void show() {
        if (!defer) onscreen.drawImage(offscreenImage, 0, 0, null);
        if (!defer) frame.repaint();
    }
    
    // set the pen color
    public void setPenColor() { setPenColor(DEFAULT_PEN_COLOR); }
    public void setPenColor(Color color) {
        penColor = color;
        offscreen.setColor(penColor);
    }

    // draw squared of side length 2r, centered on (x, y); degenerate to pixel if small
    public void filledSquare(double x, double y, double r) {
        // screen coordinates
        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*r);
        double hs = factorY(2*r);
        if (ws <= 1 && hs <= 1) pixel(x, y);
        else offscreen.fill(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
    }
    
    public void displayPottsState(double s,int i,int j) {
        int si =  (int)Math.round(q*s);
        double r = .5/N;
        double x = ((double)i)/N +r;
        double y = ((double)j)/N +r;
        int cind = si+q;
        setPenColor(colors[cind]);
        //System.out.println("s="+s+" q="+q);
        filledSquare(x,y,r);
    }

    public void setTitle(String s) {
        frame.setTitle(s);
    }
    
    public Draw(int width, int height,int q,int N) {
        this.q=q;
        this.N=N;
        this.width  = width;
        this.height = height;
        if (width <= 0 || height <= 0) throw new RuntimeException("Illegal dimension");
        colors = new Color[2*q+1];
        int i=0;
        for(int k=-q;k<=q;k++) {
            float g = (float)(1.-i/(2.*q));
            colors[i] = new Color(g,g,g);
            i++;
        }
        offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        onscreenImage  = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        offscreen = offscreenImage.createGraphics();
        onscreen  = onscreenImage.createGraphics();
        setXscale();
        setYscale();
        offscreen.setColor(DEFAULT_CLEAR_COLOR);
        offscreen.fillRect(0, 0, width, height);
        //            setPenColor();
        //setPenRadius();
        setFont();

        // add antialiasing
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);

        // the drawing panel
        ImageIcon icon = new ImageIcon(onscreenImage);
        draw = new JLabel(icon);
        //draw.addMouseListener(this);
        //draw.addMouseMotionListener(this);


        frame.setContentPane(draw);

        // label cannot get keyboard focus
        //frame.addKeyListener(this);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            // closes all windows
        // frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);      // closes only current window
        frame.setTitle("PottsDemo");
        //frame.setJMenuBar(createMenuBar());
        frame.pack();
        frame.setVisible(true);

        clear();
    }
        

}
