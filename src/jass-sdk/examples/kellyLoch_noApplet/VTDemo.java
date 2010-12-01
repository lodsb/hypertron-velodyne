import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import jass.utils.*;
import java.util.*;
import java.awt.*;

/**
   Filter something through a Tube filter
 */
public class VTDemo  extends Thread {

    public static void main (String args[]) throws Exception {
        float srate = 44100.f;
        int bufferSize = 16;
        int bufferSizeJavaSound = 1024*4;
        int nchannels = 1;
        int nTubeSections;
        if(args.length != 3) {
            System.out.println("Usage: java VTDemo ncontrolpoints length srate");
            return;
        }
        
        nTubeSections = Integer.parseInt(args[0]); // only for control
        double tubeLength = Double.parseDouble(args[1]);
        srate = (float) Double.parseDouble(args[2]);
        // TubeModel will decide how many segments are needed and interpolate
        final TubeModel tm = new TubeModel(nTubeSections);
        tm.setLength(tubeLength);
        
        int nbuttons = 4;
        final int nAuxSliders = 4+3;
        int nSliders = nTubeSections+nAuxSliders;
        String[] names = new String[nSliders];
        double[] val = new double[nSliders];
        double[] min = new double[nSliders];
        double[] max = new double[nSliders];
        names[0] = "glottal refl. ";
        val[0] = .85; min[0] = 0; max[0] = .999;
        names[1] = "lip refl. ";
        val[1] = .99; min[1] = 0; max[1] = .999;
        names[2] = "damping ";
        val[2] = 1.0; min[2] = 0; max[2] = 1.0;
        names[3] = "pitch ";
        val[3] = 80; min[3] = 20; max[3] = 1000;

        names[4] = "openQuot ";
        val[4] = .5; min[4] = 0.001; max[4] = 1;
        names[5] = "speedQuot ";
        val[5] = 4; min[5] = 0.1; max[5] = 10;
        names[6] = "length ";
        val[6] = tubeLength; min[6] = tubeLength/10; max[6] = tubeLength*1.8;
        
        double minA = 0;
        double maxA = 20;
        for(int k=nAuxSliders;k<nSliders;k++) {
            names[k] = "R("+new Integer(k-nAuxSliders).toString() + ") ";
            val[k] = 5;
            min[k] = minA;
            max[k] = maxA;
            double r=Math.sqrt(val[k]/Math.PI);
            tm.setRadius(k-nAuxSliders,r);
        }
        final SourcePlayer player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        player.setUseNativeSound(false);
        player.setNumRtAudioBuffersNative(4);
        double c= 340; // vel. of sound
        final HalfSampleDelayTubeFilter filter = new HalfSampleDelayTubeFilter(srate,tm,c);
        final FilterContainer filterContainer = new FilterContainer(srate,bufferSize,filter);
        final GlottalWave source = new GlottalWave(srate,bufferSize);
        filterContainer.addSource(source);
        player.addSource(filterContainer);
        filter.changeTubeModel();

        Controller a_controlPanel = new Controller(new java.awt.Frame ("DemoFilter"),
                                                   false,val.length,nbuttons) {
                boolean muted=false;
                
            public void onButton(int k) {
                switch(k) {
                case 0:
                    player.resetAGC();
                    filter.resetFilter();
                    break;
                case 1: {
                    FileDialog fd = new FileDialog(new Frame(),"Save");
                    fd.setMode(FileDialog.SAVE);
                    fd.setVisible(true);
                    saveToFile(fd.getFile());
                }
                break;
                case 2: {
                    FileDialog fd = new FileDialog(new Frame(),"Load");
                    fd.setMode(FileDialog.LOAD);
                    fd.setVisible(true);
                    loadFromFile(fd.getFile());
                }
                break;
                case 3:
                    muted = !muted;
                    player.setMute(muted);
                    player.resetAGC();
                break;
                }
            }
            
            public void onSlider(int k) {
                switch(k) {
                case 0:
                    filter.setGlottalReflectionCoeff(this.val[k]);
                    break;
                case 1:
                    filter.setLipReflectionCoeff(this.val[k]);
                    break;
                case 2:
                    filter.setDampingCoeff(this.val[k]);
                    break;
                case 3:
                    source.setFrequency((float)this.val[k]);
                    break;
                case 4:
                    source.setOpenQuotient((float)this.val[k]);
                    break;
                case 5:
                    source.setSpeedQuotient((float)this.val[k]);
                    break;
                case 6:
                    tm.setLength((double)this.val[k]);
                    filter.changeTubeModel();
                    break;
                default:
                    double r=Math.sqrt(val[k]/Math.PI);
                    tm.setRadius(k-nAuxSliders,r);
                    filter.changeTubeModel();
                    break;
                }
            }
            
        };

        a_controlPanel.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("Close handler called");
                player.stopPlaying();
                try{
                    //sleep(500);
                } catch(Exception e3) {
                }
                System.exit(0);
            }
	    });
	
        a_controlPanel.setSliders(val,min,max,names);
        a_controlPanel.setButtonNames (new String[] {"Reset","Save","Load","(Un)mute"});
        a_controlPanel.setVisible(true);
        player.start();
        /*
        int sleepms = 50;
        double deltaa=.1;
        while(true) {
            sleep(sleepms);
            for(int i=0;i<nTubeSections;i++) {
                double da = deltaa*(2*Math.random()-1);
                double r0 = tm.getRadius(i);
                double ar0 = Math.PI*r0*r0;
                double ar1 = ar0 + da;
                if(ar1<minA) {
                    ar1 = ar0 - da;
                } else if(ar1>maxA) {
                    ar1 = ar0 - da;
                }
                val[i+nAuxSliders] = ar1;
            }
            a_controlPanel.setSliders(val,min,max,names);
            filter.changeTubeModel();
        }
        */
    }
    
}
