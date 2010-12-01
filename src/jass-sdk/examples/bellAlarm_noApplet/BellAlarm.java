import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import java.util.*;
import java.awt.*;

public class BellAlarm extends Thread {

    public static void main (String args[]) throws Exception {
        double hrs = 9;
        double dt = 60*60 *hrs; // wakeup
      
        float srate = 44100.f;
        int bufferSize = 1024;
        int nRtAudioBuffers = 512/bufferSize;
        int bufferSizeJavaSound = 1024*8;

        if(args.length != 1) {
            System.out.println("Usage: java BellAlarm ../data/bell4.sy");
            return;
        }
        MicroTime mt = new MicroTime();
        double now = mt.getTime();
        double waket = now +dt;
        while(now<waket) {
            sleep(1000);
            now = mt.getTime();
        }
        final SourcePlayer player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        //player.setUseNativeSound(true);
        player.setNumRtAudioBuffersNative(nRtAudioBuffers);
        final ModalModel mm = new ModalModel(args[0]);
        final ModalModel mmOrg = new ModalModel(args[0]);
        final StickyModalObjectWithOneContact bell =
            new StickyModalObjectWithOneContact(mm,srate,bufferSize);
        float dur = .002f; // 2 ms
        int nsamples = (int)(srate * dur);
        float[] cosForce = new float[nsamples];
        for(int i=0;i<nsamples;i++) {
            cosForce[i] = (float)(.5*(1.-Math.cos(2*Math.PI*(i+1)/(1+nsamples))));
        }
        final OneShotBuffer force = new OneShotBuffer(srate,bufferSize,cosForce);
        bell.addSource(force);
        player.addSource(bell);
        int nModes0 = 30;
        bell.setNf(nModes0);
        float hardNess0 = 10f;
        force.setSpeed(hardNess0);
        force.setVolume(hardNess0);
        class HitFreq {
            double hitFreq = 1.0; // must wrap in final class for Controller to access
        };        
        final HitFreq hitFreq = new HitFreq();
        
        // Add control panel

        String[] names = {"Hardness   ",
                          "No. modes  ",
                          "Bell freq. ",
                          "Toll speed ",
                          "Warp modes "
        };
        double[] val =   {hardNess0,
                          nModes0,
                          1,
                          hitFreq.hitFreq,
                          0
        };
        double[] min =   {0.1,
                          1,
                          .25,
                          .1,
                          -.05
        };
        double[] max =   {hardNess0,
                          mm.f.length,
                          3,
                          1,
                          .05
        };
        int nbuttons = 4;
        Controller a_controlPanel = new Controller(new java.awt.Frame ("BellAlarm"),
                                                   false,val.length,nbuttons) {

                public void onButton(int k) {
                    switch(k) {
                    case 0:
                        player.resetAGC();
                        break;
                    case 1:
                        randWarp();
                        warp(this.val[4]);
                        bell.computeFilter();
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
                        break;
                    }
                    } 
                }
            
                public void onSlider(int k) {
                    switch(k) {
                    case 0:
                        force.setSpeed((float)this.val[0]);
                        force.setVolume((float)this.val[0]);
                        break;
                    case 1:
                        int nf = (int)this.val[1];
                        bell.setNf(nf);
                        break;
                    case 2:
                        bell.setFrequencyScale((float)this.val[2]);
                        break;
                    case 3:
                        hitFreq.hitFreq = this.val[3];
                        break;
                    case 4:
                        warp(this.val[4]);
                        bell.computeFilter();
                        break;
                    }
                }

                float[] w; // warp direction

                void randWarp() {
                    for(int i=0;i<w.length;i++) {
                        w[i] = (float)(2*Math.random()-1);
                    }
                }

                    {
                        w = new float[mm.f.length];
                        randWarp();
                    }
            
                public void warp(double x) {
                    int nf = w.length;
                    for(int i=0;i<nf;i++) {
                        mm.f[i] = (float)(mmOrg.f[i]*(1 + w[i]*x ));
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
        
        a_controlPanel.setButtonNames (new String[] {"Reset","Generate Warp Direction","Save","Load"});
        a_controlPanel.setVisible(true);
        
        player.start();

        while(true) {
            force.hit();
            sleep((int)(1000/hitFreq.hitFreq));
        }
    }

}


