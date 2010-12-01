import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import jass.utils.*;
import java.util.*;
import java.awt.*;

/**
   Filter something through a Kelly-lochbaum filter
 */

public class VTDemo2  extends Thread {
    
    static final int  nTubeSections=8;    
    static double[] tract = new double[nTubeSections]; // for presets
    static final double tubeLength=-1;

    
    public static void main (String args[]) throws Exception {
        float srate = 44100.f;
        int bufferSize = 16;
        int bufferSizeJavaSound = 1024*4;
        int nchannels = 1;

        if(args.length != 2) {
            System.out.println("Usage: java VTDemo2 .17 srate");
            return;
        }
        double tubeLength = Double.parseDouble(args[0]);
        srate = (float) Double.parseDouble(args[1]);
        // TubeModel will decide how many segments are needed and interpolate
        final TubeModel tm = new TubeModel(nTubeSections);
        tm.setLength(tubeLength);
        
        int nbuttons = 4 + 5;
        final int nAuxSliders = 3+4;
        int nSliders = nAuxSliders;
        String[] names = new String[nSliders];
        double[] val = new double[nSliders];
        double[] min = new double[nSliders];
        double[] max = new double[nSliders];
        names[0] = "glottal refl. ";
        val[0] = .5; min[0] = 0; max[0] = .99;
        names[1] = "lip refl. ";
        val[1] = .99; min[1] = 0; max[1] = .99;
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

        final SourcePlayer player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        double c= 340; // vel. of sound
        final HalfSampleDelayTubeFilter filter = new HalfSampleDelayTubeFilter(srate,tm,c);
        final FilterContainer filterContainer = new FilterContainer(srate,bufferSize,filter);
        final GlottalWave source = new GlottalWave(srate,bufferSize);
        filterContainer.addSource(source);
        player.addSource(filterContainer);
        preset("AH");
        for(int i=0;i<nTubeSections;i++) {
            tm.setRadius(i,tract[i]);
        }
        filter.changeTubeModel();
        player.resetAGC();

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
                case 3: {
                    muted = !muted;
                    player.setMute(muted);
                    player.resetAGC();
                }
                break;
                case 4: {
                    preset("AH");
                    for(int i=0;i<nTubeSections;i++) {
                        tm.setRadius(i,tract[i]);
                    }
                    filter.changeTubeModel();
                    filter.resetFilter();
                    player.resetAGC();
                }
                break;
                case 5: {
                    preset("EE");
                    for(int i=0;i<nTubeSections;i++) {
                        tm.setRadius(i,tract[i]);
                    }
                    filter.changeTubeModel();
                    filter.resetFilter();
                    player.resetAGC();
                }
                break;
                case 6: {
                    preset("EH");
                    for(int i=0;i<nTubeSections;i++) {
                        tm.setRadius(i,tract[i]);
                    }
                    filter.changeTubeModel();
                    filter.resetFilter();
                    player.resetAGC();
                }
                break;
                case 7: {
                    preset("OH");
                    for(int i=0;i<nTubeSections;i++) {
                        tm.setRadius(i,tract[i]);
                    }
                    filter.changeTubeModel();
                    filter.resetFilter();
                    player.resetAGC();
                }
                break;
                case 8: {
                    preset("OO");
                    for(int i=0;i<nTubeSections;i++) {
                        tm.setRadius(i,tract[i]);
                    }
                    filter.changeTubeModel();
                    filter.resetFilter();
                    player.resetAGC();
                }
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
        a_controlPanel.setButtonNames (new String[] {"Reset","Save","Load","(Un)mute","AH","EE","EH","OH","OO"});
        a_controlPanel.setVisible(true);
        player.start();

    }
    
    public static void preset(String p) {
        tract[0]=0;
		if (p=="AH") {tract[8-1]=0.56;tract[7-1]=0.68;tract[6-1]=0.68;tract[5-1]=0.48;tract[4-1]=0.32;tract[3-1]=0.16;tract[2-1]=0.36;tract[1-1]=0.32;}
		if (p=="EE") {tract[8-1]=0.44;tract[7-1]=0.20;tract[6-1]=0.16;tract[5-1]=0.36;tract[4-1]=0.64;tract[3-1]=0.80;tract[2-1]=0.72;tract[1-1]=0.36;}
		if (p=="EH") {tract[8-1]=0.44;tract[7-1]=0.28;tract[6-1]=0.40;tract[5-1]=0.56;tract[4-1]=0.68;tract[3-1]=0.72;tract[2-1]=0.52;tract[1-1]=0.28;}
		if (p=="OH") {tract[8-1]=0.16;tract[7-1]=0.72;tract[6-1]=0.48;tract[5-1]=0.40;tract[4-1]=0.24;tract[3-1]=0.20;tract[2-1]=0.40;tract[1-1]=0.28;}
		if (p=="OO") {tract[8-1]=0.12;tract[7-1]=0.68;tract[6-1]=0.48;tract[5-1]=0.32;tract[4-1]=0.28;tract[3-1]=0.32;tract[2-1]=0.60;tract[1-1]=0.32;}
    }
}



