import java.lang.*;
import java.util.*;
import jass.render.*;
import jass.engine.*;
import jass.generators.*;

/** Shows how many modes can be jass.sized on your machine.
    CAPITALS are a legacy from old C code where this was ripped from.
    This is modal model with only one contact.
    @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class DemoModalBenchMark1Contact {
    static final int NMODES = 100;
    static final int BUFFERSIZE= 128;
    static final float SAMPLINGRATE = 22050f;
    static final float input_gain = 1.0f;
    static final float volume = 1.0f;
    static final int ntimes = 25000; // this many buffers to time
    
    static public void main(String args[]) throws Exception {
        float[] ampl = new float[NMODES];
    	float[] freq = new float[NMODES];
    	float[] damp = new float[NMODES];
        float[] input = new float[BUFFERSIZE];
        float[]output = new float[BUFFERSIZE];

        float deadline = 0.f;
        float tottime;
        float real_time =  ntimes * BUFFERSIZE/SAMPLINGRATE;

        ModalObjectWithOneContact sob = new ModalObjectWithOneContact((float)SAMPLINGRATE,NMODES,1,BUFFERSIZE);
        
        for(int i=0;i<NMODES;i++) {        /* faked values */
           	sob.modalModel.a[0][i] = 1.f;
           	sob.modalModel.f[i] = (float)(50f*Math.sqrt((float)i));
           	sob.modalModel.d[i] = freq[i]/10.f;
        }
        // Create simple noop force
        // must be non-zero as ModalObjectWithOneContact is smart enough not to do
        // any work on zero input...
        sob.addSource(new Out(BUFFERSIZE) {
            float[] precomputedBuf = new float[BUFFERSIZE];
            {
                for(int i=0;i<BUFFERSIZE;i++) {
                    precomputedBuf[i] = i;
                }
            }
            public void computeBuffer() {
                buf = precomputedBuf;
            }
        });
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
