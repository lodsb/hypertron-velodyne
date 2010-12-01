import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class Morph2Applet extends AppletController {

    SourcePlayer player;
    ModalModel mm,mm1,mm2; // morph between the two
    ModalObjectWithOneContact mob;
    OneShotBuffer force;
    double hitFreq = 1.;
    Hitter hitter;
    String syfile1;
    String syfile2;
    float morphPar = 0; // 0 is nodel 1, 1 is model 2
    int maxModes;
    
    public void setNSliders() {
        nsliders = 5;
    }

    public void setNButtons() {
        nbuttons = 1;
    }

    //for x = 0 is syfile1, for x = 1 is syfile2
    public void morph(double x) {
        for(int i=0;i< maxModes;i++) {
            mm.f[i] = (float)((1-x)*mm1.f[i] + x*mm2.f[i]);
            mm.d[i] = (float)((1-x)*mm1.d[i] + x*mm2.d[i]);
            mm.a[0][i] = (float)((1-x)*mm1.a[0][i] + x*mm2.a[0][i]);
        }
    }

    public void init() {
        super.init();
        syfile1 = getParameter("syfile1");
        syfile2 = getParameter("syfile2");
    }
    
    public void start() {
        float srate = 44100.f;
        int bufferSize = 128;
        int bufferSizeJavaSound = 8*1024;

        player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        URL codebase = getCodeBase();
        URL syurl1 = null;
        URL syurl2 = null;
        try {
            syurl1 = new URL(codebase,syfile1);
            syurl2 = new URL(codebase,syfile2);
        } catch(MalformedURLException e) {
            System.out.println(e+" Malformed URL");
        }
        try {
            mm = new ModalModel(syurl1); // warp this one
            mm1 = new ModalModel(syurl1); // don't change this one
            mm2 = new ModalModel(syurl2); // don't change this one
        } catch(IOException e) {
            System.out.println(e);
        }
        maxModes = mm1.nfUsed;
        if(mm2.nfUsed < maxModes) {
            maxModes = mm2.nfUsed;
        }
        mob = new ModalObjectWithOneContact(mm,srate,bufferSize);
        float dur = .002f; // 2 ms
        int nsamples = (int)(srate * dur);
        float[] cosForce = new float[nsamples];
        for(int i=0;i<nsamples;i++) {
            cosForce[i] = (float)(.5*(1.-Math.cos(2*Math.PI*(i+1)/(1+nsamples))));
        }
        force = new OneShotBuffer(srate,bufferSize,cosForce);
        try {
            mob.addSource(force);
            player.addSource(mob);
        } catch(SinkIsFullException e) {
            System.out.println(this+ " " + e);
        }
        mob.setNf(maxModes);
        float hardNess0 = 10f;
        force.setSpeed(hardNess0);
        force.setVolume(hardNess0);
        
        String[] names = {"Hardness   ",
                          "No. modes  ",
                          "Freq. ",
                          "Hit speed ",
                          "Morph "
        };
        double[] val =   {hardNess0,
                          maxModes,
                          1,
                          hitFreq,
                          0
        };
        double[] min =   {0.1,
                          1,
                          .25,
                          .1,
                          0
        };
        double[] max =   {hardNess0,
                          maxModes,
                          3,
                          1,
                          1
        };
        setValues(val,min,max,names);
        jButton[0].setText ("Stop");
        
        player.start();
        hitter = new Hitter();
        hitter.hitFreq = hitFreq;
        hitter.force = force;
        hitter.start();
        hitter.isRunning = true;
    }
    
    protected void onSlider(int k) {
        switch(k) {
        case 0:
            force.setSpeed((float)this.val[0]);
            force.setVolume((float)this.val[0]);
            break;
        case 1:
            int nf = (int)this.val[1];
            mob.setNf(nf);
            break;
        case 2:
            mob.setFrequencyScale((float)this.val[2]);
            break;
        case 3:
            hitter.hitFreq = this.val[3];
            break;
        case 4:
            morph(this.val[4]);
            mob.computeFilter();
            break;
        }
    }

    protected void jButtonMousePressed (int k,java.awt.event.MouseEvent evt) {
        switch(k) {
        case 0:
            if(hitter.isRunning) {
                jButton[0].setText("Start");
                hitter.isRunning = false;
                mob.clearHistory();
                force.clearBuffer();
                player.resetAGC();
            } else {
                jButton[0].setText("Stop");
                hitter.isRunning = true;
            }
            break;
        }
    }

    class Hitter extends Thread {
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

