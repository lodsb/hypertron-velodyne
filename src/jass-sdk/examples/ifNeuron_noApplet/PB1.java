import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import jass.neuron.*;
import java.awt.*;

public class PB1 {
    
    String[] names = {"gMax ","Tpulse ","Ii(nA) ","Tref ","Tmem ","Vt ","Vrest ","Delay ","Vext ","Vstim "};
    double[] val =   {0,       1e-6,     5,    2,     40,      10,   0,       25,      10,     10};
    double[] min =   {0,       1e-6,     0,      0,      1,      .01,   -20,    0,     0,      0};
    double[] max =   {1e-3,     10,     100,    100,     100,     10,    0,     200,       200,    200};
    int nbuttons = 1+2;
    
    PBNeuron pbNeuron;
    SourcePlayer sp1;
    StatPulses[] stimulus;
    RandOut rand;
    Probe p1;



    DelayUG delay;
    Constant constant;
    Rectify rectify;
    DMSSynapse[] synapse;
    Boolean probeOn = false;

    int nsyn = 10;
    
    float srate = 2000.f;

    public class PB1Controller extends Controller2 {
        private PB1 pb1;
        
        public PB1Controller(java.awt.Frame parent,boolean modal,int nsl,int nbut,PB1 pb1) {
            super(parent,modal,nsl,nbut);
            this.pb1 = pb1;
        }
        
        public void onButton(int k) {
            switch(k) {
            case 0:
                if(probeOn) {
                    setButtonName ("Probe is Off",0);
                    pb1.p1.off();
                } else {
                    setButtonName ("Probe is On",0);
                    pb1.p1.on();
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
                for(int i=0;i<pb1.nsyn;i++) {
                    pb1.synapse[i].gMax = this.val[k];
                }
                break;
            case 1:
                //pb1.constant.setConstant((float)this.val[k]);
                //pb1.rand.setGain((float)this.val[k]);
                for(int i=0;i<pb1.nsyn;i++) {
                    pb1.stimulus[i].setMeanT((float)this.val[k]);
                }
                break;
            case 2:
                pb1.pbNeuron.setIin(this.val[k]*1e-9);
                break;
                
            }
        }
    }


    public static void main (String args[]) throws SinkIsFullException {
        new PB1(args);
    }
    
    public PB1(String args[]) throws SinkIsFullException {
        int bufferSize = 32;
        int bufferSizeJavaSound = 0*1024;
        
        if(args.length <0) {
            System.out.println("Usage: java PB1 ");
            return;
        }
        p1 = new Probe(bufferSize,"v.dat");
        synapse = new DMSSynapse[nsyn];
        stimulus = new StatPulses[nsyn];
        pbNeuron = new PBNeuron(bufferSize, srate);
        sp1 = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        sp1.setPriority(Thread.MAX_PRIORITY);
        sp1.AGCOff();
        for(int i=0;i<nsyn;i++) {
            synapse[i] = new DMSSynapse(1e-3,2e-3,2e3,1e3,1e-9,70e-3);
            stimulus[i] = new  StatPulses(srate,bufferSize);
            stimulus[i].setProbabilityDistributionExponent(0); // all pulses 1
            stimulus[i].setStdT(0.01f); //irr regular pulses
            pbNeuron.addConnection(stimulus[i], synapse[i],true);
        }
        //DMSSynapse synapse = new DMSSynapse(1e-3,2e-3,2e3,1e3,1e-9,70e-3);
        p1.addSource(pbNeuron);
        sp1.addSource(p1);
        p1.off();


        PB1Controller a_controlPanel = new PB1Controller(new java.awt.Frame ("PB1"),false,val.length,nbuttons,this);
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
        a_controlPanel.setButtonNames (new String[] {"Probe Is Off","Save","Load"});
        a_controlPanel.setVisible(true);
        
        sp1.start();
        System.out.println(p1.isOn());
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

