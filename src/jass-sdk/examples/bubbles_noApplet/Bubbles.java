import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.util.*;
import java.awt.*;

/**
   Bubbles. Needs sy file with nBubbles freq.
   Analysis of damping leads to:

   f = 3.0/r with r bubble radius

   d = (.043 + 1/(400*sqrt(r))*f
   OR
   d = .043*f + f^1.5/721

   The gain a of a bubble is r^aExp (it's impulse response for a
   unit radial inward collapse).
   Assume bubble energy is 2 pi R^3 u^2, u is av. inward velocity on boundary.
   If assume u indep. of bubble size, then aExp = 1.5

   Create bank of nBubbles bubbles of sizes in range rmin to rmax,
   geometrically interpolated.

   Excite each bubble with poisson process of impulses with lambda(r)
   dependent on bubble radius. The magnitude of impulse is rnd^probExp,
   where rnd is random variable in [0 1]. probExp=10 definitely sounds
   better than 1.

 */

public class Bubbles {

    float srate = 22050.f;
    float fUpper = 10000.0f; // highest bubble freq.
    int bufferSize = 32;
    int bufferSizeJavaSound = 1024*10;
    int nBuffersRtAudio = 1024/bufferSize;
    boolean nativeSound = false;
    int nchannels = 1;
    protected double xi=0.1; // rise factor
    protected double riseCutoffExcitation; // rise trigger cutoff

    int nBubbles;

    // sliders properties
    double aExp = 1.5;
    double lExp = 1;
    // bubble radii in mm
    double rmin = 3000/(srate/2); // in mm
    double rmax = 10;
    double lambdaFactor = 1;
    double bubblesPerS = 1000;
    double ximin=0,ximax=1;
    double riseCutoffMax=1;
    double riseCutoffMin=0;
    double riseCutoff=.9;
    // for modal models scale freq damping and set balance g between modal and water sound g=0 is no modal
    double ffact=1,dfact=1,gfact=1;
    double ffactMin=.1,dfactMin=.1,gfactMin=0;
    double ffactMax=10,dfactMax=100,gfactMax=1;
    double std; // relative standard deviation of mean intet bubble time
    double probExp = 10;
    String[] names = {"alpha ","gamma ","bbls/s "    ,"std "  ,"beta " ,"r- " ,"r+ ","xi " ,"riseCutoff " ,"fscale ","dscale ","mgain "};
    double[] val =   {aExp    ,lExp    ,bubblesPerS  ,std     ,probExp ,rmin  ,rmax ,xi    ,riseCutoff    ,ffact    ,dfact    ,gfact};
    double[] min =   {0.01    ,-1      ,0            ,0       ,.1      ,rmin  ,rmin ,ximin ,riseCutoffMin ,ffactMin ,dfactMin ,gfactMin};
    double[] max =   {2       ,10      ,100000       ,4       ,50      ,2     ,50   ,ximax ,riseCutoffMax ,ffactMax ,dfactMax ,gfactMax};
    int nbuttons = 3;
    String lambdaModel = "1/f"; // or "1/f";
    
    public class BubbleController extends Controller {
        private Bubbles bubbles;
        
        public BubbleController(java.awt.Frame parent,boolean modal,int nsl,int nbut,Bubbles bubbles) {
            super(parent,modal,nsl,nbut);
            this.bubbles = bubbles;
        }
        
        public void onButton(int k) {
            switch(k) {
            case 0:
                bubbles.player.resetAGC();
                for(int i=0;i<bubbles.nBubbles;i++) {
                    bubbles.mixer.setGain(i,1);
                }
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
                recomputeBubbles();
                bubbles.updateLambdaCPStuff();
                bubbles.resetLambdaCP();
                recomputeEvents();
            }
            break;
            } 
        }
        
