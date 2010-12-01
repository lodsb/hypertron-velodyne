import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.util.*;
import java.awt.*;

/**
   Listen to white noise filtered with a Butterworth filter
 */

public class DemoFilter extends Thread {
    static boolean ascii = true;
    static boolean toFile = false;
    
    public static void main (String args[]) throws Exception {
        float srate = 44100.f;
        int bufferSize = 64;
        int bufferSizeJavaSound = 1024*8;
        int nBuffersRtAudio = 512/bufferSize;
        boolean nativeSound = false;
        int nchannels = 1;
        if(args.length != 2) {
            System.out.println("Usage: java DemoFilter ../data/white.wav ../data/sine1.sy");
            return;
        }
        final SourcePlayer player;
        if(toFile) {
            player = new SourcePlayer(bufferSize,srate,"tmp.m");
        } else {
            player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        }
        player.setUseNativeSound(nativeSound);
        player.setNumRtAudioBuffersNative(nBuffersRtAudio);
        player.setNChannels(nchannels);
                    
        
        final Butter2LowFilter filter = new Butter2LowFilter(srate);
        final FilterContainer filterContainer = new FilterContainer(srate,bufferSize,filter);
        final RandOut source = new RandOut(bufferSize);
        final ModalModel mm = new ModalModel(args[1]);
        final ModalObjectWithOneContact mob =
            new ModalObjectWithOneContact(mm,srate,bufferSize);
        
        filterContainer.addSource(source);
        mob.addSource(filterContainer);
        player.addSource(mob);
        double cutoffFreq = 50;
        // Add control panel

        String[] names = {"Cutoff Freq. ", "f1 ", "d1 "};
        double[] val =   {cutoffFreq,       1,     1};
        double[] min =   {cutoffFreq,      .1,    .1};
        double[] max =   {4000,          10,     1000};
        int nbuttons = 1;
        Controller a_controlPanel = new Controller(new java.awt.Frame ("DemoFilter"),
                                                   false,val.length,nbuttons) {

            public void onButton(int k) {
                switch(k) {
                    case 0:
                    player.resetAGC();
                    filter.reset();
                    break;
                } 
            }
            
            public void onSlider(int k) {
                switch(k) {
                    case 0:
                    filter.setCutoffFrequency((float)this.val[k]);
                    break;
                    case 1:
                    mob.setFrequencyScale((float)this.val[k]);
                    break;
                    case 2:
                    mob.setDamping((float)this.val[k]);
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

        if(toFile) {
            player.AGCOff();
            // render loop
            long lt = 0;
            int twait = 64;
            try {
                while(true) {
                    lt += twait;
                    double realTime = lt/1000.;
                    if(ascii) {
                        player.advanceTime(realTime,ascii);
                    } else {
                        player.advanceTime(realTime);
                    }
                    sleep(twait);
                }
            } catch(Exception e) {
                System.out.println(e);
            }
        } else {
          player.start();
        }

    }

}


