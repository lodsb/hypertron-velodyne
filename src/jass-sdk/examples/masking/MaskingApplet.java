import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.util.*;
import java.net.*;

/*
  Tones of the form:
  y = a*sin(2*pi*f*t)
  I(y) = a*a/2
  db = 10*log(a*a/(2*I0)) = 10*log(a*a/2) - 10*log(I0)

  noise of the form:
  y = a * rnd(t), rnd white noise of bandwidth Fb
  I(y) = a*a/3
  D(y) = I(y)/Fb
  Level l(y) = 10*log(a*a/(3*Fb*I0)) = 10*log(a*a/(3*Fb)) - 10*log(I0)

  A tone can be heard when its db level is 17 above the density level
  of white noise in these units.
  We've set I0 = 1, arbitrary.
 */
public class MaskingApplet extends AppletController {

    float dbMax=0,dbMin=-100,lMax = -50,lMin = -100;
    float f1=1200,f2=400,a1,a2,db1=dbMax,db2=dbMin,a_n,l_n=lMax;
    String sine50Hz44100 = "../data/sin20ms.wav";
    float baseFreqWavFile = 50;
    float srate = 44100.f;
    
    SourcePlayer player;
    LoopBuffer tone1,tone2;
    RandOut randOut;
    Mixer mixer;
    int nSources = 3; // 2 tones + noise

        
    public void setNSliders() {
        nsliders = 5;
    }

    public void setNButtons() {
        nbuttons = 2;
    }

    // Convert db to amp for tone
    public double db2a(double db) {
        return Math.sqrt(2)*Math.exp(db*Math.log(10)/20);
    }

    // Convert density level to amp for white noise
    public double level2a(double lev) {
        return Math.sqrt(3*srate/2)*Math.exp(lev*Math.log(10)/20);
    }
    
    public void init() {
        super.init();
        sine50Hz44100 = getParameter("wavfile");
    }

    public void start() {
        int bufferSize = 1024;
        int bufferSizeJavaSound = 8*1024;
        URL codebase = getCodeBase();
        URL wavurl = null;
        try {
            wavurl = new URL(codebase,sine50Hz44100);
        } catch(MalformedURLException e) {
            System.out.println(e+" Malformed URL: " +codebase+" "+ sine50Hz44100);
        }
        tone1 = new LoopBuffer(srate,bufferSize,wavurl);
        tone2 = new LoopBuffer(srate,bufferSize,wavurl);
        randOut = new RandOut(bufferSize);
        mixer = new Mixer(bufferSize,nSources);
        player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        try {
            player.addSource(mixer);
            mixer.addSource(tone1);
            mixer.addSource(tone2);
            mixer.addSource(randOut);
        } catch(Exception e) {
        }
        // Add control panel
        String[] names = {"Freq 1 ",
                          "Level (db) 1 ",
                          "Freq. 2 ",
                          "Level (db) 2 ",
                          "Noise density level (db/Hz) "
        };
        double[] val =   {f1,
                          db1,
                          f2,
                          db2,
                          l_n
        };
        double[] min =   {50,
                          dbMin,
                          50,
                          dbMin,
                          lMin
        };
        double[] max =   {srate/2,
                          dbMax,
                          srate/2,
                          dbMax,
                          lMax
        };
        tone1.setSpeed(f1/baseFreqWavFile);
        tone2.setSpeed(f2/baseFreqWavFile);
        a1 = (float)db2a(db1);
        a2 = (float)db2a(db2);
        a_n = (float)level2a(l_n);
        mixer.setGain(0,a1);
        mixer.setGain(1,a2);
        mixer.setGain(2,a_n);
        setValues(val,min,max,names);
        jButton[0].setText ("Reset");
        jButton[1].setText ("Mute");
        //show();
        player.start();
    }

    protected void onSlider(int k) {
        switch(k) {
        case 0:
            f1 = (float)this.val[0];
            tone1.setSpeed(f1/baseFreqWavFile);
            break;
        case 1:
            db1 = (float)this.val[1];
            a1 = (float)db2a(db1);
            mixer.setGain(0,a1);
            break;
        case 2:
            f2 = (float)this.val[2];
            tone2.setSpeed(f2/baseFreqWavFile);
            break;
        case 3:
            db2 = (float)this.val[3];
            a2 = (float)db2a(db2);
            mixer.setGain(1,a2);
        case 4:
            l_n = (float)this.val[4];
            a_n = (float)level2a(l_n);
            mixer.setGain(2,a_n);
        }
    }
    
    protected void jButtonMousePressed (int k,java.awt.event.MouseEvent evt) {
        switch(k) {
        case 0:
            player.resetAGC();
            break;
        case 1:
            boolean muted = player.getMute();
            player.setMute(!muted);
            if(muted) {
                jButton[1].setText ("Mute");
            } else {
                jButton[1].setText ("Unmute");
            }
            break;
        } 
    }   
    
}



