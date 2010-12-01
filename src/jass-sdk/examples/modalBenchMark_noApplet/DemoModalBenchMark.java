import java.lang.*;
import java.util.*;
import jass.render.*;
import jass.engine.*;
import jass.generators.*;

/** Shows how many modes can be jass.sized on your machine.
    CAPITALS are a legacy from old C code where this was ripped from.
    This is generic modal model with arbitrary number of contacts.
    @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class DemoModalBenchMark {
    static final int NMODES = 1000;
    static final int BUFFERSIZE= 128;
    static final float SAMPLINGRATE = 22050f;
    static final float input_gain = 1.0f;
    static final float volume = 1.0f;
    static final int ntimes = 500; // this many buffers to time
    
    static public void main(String args[]) throws Exception {
        float[] ampl = new float[NMODES];
    	float[] freq = new float[NMODES];
    	float[] damp = new float[NMODES];
        float[] input = new float[BUFFERSIZE];
        float[]output = new float[BUFFERSIZE];

        float deadline = 0.f;
        float tottime;
        float real_time =  ntimes * BUFFERSIZE/SAMPLINGRATE;

        ModalObject sob = new ModalObject((float)SAMPLINGRATE,NMODES,1,BUFFERSIZE);
        
        for(int i=0;i<NMODES;i++) {        /* faked values */
           	sob.modalModel.a[0][i] = 1.f;
           	sob.modalModel.f[i] = (float)(50f*Math.sqrt((float)i));
           	sob.modalModel.d[i] = freq[i]/10.f;
        }
        // Create simple noop force
        ((ModalObject.Contact)sob.addSource(new Out(BUFFERSIZE) {
            public void computeBuffer() {
            }
        })).start();
        SourcePlayer sp = new SourcePlayer(BUFFERSIZE,(float)SAMPLINGRATE,"tmp.raw");
        sp.addSource(sob);
        Date t1 = new Date();
        for(int i=0;i<ntimes;i++) {
            double realTime = i*BUFFERSIZE/SAMPLINGRATE;
            sp.advanceTime(realTime);
        }
        Date t2 = new Date();
        tottime = (t2.getTime() - t1.getTime())/1000.f;
    	System.out.println("Runtime: "+ tottime + " Realtime passed: " +real_time);
        System.out.println("Can do "+real_time*NMODES/tottime + " partials at " + SAMPLINGRATE + " Hz");	
	}

}
