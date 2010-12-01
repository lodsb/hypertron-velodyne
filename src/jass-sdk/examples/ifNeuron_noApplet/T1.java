import jass.render.*;
import jass.engine.*;
import jass.generators.*;
import jass.neuron.*;

public class T1 {
    SourcePlayer sp1;
    Sine s1;
    Sine s2;
    Sine s3;
    ThreadMixer tm;
    Mixer m1;
    Mixer m2;
    Mixer m3;
    float srate = 44100.f;
    boolean multithreaded = true;

    public static void main (String args[]) throws SinkIsFullException {
        new T1(args);
    }
    
    public T1(String args[]) throws SinkIsFullException {
        int bufferSize = 1024;
        int bufferSizeJavaSound = 10*1024;
        
        s1 = new Sine(srate,bufferSize);
        s2 = new Sine(srate,bufferSize);
        s3 = new Sine(srate,bufferSize);
        tm = new ThreadMixer(bufferSize);
        m1 = new Mixer(bufferSize,2);
        m2 = new Mixer(bufferSize,2);
        m3 = new Mixer(bufferSize,2);
        sp1 = new SourcePlayer(bufferSize,bufferSizeJavaSound,srate);
        sp1.addSource(tm);
        tm.addSource(m1);
        tm.addSource(s2);
        m1.addSource(s2,true);
        m1.addSource(s1);
        tm.init();
        tm.setGain(0,1f);
        tm.setGain(1,1f);

        /*
        if(multithreaded) {
            sp1.addSource(tm);
            tm.addSource(m1);
            tm.addSource(m2);
            m1.addSource(s1);
            m1.addSource(s2,true);
            m2.addSource(m1,true);
            m2.addSource(s2);
            tm.init();
            tm.setGain(0,1f);
            tm.setGain(1,1f);
            tm.setGain(2,1f);

        } else {
            sp1.addSource(m3);
            m3.addSource(m2);
            m3.addSource(m1);
            m1.addSource(s1);
            m1.addSource(s2);
            m2.addSource(m1);
            m2.addSource(s2);
        }
        */
        m1.setGain(0,1f);
        m1.setGain(1,1f);
        m2.setGain(0,1f);
        m2.setGain(1,1f);
        m3.setGain(0,1f);
        m3.setGain(1,1f);

        s1.setFrequency(400);
        s2.setFrequency(900);
        s2.setFrequency(999);
        sp1.setPriority(Thread.MAX_PRIORITY);
        sp1.start();
    }

}

