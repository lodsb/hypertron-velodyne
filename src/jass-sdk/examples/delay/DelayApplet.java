import jass.render.*;
import jass.engine.*;
import jass.generators.*;

/**
   Delay line with low-pass; a simple Karplus-Strong like string
   sound (though the lowpass is at the wrong place it sounds good to me)
   @author Kees van den Doel (kvdoel@cs.ubc.ca)
*/
public class DelayApplet extends AppletController {
    Mixer mixer;
    SourcePlayer player;
    boolean isOn = false;
    float gain2 = .95f;
    
    public void setNSliders() {
        nsliders = 1;
    }
   
    public void setNButtons() {
        nbuttons = 1;
    }
    public void start() {
        float srate = 44100.f;
        int bufferSize = 128*4;
        int bufferSizeJavaSound = 8*1024;
        /*
          [Rnd]o->[Mixer]o->[speaker]
                 ^---<---|
        */   
        Source rout = new Rnd(bufferSize);
        mixer = new Mixer(bufferSize,2);
        player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        LowPass lp = new LowPass();
        FilterContainer lowpass = new FilterContainer(srate,bufferSize,lp);
        try {
            player.addSource(mixer);
            mixer.addSource(rout);
            mixer.addSource(lowpass);
            lowpass.addSource(mixer);
        } catch(SinkIsFullException e) {
        }

        mixer.setGain(0,1f);
        mixer.setGain(1,gain2); // feedback

        String[] names = {"Feedback "};
        double[] val =   {gain2      };
        double[] min =   {0          };
        double[] max =   {1          };
        setValues(val,min,max,names);
        jButton[0].setText ("Stop");
        
        player.start();
        isOn = true;
    }

    protected void jButtonMousePressed (int k, java.awt.event.MouseEvent evt) {
        switch(k) {
        case 0:
            if(isOn) {
                jButton[0].setText ("Start");
                isOn = false;
                mixer.setGain(0,0);
            } else {
                jButton[0].setText ("Stop");
                isOn = true;
                player.resetAGC();
                mixer.setGain(0,gain2);
            }
            break;
        }

    }
    
    protected void onSlider(int k) {
        switch(k) {
        case 0:
            mixer.setGain(1,gain2 = (float)this.val[0]);
            break;
        }
    }
    
}


// Simple low-pass
class LowPass implements Filter {
    float lastin = 0;
    public void filter(float [] output, float[] input, int nsamples, int inputOffset) {
        int len = input.length;
        // note input[] may be same as output[]!
        for(int i=0;i<len;i++) {
            float tmplastin = input[i];
            output[i] = (lastin + tmplastin)*.5f;
            lastin = tmplastin;
        }
    }
}

// Produce some random pulses to excite the string
class Rnd extends Out {
    public Rnd(int bufferSize) {
        super(bufferSize);
    }
    
    protected void computeBuffer() {
        double x = Math.random();
        if(x > .95) {
            buf[0] = 1;
        } else {
            buf[0] = 0;
        }
    }
}
