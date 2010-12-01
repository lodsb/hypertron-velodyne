import jass.render.*;
import jass.generators.*;
import java.awt.*;
import jass.utils.*;

/*



*/

public class VTDemo2Root implements Runnable {

    static final String aboutStr = "Select various vowels by clicking the buttons in the control panel. This will  generate a  particular vocal  tract shape  matching the  vowel and synthesize  the  sound of  exciting  such a  vocal  tract  shape with  a Rosenberg type glottal excitation. The  airway model is morphed in order to fit this  tube model.  Start the timeline in order  to see the airway change.   You can  also tweak  reflection  coefficients at  the lip  and glottis,  attenuation (damping)  and the  4 glottal  parameters  for the excitation. The formants window plots  the spectrum of the sound.  After you move  the sliders you need to  click the formants button  to see the update.\n The particular vowels implemented here are the six Russian vowels as described in \"Acoustics Theory of Speech Production\", Chapter 2.3, Gunnar Fant, 1970 ";
    
    static final int  nTubeSections=40;    
    static double[] tract = new double[nTubeSections]; // for presets
    static final double tubeLength=-1;
    String[] args = {".17","44100"};
    SourcePlayer player;
    Controller a_controlPanel;
    FormantsPlotter formantsPlotter;
    float srate;
    double multM=1; // experimentatlly determined multiplier for RightLoadedWebsterTube
    public String getAbout() {
        return aboutStr;
    }

    private boolean haltMe=false;
    
    public void detach() {
        halt();
        System.out.println("halt!!");
    }

    public void halt() {
        haltMe=true;
        player.stopPlaying();
        a_controlPanel.dispose();
        formantsPlotter.close();
    }
    
    public VTDemo2Root() {
        super();
        run();

    }
    
    public static void main(String[] args) {
        new VTDemo2Root();
    }
    
