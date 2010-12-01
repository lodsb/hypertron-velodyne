import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.util.*;
import java.awt.*;

/**
   Pan white noise
 */

public class Stereo extends Thread {
    
    public static void main (String args[]) throws Exception {
        float srate = 44100.f;
        int bufferSize = 64;
        int bufferSizeJavaSound = 1024*16;
        int nBuffersRtAudio = 1024/bufferSize;
        boolean nativeSound = false;
        int nchannels = 2;
        int nSources = 1;
        if(args.length != 0) {
            System.out.println("Usage: java Stereo");
            return;
        }
        final SourcePlayer player;
        player = new SourcePlayer(bufferSize*nchannels,bufferSizeJavaSound,srate);
        player.setNChannels(nchannels);
        player.setUseNativeSound(nativeSound);
        player.setNumRtAudioBuffersNative(nBuffersRtAudio);

        final RandOut source = new RandOut(bufferSize);
        //final Sine source = new Sine(srate,bufferSize);
        //source.setFrequency(440);
        final Mixer mixer = new Mixer(bufferSize*nchannels,nSources,nchannels);
        mixer.addSource(source);

        player.addSource(mixer);
        double pan = .5;
        // Add control panel

        String[] names = {"pan "};
        double[] val =   {pan};
        double[] min =   {0};
        double[] max =   {1};
        int nbuttons = 1;
        Controller a_controlPanel = new Controller(new java.awt.Frame ("Stereo"),
                                                   false,val.length,nbuttons) {

            public void onButton(int k) {
                switch(k) {
                    case 0:
                    player.resetAGC();
                    mixer.setGain(0,1);
                    break;
                } 
            }
            
            public void onSlider(int k) {
                switch(k) {
                    case 0:
                    mixer.setPan(0,(float)this.val[k]);
                    break;
                }
            }
            
        };

        a_controlPanel.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("Close handler called");
                player.stopPlaying();
                try{
                    sleep(500);
                } catch(Exception e3) {
                }
                System.exit(0);
            }
	    });
	
        a_controlPanel.setSliders(val,min,max,names);
        a_controlPanel.setButtonNames (new String[] {"Reset"});
        a_controlPanel.setVisible(true);

        player.start();


    }

}


