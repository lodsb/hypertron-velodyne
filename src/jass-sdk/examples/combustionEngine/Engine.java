import jass.render.*;
import jass.engine.*;
import jass.generators.*;

/**
   Combustion engine model
   @author Kees van den Doel (kvdoel@cs.ubc.ca)
*/

public class Engine {

    public static void main (String args[]) throws SinkIsFullException, java.io.FileNotFoundException {
        float srate = 44100.f;
        int bufferSize = 128;
        int nInputs=4;
        int bufferSizeJavaSound = 8*1024;

        if(args.length != 1) {
            System.out.println("Usage: java Engine ../data/stick.sy ");
            return;
        }
        final Mixer mixer = new Mixer(bufferSize,nInputs);
        //final ErraticLoopBuffer cylinders = new ErraticLoopBuffer(srate,bufferSize,args[1]);
        final LoopBuffer intake = new LoopBuffer(srate,bufferSize,"../data/fourStrokeIntake.wav");
        final LoopBuffer combustion = new LoopBuffer(srate,bufferSize,"../data/fourStrokeCombustion.wav");
        final LoopBuffer exhaust = new LoopBuffer(srate,bufferSize,"../data/fourStrokeExhaust.wav");
        final LoopBuffer fan = new LoopBuffer(srate,bufferSize,"../data/neytone2.wav");
        mixer.addSource(intake);
        mixer.addSource(combustion);
        mixer.addSource(exhaust);
        mixer.addSource(fan);
        for(int i=0;i<nInputs;i++) {
            mixer.setGain(i,1);
        }
        final SourcePlayer player = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        final ModalModel mm = new ModalModel(args[0]);
        final ModalObjectWithOneContact engineResonances =
            new ModalObjectWithOneContact(mm,srate,bufferSize);
        player.addSource(engineResonances);
        engineResonances.addSource(mixer);


        // Add control panel
        boolean isModal = false;
        int nsliders = 8;
        Controller a_controlPanel = new Controller(new java.awt.Frame ("Engine"),
                                                   isModal,nsliders,1) {

            public void onButton(int k) {
                switch(k) {
                    case 0:
                    player.resetAGC();
                    //mm.fscale = .1f;
                    //mm.dscale = .001f;
                    //engineResonances.computeFilter();
                    break;
                } 
            }
            
            public void onSlider(int k) {
                switch(k) {
                    case 0:
                        intake.setSpeed((float)super.val[0]);
                        combustion.setSpeed((float)super.val[0]);
                        exhaust.setSpeed((float)super.val[0]);
                        fan.setSpeed((float)super.val[0]/16);
                    break;
                    case 1:
                        //cylinders.setMisfireProb((float)super.val[1]);
                    break;
                case 2:
                    mixer.setGain(0,(float)super.val[2]);
                    break;
                case 3:
                    mixer.setGain(1,(float)super.val[3]);
                    break;
                case 4:
                    mixer.setGain(2,(float)super.val[4]);
                    break;
                case 5:
                    mm.fscale = (float)super.val[5];
                    engineResonances.computeFilter();
                    break;
                case 6:
                    mm.dscale = (float)super.val[6];
                    engineResonances.computeFilter();
                    break;
                case 7:
                    mixer.setGain(3,(float)super.val[7]);
                    break;
                }
            }
        };
        
        float speed0 = 3f;
        String[] names = {"Speed ", "Beatupness ","Intake ","Combustion ","Exhaust ","Freq. ","Damping ","Fan "};
        double[] val =   {speed0,   0            ,.3       ,.3           ,.3        ,.15     , 2        ,.4    };
        double[] min =   {speed0,   0            ,0        ,0            ,0         ,.03     ,.01       ,0     };
        double[] max =   {30,       .1           ,1        ,1            ,1         ,1       ,10        ,1     };
        a_controlPanel.setSliders(val,min,max,names);
        a_controlPanel.setButtonNames(new String[] {"Reset"});
        a_controlPanel.setVisible(true);
        //cylinders.setSpeed(speed0);
        
        player.start();
    }

}




