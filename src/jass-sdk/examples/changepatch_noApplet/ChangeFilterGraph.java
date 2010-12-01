import java.io.*;
import jass.render.*;
import jass.engine.*;
import jass.generators.*;

/**
   This demo is changing the patch dynamically while running.
   It used to work, but after moving things around it stopped working
   consistently. Race conditions are intermittent.
   TODO:
   To fix it requires probably just making selected methods synchronized.
 */

public class ChangeFilterGraph extends Thread {
    public static void main(String[] args) throws InterruptedException, SinkIsFullException {
        int bufferSize = 2;
        int bufferSizeJS = 8*1024;
        float srate = 44100;
        int twait = 2000;
        SourcePlayer sp1 = new SourcePlayer(bufferSize,bufferSizeJS,srate);
        LoopBuffer af = new LoopBuffer(srate,bufferSize,"../data/car1.wav");
        LoopBuffer af2 = new LoopBuffer(srate,bufferSize,"../data/grid.wav");
        ModalModel mm1 = null;
        try {
            mm1 = new ModalModel("../data/s100.sy");
        } catch(FileNotFoundException e) {
            System.out.println(e);
            System.exit(0);
        }
        ModalObject mo1 = new ModalObject(mm1,srate,bufferSize);
       	ModalObject.Contact c1=null,c2=null;
        
        c1 = (ModalObject.Contact)mo1.addSource(af);
        c1.start();
        sp1.addSource(mo1);
        sp1.start();

        sleep(twait);

        System.out.println("change to file rendering");
        // change to file rendering
        sp1.stopPlaying();
        sp1.removeSource(mo1);
        sp1 = new SourcePlayer(bufferSize,srate,"tmp");
        sp1.addSource(mo1);
        mo1.setTime(0);
        af.setTime(0);
        double realtime = 10;
        try {
            //sp1.advanceTime(realtime,true);
            sp1.advanceTime(realtime);
        } catch(Exception e) {
        }

        // switch back to real-time rendering
        System.out.println("change to real-time rendering");
        sp1.stopPlaying();
        sp1.removeSource(mo1);
        sp1 = new SourcePlayer(bufferSize,bufferSizeJS,srate);
        sp1.addSource(mo1);
        mo1.setTime(0);
        af.setTime(0);

        sleep(twait);
        
        System.out.println("add grid");
        c2 = (ModalObject.Contact)mo1.addSource(af2);
        c2.start();
        sp1.start();
        
        sleep(twait);

        System.out.println("remove car");
        mo1.removeSource(af);

        sleep(twait);

        System.out.println("remove grid");
        mo1.removeSource(af2);

        sleep(twait);

        System.out.println("add  grid");
        c2 = (ModalObject.Contact)mo1.addSource(af2);
        c2.start();

    }        
}



