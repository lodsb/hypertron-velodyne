import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class BellApplet extends AppletController {

    SourcePlayer player;
    ModalModel mm,mmOrg;
    ModalObjectWithOneContact bell;
    OneShotBuffer force;
    double hitFreq = .333;
    BellToller bellToller;
    float[] w; // warp direction
    String syfile = "../data/bell4.sy";
    
    public void setNSliders() {
        nsliders = 5;
    }

    public void setNButtons() {
        nbuttons = 2;
    }
    
    void randWarp() {
        for(int i=0;i<w.length;i++) {
            w[i] = (float)(2*Math.random()-1);
        }
    }
    
    public void warp(double x) {
        int nf = w.length;
        for(int i=0;i<nf;i++) {
            mm.f[i] = (float)(mmOrg.f[i]*(1 + w[i]*x ));
        }
    }

    public void init() {
        super.init();
        syfile = getParameter("syfile");
    }
    
    public void start() {
        float srate = 44100.f;
        int bufferSize = 128;
        int bufferSizeJavaSound = 8*1024;

        player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        URL codebase = getCodeBase();
        URL syurl = null;
        try {
            syurl = new URL(codebase,syfile);
        } catch(MalformedURLException e) {
            System.out.println(e+" Malformed URL: " +codebase+" "+ syfile);
        }
        try {
            mm = new ModalModel(syurl); // warp this one
            mmOrg = new ModalModel(syurl); // don't change this one
        } catch(IOException e) {
            System.out.println(e);
        }
        w = new float[mm.f.length];
        bell = new ModalObjectWithOneContact(mm,srate,bufferSize);
        float dur = .002f; // 2 ms
        int nsamples = (int)(srate * dur);
        float[] cosForce = new float[nsamples];
        for(int i=0;i<nsamples;i++) {
            cosForce[i] = (float)(.5*(1.-Math.cos(2*Math.PI*(i+1)/(1+nsamples))));
        }
        force = new OneShotBuffer(srate,bufferSize,cosForce);
        try {
            bell.addSource(force);
            player.addSource(bell);
        } catch(SinkIsFullException e) {
            System.out.println(this+ " " + e);
        }
        int nModes0 = 30;
        bell.setNf(nModes0);
        float hardNess0 = 10f;
        force.setSpeed(hardNess0);
        force.setVolume(hardNess0);
        
        String[] names = {"Hardness   ",
                          "No. modes  ",
                          "Bell freq. ",
                          "Toll speed ",
                          "Warp modes "
        };
        double[] val =   {hardNess0,
                          nModes0,
                          1,
                          hitFreq,
                          0
        };
        double[] min =   {0.1,
                          1,
                          .25,
                          .1,
                          -.1
        };
        double[] max =   {hardNess0,
                          mm.f.length,
                          3,
                          1,
                          .1
        };
        setValues(val,min,max,names);
        jButton[0].setText ("Stop");
        jButton[1].setText ("Generate Warp Direction");
        
        player.start();
        bellToller = new BellToller();
        bellToller.hitFreq = hitFreq;
        bellToller.force = force;
        bellToller.start();
        bellToller.isRunning = true;
        randWarp();            
    }
    
    protected void onSlider(int k) {
        switch(k) {
        case 0:
            force.setSpeed((float)this.val[0]);
            force.setVolume((float)this.val[0]);
            break;
        case 1:
            int nf = (int)this.val[1];
            bell.setNf(nf);
            break;
        case 2:
            bell.setFrequencyScale((float)this.val[2]);
            break;
        case 3:
            bellToller.hitFreq = this.val[3];
            break;
        case 4:
            warp(this.val[4]);
            bell.computeFilter();
            break;
        }
    }

    protected void jButtonMousePressed (int k,java.awt.event.MouseEvent evt) {
        switch(k) {
        case 0:
            if(bellToller.isRunning) {
                jButton[0].setText("Start");
                bellToller.isRunning = false;
                bell.clearHistory();
                force.clearBuffer();
                player.resetAGC();
            } else {
                jButton[0].setText("Stop");
                bellToller.isRunning = true;
            }
            break;
        case 1:
            randWarp();
            warp(this.val[4]);
            bell.computeFilter();
            break;
        }
    }

    class BellToller extends Thread {
        public double hitFreq = .33;
        public OneShotBuffer force;
        public boolean isRunning = false;
        
        public void run() {
            try {
                while(true) {
                    if(isRunning) {
                        force.hit();
                    }
                    sleep((int)(1000/hitFreq));
                }
            } catch(InterruptedException e) {
                System.out.println(this+" "+e);
            }
        }
    }
    
}

