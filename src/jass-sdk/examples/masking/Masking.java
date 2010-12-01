import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.util.*;
import java.awt.*;



public class Masking extends Thread {

    static float dbMax=0,dbMin=-100,lMax = -50,lMin = -100;
    static float a_n,l_n=lMin;
    static float[] f;
    static int nfreq;
    static float[] a;
    static float[] db;

    static String sine50Hz44100 = "../data/sin20ms.wav";
    static float baseFreqWavFile = 50;
    static float srate = 44100.f;
    static int nButtons = 4;
    
    static SourcePlayer player;
    static LoopBuffer[] tone;
    static RandOut randOut;
    static Mixer mixer;
    static int nSources;

    // Convert db to amp for tone
    public static double db2a(double db) {
        return Math.sqrt(2)*Math.exp(db*Math.log(10)/20);
    }
    
    // Convert density level to amp for white noise
    public static double level2a(double lev) {
        return Math.sqrt(3*srate/2)*Math.exp(lev*Math.log(10)/20);
    }
    
    public static void main (String args[]) throws Exception {
        if(args.length != 1) {
            System.out.println("Usage: java Masking nfrequencies");
            return;
        }
        nfreq = Integer.parseInt(args[0]);
        int nsliders = nfreq*2+1;

        nSources = 1+nfreq; // nfreq tones 1 white noise
        a = new float[nfreq];
        db = new float[nfreq];
        f = new float[nfreq];
        for(int i=0;i<nfreq;i++) {
            f[i] = 200 + i*20;
        }
	
        db[0] = dbMax;
        for(int i=1;i<nfreq;i++) {
            db[i] = dbMin;
        }

        int bufferSize = 1024;
        int bufferSizeJavaSound = 8*1024;
        tone = new LoopBuffer[nfreq];
        for(int i=0;i<nfreq;i++) {
            tone[i] = new LoopBuffer(srate,bufferSize,sine50Hz44100);
        }
        randOut = new RandOut(bufferSize);
        mixer = new Mixer(bufferSize,nSources);
        player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        try {
            player.addSource(mixer);
            for(int i=0;i<nfreq;i++) {
                mixer.addSource(tone[i]);
            }
            mixer.addSource(randOut);
        } catch(Exception e) {
        }
        // Add control panel

        String[] names = new String[nsliders];
        double[] val =   new double[nsliders];
        double[] min =   new double[nsliders];
        double[] max =   new double[nsliders];
        for(int i=0;i<nsliders-2;i+=2) {
            names[i] = "f"+(i/2)+" ";
            val[i] = f[i/2];
            min[i] = 50;
            max[i] = srate/2;
            names[i+1] = "dB"+(i/2)+" ";
            val[i+1] = db[i/2];
            min[i+1] = dbMin;
            max[i+1] = dbMax;
        }
        names[nsliders-1] = "Noise density level (db/Hz) ";
        val[nsliders-1] = l_n;
        min[nsliders-1] = lMin;
        max[nsliders-1] = lMax;

        String[] button_names = {"Reset","Mute","Save","Load"};
                    
        for(int i=0;i<nfreq;i++) {
            //tone[i].setSpeed(f[i]/baseFreqWavFile);
            a[i] = (float)db2a(db[i]);
            //mixer.setGain(i,a[i]);
        }
        a_n = (float)level2a(l_n);
        //mixer.setGain(nfreq,a_n);
        
        Controller a_controlPanel =
            new Controller(new java.awt.Frame ("Masking"), false,val.length,nButtons) {
                public void onButton(int k) {
                    switch(k) {
                        case 0:
                        player.resetAGC();
                        break;
                        case 1:
                        boolean muted = player.getMute();
                        player.setMute(!muted);
                        if(muted) {
                            jButton[1].setText ("Mute");
                        } else {
                            jButton[1].setText ("Unmute");
                        }
                        break;
                        case 2: {
                            FileDialog fd = new FileDialog(new Frame(),"Save");
                            fd.setMode(FileDialog.SAVE);
                            fd.setVisible(true);
                            saveToFile(fd.getFile());
                        }
                        break;
                        case 3: {
                            FileDialog fd = new FileDialog(new Frame(),"Load");
                            fd.setMode(FileDialog.LOAD);
                            fd.setVisible(true);
                            loadFromFile(fd.getFile());
                        }
                        break;
                    } 
                }
            
                public void onSlider(int k) {
                    if(k==this.nsliders-1) {
                        l_n = (float)this.val[k];
                        a_n = (float)level2a(l_n);
                        mixer.setGain(nfreq,a_n);
                    } else if(k%2 == 0) {
                        f[k/2] = (float)this.val[k];
                        tone[k/2].setSpeed(f[k/2]/baseFreqWavFile);
                    } else if(k%2 == 1) {
                        db[(k-1)/2] = (float)this.val[k];
                        a[(k-1)/2] = (float)db2a(db[(k-1)/2]);
                        mixer.setGain((k-1)/2,a[(k-1)/2]);
                    }
                }

            };
        
        a_controlPanel.setSliders(val,min,max,names);
        a_controlPanel.setVisible(true);
        
        player.start();
    }
}



