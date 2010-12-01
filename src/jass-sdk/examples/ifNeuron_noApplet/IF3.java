import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import jass.neuron.*;
import java.awt.*;

public class IF3 {
    
    String[] names = {"g ","Vrev ","fIn ","Tref ","Tmem ","Vt ","Vrest ","Delay ","Vext ","Vstim "};
    double[] val =   {10,    20,    10,    2,     40,      10,   0,       25,      10,     10};
    double[] min =   {0,   -100,   0,      0,      1,      .01,   -20,    0,     0,      0};
    double[] max =   {10,  100,   100,    100,     100,     10,    0,     200,       200,    200};
    int nbuttons = 1+2;
    
    IFNeuron ifNeuron;
    SourcePlayer sp1;
    RandPlusOne stimulus;
    Probe p1;
    Probe p2;
    Probe p3;


    DelayUG delay;
    Constant external;
    Rectify rectify;

    Boolean probeOn = false;

    int nSynapses = 3;
    
    float srate = 2000.f;

    public class IF3Controller extends Controller2 {
        private IF3 if3;
        
        public IF3Controller(java.awt.Frame parent,boolean modal,int nsl,int nbut,IF3 if3) {
            super(parent,modal,nsl,nbut);
            this.if3 = if3;
        }
        
        public void onButton(int k) {
            switch(k) {
            case 0:
                if(probeOn) {
                    if3.p1.off();
                    if3.p2.off();
                    if3.p3.off();
                    setButtonName ("Probe is Off",0);
                } else {
                    if3.p1.on();
                    if3.p2.on();
                    if3.p3.on();
                    setButtonName ("Probe is On",0);
                }
                probeOn = !probeOn;
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
            }
                break;
            } 
        }
        
        public void onSlider(int k) {
            switch(k) {
            case 0:
                float sw = (float)this.val[k];
                for(int i=0;i<nSynapses;i++) {
                    if3.ifNeuron.setSynapticWeight(i,sw);
                }
                break;
            case 1:
                float vrev = (float)this.val[k];
                for(int i=0;i<nSynapses;i++) {
                    if3.ifNeuron.setSynapticReversalPotential(i,vrev);
                }
                break;
            case 2:
                //                if3.stimulus.setFrequency((float)(this.val[k]));
                break;
            case 3:
                if3.ifNeuron.setTr((float)(this.val[k]/1000));
                break;
            case 4:
                if3.ifNeuron.setTm((float)(this.val[k]/1000));
                break;
            case 5:
                if3.ifNeuron.setVt((float)this.val[k]);
                break;
            case 6:
                if3.ifNeuron.setVr((float)this.val[k]);
                break;
            case 7:
                if3.delay.setRawDelay((float)(this.val[k])/1000);
                break;
            case 8:
                if3.external.setConstant((float)this.val[k]);
                break;
            case 9:
                if3.stimulus.setVolume((float)this.val[k]);
                break;
            }
        }
    }


    public static void main (String args[]) throws SinkIsFullException {
        new IF3(args);
    }
    
    public IF3(String args[]) throws SinkIsFullException {
        int bufferSize = 2;
        int bufferSizeJavaSound = 1024;
        float Tr=10f/1000f;     // refractory time
        float Vt=10f;    // threshold potential
        float V0=0;    // resting potential
        float Vr=0;    // reset potential
        float Tm=40f/1000f; // membrane decay time constant Tm = Rm*Cm, Rm and Cm drop out of the eq.
        
        if(args.length <0) {
            System.out.println("Usage: java IF3 ");
            return;
        }

        ifNeuron = new IFNeuron(bufferSize, nSynapses,srate,Vt,V0,Vr,Tr,Tm);
        stimulus = new RandPlusOne(bufferSize);
        external = new Constant(bufferSize);
        //rectify = new Rectify(bufferSize);
        p1 = new Probe(bufferSize,"in1.m");
        p2 = new Probe(bufferSize,"in2.m");
        p3 = new Probe(bufferSize,"out.dat");
        delay = new DelayUG(bufferSize,srate);
        sp1 = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        
        
        // build graph
        //rectify.addSource(stimulus);
        p1.addSource(stimulus);
        ifNeuron.addSource(p1);
        delay.addSource(stimulus);
        p2.addSource(delay);
        ifNeuron.addSource(p2);
        ifNeuron.addSource(external);

        
        p3.addSource(ifNeuron);
        sp1.addSource(p3);

        ifNeuron.setSynapticReversalPotential(0,20);
        ifNeuron.setSynapticWeight(0,10f);

        p1.off();
        p2.off();
        p3.off();




        IF3Controller a_controlPanel = new IF3Controller(new java.awt.Frame ("IF3"),false,val.length,nbuttons,this);
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
        a_controlPanel.setButtonNames (new String[] {"Probe Off","Save","Load"});
        a_controlPanel.setVisible(true);
        
        sp1.start();
    }

    class RandPlusOne extends Out {
        Constant c;
        RandOut s;
        Mixer m;
        public RandPlusOne(int bufferSize) {
            super(bufferSize);
            m = new Mixer(bufferSize,2);
            s = new RandOut(bufferSize);
            c = new Constant(bufferSize);
            try {
                m.addSource(s);
                m.addSource(c);
                m.setGain(0,1);
                m.setGain(1,1);
            } catch(Exception e) {}
        }


        public void setVolume(float f) {
            s.setGain(f);
            c.setConstant(f);
        }
        
        protected void computeBuffer() {
            try {
                buf = m.getBuffer(getTime());
            } catch(BufferNotAvailableException e) {
                System.out.println(this+" "+e);            
            }
        }

    }
    
}