        public void onSlider(int k) {
            switch(k) {
            case 0:
                bubbles.val[k] = this.val[k];
                bubbles.aExp = bubbles.val[k];
                recomputeBubbles();
                break;
            case 1:
                bubbles.val[k] = this.val[k];
                bubbles.lExp = bubbles.val[k];
                recomputeEvents();
                break;
            case 2:
                bubbles.val[k] = this.val[k];
                bubbles.bubblesPerS = bubbles.val[k];
                recomputeEvents();
                break;
            case 3:
                bubbles.val[k] = this.val[k];
                bubbles.std = bubbles.val[k];
                recomputeEvents();
                break;
            case 4:
                bubbles.val[k] = this.val[k];
                bubbles.probExp = bubbles.val[k];
                recomputeEvents();
                break;
            case 5:
                bubbles.val[k] = this.val[k];
                bubbles.rmin = bubbles.val[k];
                recomputeBubbles();
                bubbles.updateLambdaCPStuff();
                bubbles.resetLambdaCP();
                recomputeEvents();
                break;
            case 6:
                bubbles.val[k] = this.val[k];
                bubbles.rmax = bubbles.val[k];
                recomputeBubbles();
                bubbles.updateLambdaCPStuff();
                bubbles.resetLambdaCP();
                recomputeEvents();
                break;
            case 7:
                bubbles.val[k] = this.val[k];
                bubbles.xi = bubbles.val[k];
                for(int i=0;i<bubbles.nBubbles;i++) {
                    mobs[i].setXi((float)bubbles.xi);
                }
                break;
            case 8:
                bubbles.val[k] = this.val[k];
                bubbles.riseCutoffExcitation = bubbles.val[k];
                for(int i=0;i<bubbles.nBubbles;i++) {
                    mobs[i].setRiseCutoffExcitation((float)bubbles. bubbles.riseCutoffExcitation);
                }
                break;
            case 9:
                if(haveSy) {
                    resonantSurface.setFrequencyScale((float)this.val[k]);
                }
                break;
            case 10:
                if(haveSy) {
                    resonantSurface.setDamping((float)this.val[k]);
                }
                break;
            case 11:
                if(haveSy) {
                    float g1 = (float)this.val[k];
                    float g2 = (float)(1-this.val[k]);
                    mixer2.setGain(0,g1);
                    mixer2.setGain(1,g2);
                }
                break;
            }
        }
    }

    // sliders properties for lambda controller
    String[] lNames;
    double[] lVal; // relative nbubbles/s for bubble i
    double[] lMin;
    double[] lMax;

    public void initLambdaCPStuff() {
        lNames = new String[nBubbles];
        lVal = new double[nBubbles];
        lMin = new double[nBubbles];
        lMax = new double[nBubbles];
        for(int i=0;i<nBubbles;i++) {
            lNames[i] = new Integer((int)(radii[i]*10000)).toString() + ": ";
            lMin[i] = 0;
            lMax[i] = 1.0;
        }
        setLambdaCPStuff();
    }

    // updates when radii population changes
    public void updateLambdaCPStuff() {
        for(int i=0;i<nBubbles;i++) {
            lNames[i] = new Integer((int)(radii[i]*10000)).toString() + ": ";
        }
        //setLambdaCPStuff();
    }

    // set values according to some scheme
    public void setLambdaCPStuff() {

        if(lambdaModel.equals("flat")) {
            for(int i=0;i<nBubbles;i++) {
                lVal[i] = .5;
            }
        } else if(lambdaModel.equals("1/f")) {
            for(int i=0;i<nBubbles;i++) {
                lVal[i] = 1./Math.pow((1000*radii[i]/rmin),lExp);
            }
            double b = max_lVal();
            for(int i=0;i<nBubbles;i++) {
                lVal[i] /= b;
            }
        }
    }
    
    public void resetLambdaCP() {
        l_controlPanel.setValues(lVal,lMin,lMax,lNames);
        for(int i=0;i<nBubbles;i++) {
            //l_controlPanel.onSlider(i);
        }
    }

    private double sum_lVal() {
        double res = 0;
        for(int i=0;i<nBubbles;i++) {
            res += lVal[i];
        }
        return res;
    }
    
    private double max_lVal() {
        double res = 0;
        for(int i=0;i<nBubbles;i++) {
            if(lVal[i] > res) {
                res = lVal[i];
            }
        }
        return res;
    }

    int lNbuttons = 4;
    
    public class LambdaController extends Controller {
        private Bubbles bubbles;
        
        public LambdaController(java.awt.Frame parent,boolean modal,int nsl,int nbut,Bubbles bubbles) {
            super(parent,modal,nsl,nbut);
            this.bubbles = bubbles;
        }
        
        public void onButton(int k) {
            switch(k) {
            case 0:
                bubbles.lambdaModel = "flat";
                bubbles.setLambdaCPStuff();
                bubbles.resetLambdaCP();
                recomputeEvents();
                break;
            case 1:
                bubbles.lambdaModel = "1/f";
                bubbles.setLambdaCPStuff();
                bubbles.resetLambdaCP();
                recomputeEvents();
                break;
            case 2: {
                FileDialog fd = new FileDialog(new Frame(),"Save");
                fd.setMode(FileDialog.SAVE);
                fd.setVisible(true);
                saveToFile(fd.getFile());
            }
            break;
            case 3: {
                FileDialog fd = new FileDialog(new Frame(),"Load");
                fd.setMode(FileDialog.LOAD);
                fd.setVisible(true);
                loadFromFile(fd.getFile());
                resetLambdaCP();
                recomputeEvents();
            }
            break;
            } 
        }
        
        public void onSlider(int k) {
            lVal[k] = this.val[k];
            /*
            double b = sum_lVal() - lVal[k];
            for(int i=0;i<nBubbles;i++) {
                if(i!=k) {
                    lVal[i] *= (1 - lVal[k])/b;
                }
            }
            */
            //resetLambdaCP();
            recomputeEvents();
        }
    }
    
    Mixer mixer;
    Mixer mixer2;
    SourcePlayer player;
    Bubbles bubbles;
    SingleMode[] mobs;
    double[] radii; // of bubbles in meters
    LambdaController l_controlPanel;

    StatPulses[] sources;

    public void recomputeBubbles() {
        // rmin rmax in mm, everything else in m
        double lmin = Math.log(rmin/1000);
        double lmax = Math.log(rmax/1000);
        double dlr = (lmax-lmin)/(nBubbles-1);
        for(int i=0;i<nBubbles;i++) {
            radii[i] = Math.exp(lmin + i*dlr);
        }
        for(int k=0;k<nBubbles;k++) {
            double f = 3/radii[k]; 
            mobs[k].setFreq((float)f);
            mobs[k].setDamping((float)(f*(.043 + Math.sqrt(f)/721)));
            mobs[k].setGain((float)(Math.pow(radii[k]/(rmax/1000),aExp)));
            mobs[k].computeResonCoeff();
        }
    }
    
    public void recomputeEvents() {
        // first normalize bubble population by factor sum_lVal
        double tmp = sum_lVal();
        if(tmp == 0) {
            tmp = 1;
        }
        for(int k=0;k<nBubbles;k++) {
            double meanT = tmp/(bubblesPerS*lVal[k]);
            sources[k].setMeanT((float)meanT);
            sources[k].setStdT((float)(meanT*std));
            sources[k].setProbabilityDistributionExponent((float)probExp);
            sources[k].reset();
        }
    }
    
    public static void main (String args[]) throws Exception {
        new Bubbles(args);
    }

    boolean haveSy;
    ModalModel resonantSurfaceModes;
    ModalObjectWithOneContact resonantSurface;
    
    public Bubbles(String args[]) throws Exception {
        
        if(args.length != 4) {
            System.out.println("Usage: java Bubbles nBubbles jassBuffersize srate syfile");
            return;
        }
        nBubbles = Integer.parseInt(args[0]);
        bufferSize = Integer.parseInt(args[1]);
        srate = Float.parseFloat(args[2]);
        String syfn = args[3];
        try {
            resonantSurfaceModes = new ModalModel(syfn);
            haveSy = true;
        } catch(Exception e) {
            haveSy = false;
        }
        if(haveSy) {
            resonantSurface = new ModalObjectWithOneContact(resonantSurfaceModes,srate,bufferSize/nchannels);
        }
        bubbles = this;

        radii = new double[nBubbles];
        mobs = new SingleMode[nBubbles];
        player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        player.setUseNativeSound(nativeSound);
        player.setNumRtAudioBuffersNative(nBuffersRtAudio);
        sources = new StatPulses[nBubbles];
        mixer = new Mixer(bufferSize,nBubbles);

        for(int k=0;k<nBubbles;k++) {
            sources[k] = new StatPulses(srate,bufferSize);
            // placeholders, have to get right values later
            float f = 100*(k+1);
            float d = 10;
            float a = 1;
            mobs[k] = new SingleMode(f,d,a,srate,bufferSize);
            mobs[k].addSource(sources[k]);
            mixer.addSource(mobs[k]);
        }
        if(haveSy) {
            resonantSurface.addSource(mixer);
            int nsources = 2; // liquid and modelmodel
            mixer2 = new Mixer(bufferSize,nsources);
            mixer2.addSource(mixer);
            mixer2.addSource(resonantSurface);
            player.addSource(mixer2);
            float g1 = (float)gfact;
            float g2 = (float)(1-gfact);
            mixer2.setGain(0,g1);
            mixer2.setGain(1,g2);
        } else {
             player.addSource(mixer);
        }
        
        // compute actual model paramters
        recomputeBubbles();
        
        BubbleController a_controlPanel =
            new BubbleController(new java.awt.Frame ("Bubbles"),false,val.length,nbuttons,this);

        initLambdaCPStuff();
        l_controlPanel =
            new LambdaController(new java.awt.Frame ("Densities"),false,lVal.length,lNbuttons,this);

        a_controlPanel.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("Close handler called");
                player.stopPlaying();
                try{
                    //sleep(500);
                } catch(Exception e3) {
                }
                System.exit(0);
            }
	    });

        recomputeEvents();
	
        a_controlPanel.setSliders(val,min,max,names);
        a_controlPanel.setButtonNames (new String[] {"Reset","Save","Load"});
        a_controlPanel.setVisible(true);
        a_controlPanel.onButton(0);
        
        l_controlPanel.setSliders(lVal,lMin,lMax,lNames);
        l_controlPanel.setButtonNames (new String[] {"Flat","1/f^gamma","Save","Load"});
        l_controlPanel.setVisible(true);
        l_controlPanel.setLocation(0,0);
        resetLambdaCP();
        recomputeEvents();
        
        player.start();


    }


}


