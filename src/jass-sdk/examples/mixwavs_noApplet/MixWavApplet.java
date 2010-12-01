import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.net.*;

/**
   Mix some looping wav files
   @author Kees van den Doel (kvdoel@cs.ubc.ca)
*/
public class MixWavApplet extends AppletController {
    SourcePlayer player;
    ConstantLoopBuffer[] loopbuffer;
    Mixer mixer;
    int nvoices ;
    boolean isOn = false;
    String[] wavfiles;
    
    public void setNSliders() {
        nvoices = Integer.parseInt(getParameter("nvoices"));
        nsliders = nvoices;
    }
   
    public void setNButtons() {
        nbuttons = 1;
    }
    
    public void init() {
        super.init();


        String[] nm = new String[nvoices];
        wavfiles = new String[nvoices];
        for(int i=0;i<nvoices;i++) {
            nm[i] = "wavfile" + i;
            wavfiles[i] = getParameter(nm[i]);
        }

    }
    
    public void start() {
        float srate = 11025.f;
        int bufferSize = 128;
        int bufferSizeJavaSound = 8*1024;
        loopbuffer = new ConstantLoopBuffer[nvoices];
        URL codebase = getCodeBase();
        URL[] wavurl = new URL[nvoices];
        System.out.println("wavfiles[0]= "+ wavfiles[0]);
        try {
            for(int i=0;i<nvoices;i++) {
                wavurl[i] = new URL(codebase,wavfiles[i]);
            }
        } catch(MalformedURLException e) {
            System.out.println(e+" Malformed URL: " +codebase+" "+ wavfiles);
        }

        for(int i=0;i < nvoices;i++) {
            loopbuffer[i] = new ConstantLoopBuffer(srate,bufferSize,wavurl[i]);
        }
        mixer = new Mixer(bufferSize,nvoices);

        for(int i=0;i < nvoices;i++) {
            mixer.setGain(i,1);
        }
        player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        try {
            player.addSource(mixer);
            for(int i=0;i < nvoices;i++) {
                mixer.addSource(loopbuffer[i]);
            }
        } catch(SinkIsFullException e) {
        }
        String[] names = new String[nvoices];
        double[] val = new double[nvoices];
        double[] min = new double[nvoices];
        double[] max = new double[nvoices];
        for(int i=0;i < nvoices;i++) {
            int k = i+1;
            names[i] = "Volume" + k +" ";
            val[i] = 1f;
            min[i] = 0;
            max[i] = 1f;
        }
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
                for(int i=0;i < nvoices;i++) {
                    mixer.setGain(i,0);
                }
            } else {
                jButton[0].setText ("Stop");
                isOn = true;
                //player.resetAGC();
                for(int i=0;i < nvoices;i++) {
                    mixer.setGain(i,(float)val[i]);
                }
            }
            break;
        }

    }
    
    protected void onSlider(int k) {
        mixer.setGain(k,(float)this.val[k]);
    }
    
}
