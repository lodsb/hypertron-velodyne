import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import jass.contact.*;

/** As Bang using ModalObjectWithOneContact
 */
public class BangN extends Controller {
    static int nsliders = 10;
    static int nbuttons = 2;

    public BangN(java.awt.Frame parent,boolean modal) {
        super (parent, modal,nsliders,nbuttons);
    }

    public void onSlider(int k) {
        switch(k) {
        case 0:
            dur = (float)super.val[0];
            break;
        case 1:
            force = (float)super.val[1];
            break;
        case 2:
            vs = (float)super.val[2];
            af.setSlideProperties(fslide,vs);
            break;
        case 3:
            fslide = (float)super.val[3];
            af.setSlideProperties(fslide,vs);
            break;
        case 4:
            vr = (float)super.val[4];
            af.setRollProperties(froll,vr);
            break;
        case 5:
            froll = (float)super.val[5];
            af.setRollProperties(froll,vr);
            break;
        case 6:
            lpFRoll = (float)super.val[6];
            af.setRollFilter(lpFRoll, lpDryG);
            break;
        case 7:
            lpDryG = (float)super.val[7];
            af.setRollFilter(lpFRoll, lpDryG);
            break;
        case 8:
            loc = (float)super.val[8];
            if(nLocs > 1) {
                double x = (.00001 +loc)/1.0001; // 0 - 1, map to location
                x = x*(nLocs - 1); // 0 -- np-1
                int p1 = (int)x;
                int p2 = (int)(x+1);
                int p3 = 0;
                float b2 = (float)(x-p1);
                float b1 = (float)(1 - b2);
                float b3 = 0;
                System.out.println("np="+nLocs+ " "+"loc(0-np-1)="+x);
                mob.setLocation(p1,p2,p3,b1,b2,b3);
            }
            break;
        case 9:
            bal = (float)super.val[9];
            balv[0] = bal;
            balv[1] = 1-bal;
            af.setSlideBalance(balv); // set balance between 2 wav files
            af.setSlideProperties(fslide,vs); // activate
            break;
        }                
    }

    public void onButton(int k) {
        switch(k) {
        case 0:
            af.bang(force,dur/1000);
            break;
        case 1:
            sp1.resetAGC();
            break;
        }
    }

    static ModalObjectWithOneContact mob;
    static ContactForceN af;
    static float dur=1.f; //dur in ms of impact
    static float force = 0; // force of impact
    static float vs=0,vr=0,fslide=0,froll=0; // slide, roll speed and normal forces    
    // maximum audio speeds, 1 is original wav file
    static float slideSpeed1=1f,rollSpeed1=1f;
    // physical speed ranges
    static float vslide0=.0f,vslide1=1f,vroll0=.00f,vroll1=1f;
    // gain ratios
    static float physicalToAudioGainSlide=1,physicalToAudioGainRoll=1,physicalToAudioGainImpact=1;
    static float lpFRoll=100.f, lpDryG=0;
    static int nLocs = 1; // if using an sy with locations
    static float loc=0; //location 0-1
    static SourcePlayer sp1;
    static float bal = 1f;
    static float[] balv = {bal,1-bal};


    
    static String[] names = {"dur ","Fbang ","vslide ","Fslide ","vroll ","Froll ", "lPFroll ","lpDryG ","loc0-1 ", "bal "};
    static double[] val =   {dur,     force,    vs,     fslide,   vr,      froll,   lpFRoll,    lpDryG,  loc,        bal  };
    static double[] min =   {0,       0,        0,      0,        0,       0,       10,         0,       0,          0    };
    static double[] max =   {50,     1000,      1,      1000,     1,       1000,    2000,       1,       1,          1    };

    public static void main (String args[]) throws SinkIsFullException {
        float srate = 44100.f;
        int bufferSize = 512;
        int bufferSizeJavaSound = 6*1024;
        
        if(args.length != 5) {
            System.out.println("Usage: java BangN ../data/clayvase-nobogus2.sy ../data/cos20ms.wav ../data/scrapeRim20cmps.wav ../data/acmevase-smooth2.wav ../data/roll.wav\n");
            return;
        }
        try {
            mob = new ModalObjectWithOneContact(new ModalModel(args[0]),srate,bufferSize);
        } catch (java.io.FileNotFoundException ee) {
            System.out.println("Modes file not found\n");
        }
        nLocs = mob.modalModel.np;
        af = new ContactForceN(srate,bufferSize,args[1],
                               new String[]{args[2],args[3]},args[4]);
        mob.addSource(af);
        af.setStaticContactModelParameters(slideSpeed1, rollSpeed1, vslide0, vslide1,
                                           vroll0, vroll1, physicalToAudioGainSlide,
                                           physicalToAudioGainRoll,physicalToAudioGainImpact);
        af.setSlideProperties(fslide,vs);
        af.setRollProperties(froll,vr);
        af.setRollFilter(lpFRoll, lpDryG);
        sp1 = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        sp1.setUseNativeSound(true);
        sp1.addSource(mob);
        sp1.start();
        BangN app = new BangN(new java.awt.Frame("Wavetable contact forces"), true);
        app.setSliders(val,min,max,names);
        app.setButtonNames (new String[] {"Bang","Reset"});
        app.setVisible(true);
                
    }

}
