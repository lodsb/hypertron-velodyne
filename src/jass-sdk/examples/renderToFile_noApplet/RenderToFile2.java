import java.io.*;
import jass.render.*;
import jass.engine.*;
import jass.generators.*;

/**
   Render to raw 16 bit PCM file (or ascii) tmp.raw as long as not interrupted.
*/
public class RenderToFile2 extends Thread {
    public static void main(String[] args) 
        throws InterruptedException, SinkIsFullException, FileNotFoundException {
        int bufferSize = 24;
        float srate = 44100;
        int twait = 50;
        boolean ascii = true;
        SourcePlayer sp1 = new SourcePlayer(bufferSize,srate,"tmp.m");
        float[] b = new float[bufferSize];
        b[0] = 1;
        for(int i=1;i<bufferSize;i++) {
            b[i] = 0;
        }
        OneShotBuffer af1 = new OneShotBuffer(srate,bufferSize,b);
        af1.hit();
        ModalModel mm1 = new ModalModel("1.sy");
        ModalObjectWithOneContact mo1 = new ModalObjectWithOneContact(mm1,srate,bufferSize);
        mo1.addSource(af1);
        sp1.addSource(mo1);
        sp1.AGCOff();
        long lt = 0;
        float v=1;
        // render loop
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
        
    }
}