    public void run() {
        int bufferSize = 256;
        int bufferSizeJavaSound = 1024*8;

//        int nchannels = 1;
        try {
        } catch(Exception e) {
            System.out.println("File not found"+e);
        }
        if(args.length != 2) {
            System.out.println("Usage: java VTDemo2 .17 srate");
            return;
        }
        double tubeLength = Double.parseDouble(args[0]);
        srate = (float) Double.parseDouble(args[1]);
        // TubeModel will decide how many segments are needed and interpolate
        final TubeModel tm = new TubeModel(nTubeSections);
        tm.setLength(tubeLength);
        
        int nbuttons = 4 + 7 + 2;
        final int nAuxSliders = 5+4;
        int nSliders = nAuxSliders;
        String[] names = new String[nSliders];
        double[] val = new double[nSliders];
        double[] min = new double[nSliders];
        double[] max = new double[nSliders];
        names[0] = "lip M mult";
        val[0] = 1; min[0] = 0.001; max[0] = 5;
        names[1] = "lip d mult";
        val[1] = 1; min[1] = 0.05; max[1] = 1000000;
        names[2] = "unused";
        val[2] = .001; min[2] = 0.00001; max[2] = .02;
        names[3] = "unused";
        val[3] = 20; min[3] = 0.0; max[3] = 100;
        names[4] = "wall damp";
        val[4] = .01; min[4] = 0.0; max[4] = .1;
        names[5] = "pitch ";
        val[5] = 80; min[5] = 20; max[5] = 1000;
        names[6] = "openQuot ";
        val[6] = .5; min[6] = 0.001; max[6] = 1;
        names[7] = "speedQuot ";
        val[7] = 4; min[7] = 0.1; max[7] = 10;
        names[8] = "length ";
        val[8] = tubeLength; min[8] = tubeLength/10; max[8] = tubeLength*1.8;

        player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        double c= 340; // vel. of sound
        //        final HalfSampleDelayTubeFilter filter = new HalfSampleDelayTubeFilter(srate,tm,c);
        double minLen = .10;
        final RightLoadedWebsterTube tube = new RightLoadedWebsterTube(srate,tm,minLen);
        final RightLoadedWebsterTube tubeCopy = new RightLoadedWebsterTube(srate,tm,minLen);
        final FilterContainer filterContainer = new FilterContainer(srate,bufferSize,tube);
        final GlottalWave source = new GlottalWave(srate,bufferSize);
        final RandOut source2 = new RandOut(bufferSize);
        //final Impulse source2 = new Impulse(srate,bufferSize);
        //source2.setPeriod(1);
        try {
            filterContainer.addSource(source);
            player.addSource(filterContainer);
        } catch(Exception e) {}
        
        preset("a");
        for(int i=0;i<nTubeSections;i++) {
            tm.setRadius(i,tract[i]);
        }
        //tube.changeTubeModel();
        player.resetAGC();

        a_controlPanel = new Controller(new java.awt.Frame ("DemoFilter"),
                                        false,val.length,nbuttons) {
        	private static final long serialVersionUID = 1L;

                boolean muted=false;
                
                public void onButton(int k) {
                    switch(k) {
                    case 0: 
                        player.resetAGC();
                        tube.reset();
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
                        preset("a");
                        double tubeLen = .17;
                        tm.setLength(tubeLen);
                        val[8] = tubeLen;
                        val[0] = multM;
                        a_controlPanel.setSliders(val,min,max,names);
                        for(int i=0;i<nTubeSections;i++) {
                            tm.setRadius(i,tract[i]);
                        }
                        tube.multM = multM;
                        tubeCopy.multM = multM;
                        tube.changeTubeModel();
                        //tube.reset();
                        //player.resetAGC();
                        //updateFormantsPlot();
                    }
                        break;
                    case 5: {
                        preset("o");
                        double tubeLen = .185;
                        tm.setLength(tubeLen);
                        val[8] = tubeLen;
                        val[0] = multM;
                        a_controlPanel.setSliders(val,min,max,names);
                        for(int i=0;i<nTubeSections;i++) {
                            tm.setRadius(i,tract[i]);
                        }
                        tube.multM = multM;
                        tubeCopy.multM = multM;
                        tube.changeTubeModel();
                        //tube.reset();
                        //player.resetAGC();
                        //updateFormantsPlot();
                    }
                        break;
                    case 6: {
                        preset("u");
                        double tubeLen = .195;
                        tm.setLength(tubeLen);
                        val[8] = tubeLen;
                        val[0] = multM;
                        a_controlPanel.setSliders(val,min,max,names);
                        for(int i=0;i<nTubeSections;i++) {
                            tm.setRadius(i,tract[i]);
                        }
                        tube.multM = multM;
                        tubeCopy.multM = multM;
                        tube.changeTubeModel();
                        //tube.reset();
                        //player.resetAGC();
                        //updateFormantsPlot();
                    }
                        break;
                    case 7: {
                        preset("i_");
                        double tubeLen = .19;
                        tm.setLength(tubeLen);
                        val[8] = tubeLen;
                        val[0] = multM;
                        a_controlPanel.setSliders(val,min,max,names);
                        tm.setLength(tubeLen);
                        for(int i=0;i<nTubeSections;i++) {
                            tm.setRadius(i,tract[i]);
                        }
                        tube.multM = multM;
                        tubeCopy.multM = multM;
                        tube.changeTubeModel();
                        //tube.reset();
                        //player.resetAGC();
                        //updateFormantsPlot();
                    }
                        break;
                    case 8: {
                        preset("i");
                        double tubeLen = .165;
                        tm.setLength(tubeLen);
                        val[8] = tubeLen;
                        val[0] = multM;
                        a_controlPanel.setSliders(val,min,max,names);
                        for(int i=0;i<nTubeSections;i++) {
                            tm.setRadius(i,tract[i]);
                        }
                        tube.multM = multM;
                        tubeCopy.multM = multM;
                        tube.changeTubeModel();
                        //tube.reset();
                        //player.resetAGC();
                        //updateFormantsPlot();
                    }
                        break;
                    case 9: {
                        preset("e");
                        double tubeLen = .165;
                        tm.setLength(tubeLen);
                        val[8] = tubeLen;
                        val[0] = multM;
                        a_controlPanel.setSliders(val,min,max,names);
                        for(int i=0;i<nTubeSections;i++) {
                            tm.setRadius(i,tract[i]);
                        }
                        tube.multM = multM;
                        tubeCopy.multM = multM;
                        tube.changeTubeModel();
                        //tube.reset();
                        //player.resetAGC();
                        //updateFormantsPlot();
                    }
                        break;
                                            case 10: {
                        preset("-");
                        double tubeLen = .17;
                        tm.setLength(tubeLen);
                        val[8] = tubeLen;
                        val[0] = multM;
                        a_controlPanel.setSliders(val,min,max,names);
                        for(int i=0;i<nTubeSections;i++) {
                            tm.setRadius(i,tract[i]);
                        }
                        tube.multM = multM;
                        tubeCopy.multM = multM;
                        tube.changeTubeModel();
                        //tube.reset();
                        //player.resetAGC();
                        //updateFormantsPlot();
                    }
                        break;
                    case 11: { //plot formants
                        updateFormantsPlot();
                    }
                        break;
                    case 12: { //toggle lipmodel
                        tube.useLipModel = !tube.useLipModel;
                        System.out.println("useLipModel="+tube.useLipModel);
                    }
                        break;
                    }
                }

                private void updateFormantsPlot() {

                    tubeCopy.changeTubeModel();
                    tubeCopy.reset();
                    if(formantsPlotter == null) {
                        //System.out.println("CREATE formant plot");
                        formantsPlotter = new FormantsPlotter();
                    }
                    formantsPlotter.plotFormants(tubeCopy,srate);
                }
		                
                public void onSlider(int k) {
                    switch(k) {
                    case 0:
                        tube.multM = this.val[k];
                        tubeCopy.multM = this.val[k];
                        tube.changeTubeModel();
                        break;
                    case 1:
                        tube.multD = this.val[k];
                        tubeCopy.multD = this.val[k];
                        tube.changeTubeModel();
                        break;
                    case 2:
                        //tube.M2= this.val[k];
                        //tubeCopy.M2= this.val[k];
                        break;
                    case 3:
                        //tube.d2 = this.val[k];
                        //tubeCopy.d2 = this.val[k];
                        break;
                    case 4:
                        tube.dWall = this.val[k];
                        tubeCopy.dWall = this.val[k];
                        break;
                    case 5:
                        source.setFrequency((float)this.val[k]);
                        break;
                    case 6:
                        source.setOpenQuotient((float)this.val[k]);
                        break;
                    case 7:
                        source.setSpeedQuotient((float)this.val[k]);
                        break;
                    case 8:
                        tm.setLength((double)this.val[k]);
                        tube.changeTubeModel();
                        break;
                    }
                }
                
            };

