import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import jass.neuron.*;

public class IF1 {
    
    String[] names = {"g ","Vrev ","fIn ","Tref ","Tmem ","Vt ","Vrest "};
    double[] val =   {100,    20,    10,    2,     40,      10,   0};
    double[] min =   {0,   -100,   0,      0,      1,      .01,   -20};
    double[] max =   {500,  100,   100,    40,     100,     10,    0};
    int nbuttons = 2;
    
    IFNeuron ifNeuron;
    SourcePlayer sp1;
    Sine synapse;
    Probe p1;
    Probe p2;
    DelayUG ddd;
    //Constant synapse;
    //RandOutSquared  synapse;
    //RandPulses  synapse;
    Rectify rectify;
    float sw = 1;
    float vrev = 20;
    float pulseRate = 10;
    float srate = 2000.f;

    public class IF1Controller extends Controller {
        private IF1 if1;
        
        public IF1Controller(java.awt.Frame parent,boolean modal,int nsl,int nbut,IF1 if1) {
            super(parent,modal,nsl,nbut);
            this.if1 = if1;
        }
        
        public void onButton(int k) {
            switch(k) {
            case 0:
                if1.p1.off();
                if1.p2.off();
                break;
            case 1:
                if1.p1.on();
                if1.p2.on();
                break;
            } 
        }
        
        public void onSlider(int k) {
            switch(k) {
            case 0:
                if1.sw = (float)this.val[k];
                if1.ifNeuron.setSynapticWeight(0,if1.sw);
                break;
            case 1:
                if1.vrev = (float)this.val[k];
                if1.ifNeuron.setSynapticReversalPotential(0,if1.vrev);
                break;
            case 2:
                if1.pulseRate = (float)this.val[k];
                if1.synapse.setFrequency(pulseRate);
                //if1.synapse.setProbabilityPerSample(pulseRate/if1.srate);
                break;
            case 3:
                if1.ifNeuron.setTr((float)(this.val[k]/1000));
                break;
            case 4:
                if1.ifNeuron.setTm((float)(this.val[k]/1000));
                break;
            case 5:
                if1.ifNeuron.setVt((float)this.val[k]);
                break;
            case 6:
                if1.ifNeuron.setVr((float)this.val[k]);
                break;
            }
        }
    }


    public static void main (String args[]) throws SinkIsFullException {
        new IF1(args);
    }
    
    public IF1(String args[]) throws SinkIsFullException {
        int bufferSize = 32;
        int bufferSizeJavaSound = 1*1024;
        int nSyn = 1;
        float Tr=10f/1000f;     // refractory time
        float Vt=10f;    // threshold potential
        float V0=0;    // resting potential
        float Vr=0;    // reset potential
        float Tm=40f/1000f; // membrane decay time constant Tm = Rm*Cm, Rm and Cm drop out of the eq.
        
        if(args.length <0) {
            System.out.println("Usage: java IFNeuron ");
            return;
        }
        ddd = new DelayUG(1);
        ifNeuron = new IFNeuron(bufferSize, nSyn,srate,Vt,V0,Vr,Tr,Tm);
        synapse = new Sine(srate,bufferSize);
        rectify = new Rectify(bufferSize);
        p1 = new Probe(bufferSize,"in.m");
        p2 = new Probe(bufferSize,"out.m");
        sp1 = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        
        //synapse = new Constant(bufferSize);
        //synapse = new RandOutSquared(bufferSize);
        //synapse = new RandPulses(bufferSize);
        //synapse.setProbabilityPerSample(pulseRate/srate);

        // build graph
        rectify.addSource(synapse);
        p1.addSource(rectify);
        ifNeuron.addSource(p1);
        p2.addSource(ifNeuron);
        sp1.addSource(p2);

        synapse.setFrequency(pulseRate);
        ifNeuron.setSynapticReversalPotential(0,20);
        ifNeuron.setSynapticWeight(0,10f);

        p1.off();
        p2.off();




        IF1Controller a_controlPanel = new IF1Controller(new java.awt.Frame ("IF1"),false,val.length,nbuttons,this);
        a_controlPanel.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    sp1.stopPlaying();
                    try{
                        //sleep(500);
                    } catch(Exception e3) {
                    }
                    System.exit(0);
                }
            });
        
        a_controlPanel.setSliders(val,min,max,names);
        a_controlPanel.setButtonNames (new String[] {"Probe Off","Probe On"});
        a_controlPanel.setVisible(true);
        a_controlPanel.onButton(0);
        
        sp1.start();
    }

}

