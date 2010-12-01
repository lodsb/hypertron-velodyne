import java.io.*;
import java.net.*;
import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import jass.patches.*;

/** CombReverb using Moorers reverb. See e.g.
    @book{Steiglitz96,
	title		= {A Digital Signal Processing Primer with
                          Applications to Digital Audio and Computer Music},
	author          =  {Ken Steiglitz},
	publisher	= {Addison-Wesley},
	address		= {New York},
	year		= {1996},
    pages = {290--295}}

    Defaults are for 25Khz sampling rate from Steiglitz book.
    
    @author Kees van den Doel (kvdoel@cs.ubc.ca)
*/
public class ReverbApplet extends AppletController {
    
    int bufferSize = 16; // no feedback loop can have smaller delay than this
    int bufferSizeJavaSound = 0; // use huge default latency
    float srate = 44100;
    SourcePlayer player;
    AudioIn input;
    ConstantLoopBuffer loopbuf; // another source
    String wavfile = "../data/hello.wav";
    boolean useMike = false;
    float impulseT = 1f; //second
    CombReverb reverb;
    int nReflections = 6;
    // Reverb parameters
    float[] combDelays = {.05f,.056f,.061f,.068f,.072f,.078f}; // delays in seonds
    float allpassDelay = .006f; // delay in seconds
    float a = .7071068f; // allpass parameter
    float[] R = {.4897f,.6142f,.5976f,.5893f,.581f,.5644f}; // feedback comb parameters
    float[] g = {.24f,.26f,.28f,.29f,.3f,.32f}; // low-pass comb parameters
    float dryToWet = .1f; // 1 is dry only
    double minDelay = 1.3*(bufferSize/srate); // smalles feedback delay possible

    public void setNSliders() {
        nsliders = nReflections*3 + 3;
    }

    public void setNButtons() {
        nbuttons = 3;
    }

    protected void createPatch() {
        URL wavurl = null;
        try {
            wavurl = new URL(getCodeBase(),wavfile);
        } catch(MalformedURLException e) {
            System.out.println(e+" Malformed URL: " + wavfile);
        }
        player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        if(useMike) {
            input = new AudioIn(srate,bufferSize,bufferSizeJavaSound);
        } else {
            loopbuf = new ConstantLoopBuffer(srate,bufferSize,wavurl);

        }
        reverb = new CombReverb(bufferSize,srate,nReflections);
        try {
            if(useMike) {
                reverb.addSource(input);
            } else {
                reverb.addSource(loopbuf);
            }
            player.addSource(reverb);
        } catch(SinkIsFullException e) {
            System.out.println(e);
            System.exit(0);
        }
        for(int i=0;i<nsliders;i++) {
            onSlider(i); // set values
        }
        jButton[0].setText ("Reset AGC");
        if(useMike) {
            jButton[1].setText ("Wav file");
        } else {
            jButton[1].setText ("Mike Input");
        }
        jButton[2].setText ("Defaults");
    }
    
    public void start() {
        
        String[] names = {
            "DryToWet ",
            "Allpass a ",
            "Allpass delay ",
            "Comb1 delay ","Comb1 feedback ","Comb1 lowpass coeff. g ",
            "Comb2 delay ","Comb2 feedback ","Comb2 lowpass coeff. g ",
            "Comb3 delay ","Comb3 feedback ","Comb3 lowpass coeff. g ",
            "Comb4 delay ","Comb4 feedback ","Comb4 lowpass coeff. g ",
            "Comb5 delay ","Comb5 feedback ","Comb5 lowpass coeff. g ",
            "Comb6 delay ","Comb6 feedback ","Comb6 lowpass coeff. g ",
        };
        
        double[] val = {
            dryToWet,
            a,
            allpassDelay,
            combDelays[0],R[0],g[0],
            combDelays[1],R[1],g[1],
            combDelays[2],R[2],g[2],
            combDelays[3],R[3],g[3],
            combDelays[4],R[4],g[4],
            combDelays[5],R[5],g[5]
        };
        
        double[] min = {
            0,
            0,
            minDelay,
            minDelay,0,0,
            minDelay,0,0,
            minDelay,0,0,
            minDelay,0,0,
            minDelay,0,0,
            minDelay,0,0
        };
        
        double[] max = {
            1,
            1,
            1,
            1,1,1,
            1,1,1,
            1,1,1,
            1,1,1,
            1,1,1,
            1,1,1
        };
        
        setValues(val,min,max,names);
        createPatch();
        player.start();
    }

    protected void jButtonMousePressed (int k,java.awt.event.MouseEvent evt) {
        switch(k) {
        case 0:
            player.resetAGC();
            break;
        case 1:
            player.stopPlaying();
            useMike = ! useMike;
            createPatch();
            player.resetAGC();
            player.start();
            break;
        case 2:
            this.val[0] = dryToWet;
            this.val[1] = a;
            this.val[2] = allpassDelay;
            int kk = 3;
            for(int i=0;i<nReflections;i++,kk+=3) {
                this.val[kk] = combDelays[i];
                this.val[kk+1] = R[i];
                this.val[kk+2] = g[i];
            }
            setValues(val,min,max,names);
            for(int i=0;i<nsliders;i++) {
                onSlider(i);
            }
            break;
        }
    }

    protected void onSlider(int k) {
        if(k<3) { // allpass parameters and drymix ratio
            switch(k) {
            case 0:
                reverb.setDryToWet((float)(this.val[0]));
                break;
            case 1:
                reverb.setA((float)(this.val[1]));
                break;
            case 2:
                reverb.setM((float)(this.val[2]));
                break;
            }
        } else { // comb paramters
            int icomb = (k-3)/3; // comb index
            int n = (k-3) - 3 * icomb; // labels 3 comb parameters
            switch(n) {
            case 0:
                reverb.setL((float)(this.val[k]),icomb);
                break;
            case 1:
                reverb.setR((float)(this.val[k]),icomb);                
                break;
            case 2:
                reverb.setG((float)(this.val[k]),icomb);
                break;
            }       
        }
    }
}
