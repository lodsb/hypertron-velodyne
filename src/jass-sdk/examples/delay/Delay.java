import jass.render.*;
import jass.engine.*;
import jass.generators.*;

/**
   Delay line with low-pass; a simple Karplus-Strong like string
   sound (though the lowpass is at the wrong place it sounds good to me)
   @author Kees van den Doel (kvdoel@cs.ubc.ca)
*/

// Rnd is source of pulses, goes to Mixer, with a feedback on itself, then to audio-out
public class Delay {

    public static void main (String args[]) throws SinkIsFullException {
        float srate = 44100.f;
        int bufferSize = 128*4;
        int bufferSizeJavaSound = 6*1024;
        /*
          [Rnd]o->[Mixer]o->[speaker]
                 ^---<---|
        */   
        Source rout = new Rnd(bufferSize);
        final Mixer mixer = new Mixer(bufferSize,2);
        final SourcePlayer player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        LowPass lp = new LowPass();
        FilterContainer lowpass = new FilterContainer(srate,bufferSize,lp);
        player.addSource(mixer);
        mixer.addSource(rout);
        mixer.addSource(lowpass);
        lowpass.addSource(mixer);
        mixer.setGain(0,1f);
        mixer.setGain(1,0f); // feedback
        // Add control panel
        boolean isModal = false;
        int nsliders = 1;
        Controller a_controlPanel = new Controller(new java.awt.Frame ("Delay"),
                                                   isModal,nsliders,1) {

            public void onButton (int k) {
                switch(k) {
                    case 0:
                    player.resetAGC();
                    break;
                } 
            }
            
            public void onSlider(int k) {
                switch(k) {
                    case 0:
                    mixer.setGain(1,(float)super.val[0]);
                    break;
                }
            }
        };
        String[] names = {"Feedback "};
        double[] val =   {0          };
        double[] min =   {0          };
        double[] max =   {1          };
        a_controlPanel.setSliders(val,min,max,names);
        a_controlPanel.setButtonNames (new String[] {"Reset"});
        a_controlPanel.setVisible(true);
        
        player.start();
    }

}
