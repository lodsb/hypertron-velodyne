import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.net.*;
/**
   @author Kees van den Doel (kvdoel@cs.ubc.ca)
*/
public class GrainsApplet extends AppletController {
    GranularConstantLoopBuffer grainBuffer;
    SourcePlayer player;
    boolean isOn = false;
    String wavfile;
    int nGrains = 10;
    float fadeTime = .03f; // will smooth transitions here
    float linearBias = 0;
    
    public void setNSliders() {
        nsliders = 1;
    }
   
    public void setNButtons() {
        nbuttons = 1;
    }
    
    public void init() {
        super.init();
        wavfile = getParameter("wavfile");
        nGrains = Integer.parseInt(getParameter("ngrains"));
        fadeTime = new Float(getParameter("fadetime")).floatValue();
        linearBias = new Float(getParameter("linearbias")).floatValue();
    }
   
    public void start() {
        float srate = 44100.f;
        int bufferSize = 128;
        int bufferSizeJavaSound = 8*1024;
        URL codebase = getCodeBase();
        URL wavurl = null;
        try {
            wavurl = new URL(codebase,wavfile);
        } catch(MalformedURLException e) {
            System.out.println(e+" Malformed URL: " +codebase+" "+ wavfile);
        }
        grainBuffer = new GranularConstantLoopBuffer(srate,bufferSize, wavurl);
        grainBuffer.setFadeTime(fadeTime);
        grainBuffer.initRandom(nGrains,linearBias);
        player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        try {
            player.addSource(grainBuffer);
        } catch(SinkIsFullException e) {
        }

        String[] names = {"Linearity "};
        double[] val =   {linearBias  };
        double[] min =   {0           };
        double[] max =   {1           };
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
                grainBuffer.setVolume(0);
            } else {
                jButton[0].setText ("Stop");
                isOn = true;
                grainBuffer.setVolume(1f);
                //player.resetAGC();
            }
            break;
        }

    }
    
    protected void onSlider(int k) {
        switch(k) {
        case 0:
            grainBuffer.setLinearBias((float)this.val[0]);
            grainBuffer.setTransitionMatrix();
            break;
        }
    }
    
}
