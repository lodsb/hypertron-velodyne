import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import jass.contact.*;

/** As Bang using ModalObjectWithOneContact
 */
public class Bang extends Controller {
    static int nsliders = 11;
    static int nbuttons = 2;

    public Bang(java.awt.Frame parent,boolean modal) {
        super (parent, modal,nsliders,nbuttons);
    }

    public void onSlider(int k) {
        switch(k) {
        case 0:
            dur = (float)super.val[0];
            banger.setDur(dur);
            break;
        case 1:
            force = (float)super.val[1];
            banger.setForce(force);
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
            nmodes = (int)super.val[9];
            mob.setNf(nmodes);
            break;
        case 10:
            hitT = (float)super.val[10];
            banger.setHitT(hitT);
            break;            
        }                
    }

    public void onButton(int k) {
        switch(k) {
        case 0:
            af.bang(force,dur/1000);
            break;
        case 1:
            if(isMuted) {
                sp1.resetAGC();
                isMuted = false;
                app.setButtonName("Mute",k);
            } else {
                isMuted = true;
                app.setButtonName("Unmute",k);
            }
            sp1.setMute(isMuted);
            break;
        }
    }

    static ModalObjectWithOneContact mob;
    static ContactForce af;
    static float dur=0; //dur in ms of impact
    static float hitT = -1; // < 0 is no auto hitting
    static float force = 10f; // force of impact
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
    static Banger banger;
    static boolean isMuted = false;
    static Bang app;
    static ModalModel mm;
    static int nmodes;
    
    static String[] names = {"dur ","Fbang ","vslide ","Fslide ","vroll ","Froll ", "lPFroll ","lpDryG ", "loc ","nmodes ","hitT "};
    static double[] val =   {dur,     force,    vs,     fslide,   vr,      froll,   lpFRoll,    lpDryG,  loc,    nmodes,   hitT};
    static double[] min =   {0,       0,        0,      0,        0,       0,       10,         0,       0,      1,        -1};
    static double[] max =   {50,     1000,      1,      1000,     1,       1000,    2000,       1,       1,      2,         10};

    public static void main (String args[]) throws SinkIsFullException {
        float srate = 22050.f;
        int bufferSize = 256*4;
        int bufferSizeJavaSound = 10*1024;
        
        if(args.length != 4) {
            System.out.println("Usage: java Bang ../data/clayvase-nobogus2.sy ../data/cos20ms.wav ../data/d1.25.wav ../data/roll.wav\n");
            return;
        }
        try {
            mm = new ModalModel(args[0]);
            mob = new ModalObjectWithOneContact(mm,srate,bufferSize);
        } catch (java.io.FileNotFoundException ee) {
            System.out.println("Modes file not found\n");
        }
        max[max.length-2] = mm.f.length;
        nmodes = mm.f.length;
        val[max.length-2] = nmodes;
        nLocs = mob.modalModel.np;
        af = new ContactForce(srate,bufferSize,args[1],args[2],args[3]);
        mob.addSource(af);
        af.setStaticContactModelParameters(slideSpeed1, rollSpeed1, vslide0, vslide1,
                                           vroll0, vroll1, physicalToAudioGainSlide,
                                           physicalToAudioGainRoll,physicalToAudioGainImpact);
        af.setSlideProperties(fslide,vs);
        af.setRollProperties(froll,vr);
        af.setRollFilter(lpFRoll, lpDryG);
        sp1 = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        sp1.setUseNativeSound(false);

        sp1.addSource(mob);
        sp1.start();
        banger = new Banger(af);
        banger.start();
        app = new Bang(new java.awt.Frame("Wavetable contact forces"), true);
        app.setSliders(val,min,max,names);
        app.setButtonNames (new String[] {"Bang","Mute"});
        app.setVisible(true);

    }

}

class Banger extends Thread {
    
    float hitT = -1;
    float force = 1;
    float dur = 0;
    ContactForce af;

    public void setHitT(float val) {
        hitT = val;
    }

    public void setForce(float f) {
        force = f;
    }

    public void setDur(float d) {
        dur = d;
    }
    
    public Banger(ContactForce af) {
        this.af = af;
    }
    
    public void run() {
        while(true) {
            try {
                if(hitT > 0) {
                    sleep((int)(1000 * hitT));
                    af.bang(force,dur/1000f);
                } else {
                    sleep(200);
                }
            } catch(Exception e) {;}
        }
    }
}