        a_controlPanel.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.out.println("Close handler called");
                    player.stopPlaying();
                }
            });
	
        a_controlPanel.setSliders(val,min,max,names);
        a_controlPanel.setButtonNames (new String[] {"Reset","Save","Load","(Un)mute","[a]","[o]","[u]","[i-]","[i]","[e]","[-]","Formants","ToggleLipModel"});
        a_controlPanel.setVisible(true);
        a_controlPanel.onButton(nbuttons-1); // put up formants
        player.start();
        tube.reset();

        /*
        int sleepms = 10;
        double f0=80,fnow=f0;
        double dfmax = 1; 
        
        while(!haltMe) {
            try {
                Thread.sleep(sleepms);
                double df = 2*Math.random()-1;
                fnow += df*.4;
                if(fnow<f0-dfmax) {
                    fnow -= 2*df;
                } else if(fnow>f0+dfmax) {
                    fnow -= 2*df;
                }
                source.setFrequency((float)fnow);
            } catch(Exception e) {}

        }
        */

    }

    static double[] fantData_a =  new double[] {5, 5, 5, 5, 6.5,       8, 8, 8, 8, 8,
                                                8, 8, 8, 6.5, 5,       4, 3.2, 1.6, 2.6, 2.6,
                                                2, 1.6, 1.3, 1, .65,   .65, .65, 1, 1.6, 2.6,
                                                4, 1, 1.3, 1.6, 2.6};
    static double[] fantData_o =  new double[] {3.2,3.2,3.2,3.2,6.5,   13,13,16,13,10.5,
                                                10.5,8,8,6.5,6.5,       5,5,4,3.2,2,
                                                1.6,2.6,1.3,.65,.65,    1,1,1.3,1.6,2,
                                                3.2,4,5,5,1.3,          1.3,1.6,2.6};
    static double[] fantData_u =  new double[] {.65,.65,.32,.32,2,  5,10.5,13,13,13,
                                                13,10.5,8,6.5,5,    3.2,2.6,2,2,2,
                                                1.6,1.3,2,1.6,1,     1,1,1.3,1.6,3.2,
                                                5,8,8,10.5,10.5,    10.5,2,2,2.6,      2.6};
    static double[] fantData_i_ =  new double[] {6.5,6.5,2,6.5,8,   8,8,5,3.2,2.6,
                                                 2,2,1.6,1.3,1,     1,1.3,1.6,2.6,2,
                                                 4,5,6.5,6.5,8,     10.5,10.5,10.5,10.5,10.5,
                                                 13,13,10.5,10.5,6, 3.2,3.2,3.2,3.2};
    static double[] fantData_i =  new double[] {4,4,3.2,1.6,1.3,              1,.65,.65,.65,.65,
                                                .65,.65,.65,1.3,2.6,          4,6.5,8,8,10.5,
                                                10.5,10.5,10.5,10.5,10.5,     10.5,10.5,10.5,8,8,
                                                2,2,2.6,3.2};
    static double[] fantData_e =  new double[] {8,8,5,5,4,               2.6,2,2.6,2.6,3.2,
                                                4,4,4,5,5,               6.5,8,6.5,8,10.5,
                                                10.5,10.5,10.5,10.5,8,   8,6.5,6.5,6.5,6.5,
                                                1.3,1.6,2,2.6};
    static double[] fantData__ =  new double[] {5, 5, 5, 5, 5,       5, 5, 5, 5, 5,
                                                5, 5, 5, 5, 5,       5, 5, 5, 5, 5,
                                                5, 5, 5, 5, 5,   5, 5, 5, 5, 5,
                                                5, 5, 5, 5, 5};


    public void preset(String p) {
        double[] f_a=null;
        if (p=="a") {
            f_a = fantData_a;
            //multM = 1.72;
        }
		if (p=="o") {
            f_a = fantData_o;
            //multM = 2.18;
        }
        if (p=="u") {
            f_a = fantData_u;
            //multM = 4.2;
        }
        if (p=="i_") {
            f_a = fantData_i_;
            //multM = 3.79;
        }
        if (p=="i") {
            f_a = fantData_i;
            //multM = 0.74;
        }
        if (p=="e") {
            f_a = fantData_e;
            //multM = 0.527;
        }
        if (p=="-") {
            f_a = fantData__;
            //multM = 1;
        }
        // interpolate and invert Fant data
        double C = (f_a.length-1.)/(tract.length-1);
        for(int i=0;i<tract.length;i++) {
            double k = i*C;
            int ki = (int)k;
            double kfrac = k-ki;
            int i1 = ki;
            int i2 = i1+1;
            if(i2>f_a.length-1) {
                i2 = i1;
            }
            // radii in meters
            tract[tract.length-i-1] = Math.sqrt((f_a[i1]*(1-kfrac)+f_a[i2]*kfrac)/Math.PI)/100;
        }
    }
}



