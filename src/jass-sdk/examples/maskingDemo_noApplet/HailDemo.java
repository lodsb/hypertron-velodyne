import java.io.*;
import java.awt.*;

import jass.render.*;
import jass.engine.*;
import jass.generators.*;

/**
   Render to raw 16 bit PCM file (or ascii) tmp.raw as long as not interrupted or to real-time audio
*/
public class HailDemo extends Thread {

    public static void main(String[] args) 
        throws InterruptedException, SinkIsFullException, FileNotFoundException {
        int bufferSize = 1024;
        int bufferSizeJavaSound = 1024*16;
        int nFramesToSkip = 2;
        float srate = 22050;
        int twait = 50;
        boolean ascii = false;
        boolean toFile = false;
        final int nObjects = 9;
        int nf=10000; // set to 10000 to do all modes
        // int np=1;
        //float fmin=100,fmax=6000,mmcmin=.001f,mmcmax=.002f,amin=0,amax=1;
        float probExponent0 = 10;
        float eventDensity0 = .0001f;
        // renderer
        final SourcePlayer sp1;
        if(toFile) {
            sp1 = new SourcePlayer(bufferSize,srate,"tmp.raw");
        } else {
            sp1 = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        }
        // sources
        final RandPulses[] af = new RandPulses[nObjects];
        for(int i=0;i<nObjects;i++) {
            af[i] = new RandPulses(bufferSize);
        }

        final ModalModel[] mm = new ModalModel[nObjects];
        // volumes for all objects, depends on point of listening, set to defaults
        float[] ascale = new float[]{1,1,1,1,1,1,1,1,1};
        
        mm[0] = new ModalModel("../data/rectplate1.2_1.14.syenSortp_5.sy");
        mm[1] = new ModalModel("../data/rectplate1.4_1.14.syenSortp_5.sy");
        mm[2] = new ModalModel("../data/rectplate2_1.14.syenSortp_5.sy");
        mm[3] = new ModalModel("../data/rectplate3_1.14.syenSortp_5.sy");
        mm[4] = new ModalModel("../data/metalobject1.syenSortp_5.sy");
        mm[5] = new ModalModel("../data/metalobject2.syenSortp_5.sy");
        mm[6] = new ModalModel("../data/metalobject3.syenSortp_5.sy");
        mm[7] = new ModalModel("../data/woodentable1.syenSortp_5.sy");
        mm[8] = new ModalModel("../data/timecapsule.syensortp_5.sy");
        /*
        for(int i=0;i<nObjects;i++) {
            mm[i] = new RandomModalModel(
                200,1,50f,10000f,.001f,.01f,.0001f,1f);
        }
        */
        for(int i=0;i<nObjects;i++) {
            mm[i].ascale = ascale[i];
        }

        final QuenchableModalObjectWithOneContact[] mo = new QuenchableModalObjectWithOneContact[nObjects];
        for(int i=0;i<nObjects;i++) {
            mo[i] = new QuenchableModalObjectWithOneContact(mm[i],srate,bufferSize);
        }

       final ModalQuencher modalQuencher = new  ModalQuencher(bufferSize,nFramesToSkip);
       modalQuencher.setFramesToSkip(nFramesToSkip);
       
       // add modal objects to quencher
       for(int i=0;i<nObjects;i++) {
           modalQuencher.addModalObject(mo[i]);
       }
       
       // connect sources to quencher
       for(int i=0;i<nObjects;i++) {
            modalQuencher.addSource(af[i]);
        }

       // initialize quencher; an add no more sources or modalObjects now
       modalQuencher.init();
       
        // connect sources to objects
        for(int i=0;i<nObjects;i++) {
            mo[i].addSource(af[i]);
        }
        // connect objects to renderer, quencher first
        sp1.addSource(modalQuencher);
        for(int i=0;i<nObjects;i++) {
            sp1.addSource(mo[i]);
       }

        // Begin control panel code
        boolean isModal = false;
        final int nNonGainSliders = 5;
        int nsliders = nNonGainSliders+nObjects;
        int nbuttons = 4;
        Controller a_controlPanel = new Controller(new java.awt.Frame ("Hail"),
                                                   isModal,nsliders,nbuttons) {
                                                       
            public void onButton(int k) {
                switch(k) {
                    case 0:
                    sp1.resetAGC();
                    modalQuencher.resetLevel();
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
                         applySliders();
                        break;
                    }
                    case 3:
                    int nKilled = modalQuencher.getNKilledModes();
                    int totalModes = modalQuencher.getTotalModes();
                    System.out.println("killed = "+nKilled +" remaining = "+ (totalModes-nKilled)+" out of "+totalModes);
                    break;
                } 
            }

            private void applySliders() {
                for(int i=0;i<this.nsliders;i++) {
                    onSlider(i);
                }
            }
            
            public void onSlider(int k) {
                switch(k) {
                    case 0:
                    for(int i=0;i<nObjects;i++) {
                        af[i].setProbabilityPerSample((float)super.val[0]);
                    }
                    break;
                    case 1:
                    for(int i=0;i<nObjects;i++) {
                        af[i].setProbabilityDistributionExponent((float)super.val[1]);
                    }
                    break;
                    case 2:
                    int nfToUse = (int)super.val[2];
                    if(nfToUse < 10000) {
                        for(int i=0;i<nObjects;i++) {
                            if(mm[i].nf > nfToUse) {
                                mm[i].nfUsed = nfToUse;
                            }
                        }
                    }
                    break;
                    case 3:
                    float dbLevel = (float)super.val[3];
                    modalQuencher.setDbLevelLoudestMode(dbLevel);
                    break;
                    case 4:
                    float av = (float)super.val[4];
                    modalQuencher.setAv(av);
                    break;
                    default:
                    int objNumber = k-nNonGainSliders;
                    mo[objNumber].setGain((float)super.val[k]);
                    break;
                }
            }
        };
        // names, values, and ranges of sliders
        String[] names = {"HailDensity ",
                          "MassDistributionExponent ",
                          "#modes ",
                          "dB level ",
                          "maskThreshold ",
                          "gain0 ",
                          "gain1 ",
                          "gain2 ",
                          "gain3 ",
                          "gain4 ",
                          "gain5 ",
                          "gain6 ",
                          "gain7 ",
                          "gain8 "
                          
        };
        double[] val =   {eventDensity0,
                          probExponent0,
                          nf,
                          60,
                          20,
                          1,
                          1,
                          1,
                          1,
                          1,
                          1,
                          1,
                          1,
                          1
        };
        double[] min =   {0,
                          0,
                          1,
                          10,
                          0,
                          0.00001,
                          0.00001,
                          0.00001,
                          0.00001,
                          0.00001,
                          0.00001,
                          0.00001,
                          0.00001,
                          0.00001
        };
        double[] max =   {.1,
                          1000,
                          10000,
                          140,
                          100,
                          1,
                          1,
                          1,
                          1,
                          1,
                          1,
                          1,
                          1,
                          1
        };
        
        a_controlPanel.setSliders(val,min,max,names);
        a_controlPanel.setButtonNames(new String[] {"Reset","Save","Load","Debug"});
        a_controlPanel.setVisible(true);
        for(int i=0;i<nsliders;i++) {
            a_controlPanel.onSlider(i);
        }

        // End control panel code
        
        long lt = 0;
        float v=1;
        if(toFile) {
            try {
                while(true) {
                    lt += twait;
                    double realTime = lt/1000.;
                    if(ascii) {
                        sp1.advanceTime(realTime,ascii);
                    } else {
                        sp1.advanceTime(realTime);
                    }
                    sleep(twait);
                }
            } catch(Exception e) {
                System.out.println("RenderToFile"+e);
            }
        } else {
            sp1.run();
        }
        
    }
}



