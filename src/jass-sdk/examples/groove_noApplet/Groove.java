import java.net.*;

import jass.generators.*;
import jass.render.*;
import jass.engine.*;

/** implements abstract method in AudioGroove. 
    If you extend StreamingAudioGroove will stream off disk using a JavaSound class.
    However it seems to be implemented stupidly by SUN by just loading more and more into memory as you
    go along untill it runs out of memory and crashes. AudioGroove loads everything at init time.
    I can do 3 mins of mono 16 bit 44Khz wav file.
 */


//class Groove extends StreamingAudioGroove {
    class Groove extends AudioGroove {
        
	// load library with interface code
    static {
	try {
	    System.loadLibrary("Groove");
	} catch(UnsatisfiedLinkError e) {
	    System.out.println("Could not load shared library Groove: "+e);
	}
    }

    private double duration=0; // length of audio in seconds
        
    /**
       For derived classes
       @param bufferSize buffer size
    */
    public Groove(int bufferSize) {
        super(bufferSize); // this is the internal buffer size
    }
        
    /** Construct Groove from named file.
        @param srate sampling rate in Hertz.
        @param bufferSize bufferSize of this Out
        @param fn Audio file name.
    */
    public Groove(float srate,int bufferSize, String fn) throws UnsupportedAudioFileFormatException {
        super(srate,bufferSize,fn);
        this.duration = grooveBufferLength/srate;
    }
        
    /** Construct Groove from named URL.
        @param srate sampling rate in Hertz.
        @param bufferSize bufferSize of this Out
        @param url Audio file url name.
    */
    public Groove(float srate,int bufferSize, URL url) throws UnsupportedAudioFileFormatException {
        super(srate,bufferSize,url);
        this.duration = grooveBufferLength/srate;
    }

    /**
       Abstract method of parent class is implemented here
       @return the position (in seconds) of the needle into the audio data
    */
    public double getPositionOfNeedle() {
        return getPositionOfNeedleFromHardware();
    }

    /** Current position in seconds. Implement in C++ */
    public native double getPositionOfNeedleFromHardware();
        
}

