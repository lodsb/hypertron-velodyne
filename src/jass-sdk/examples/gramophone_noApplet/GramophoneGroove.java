import jass.engine.*;
import jass.generators.*;
import java.net.*;

/**
   Position based playback of wav file. Wavfile (mono) is indexed
   with position of needle on record.
   Every call to getBuffer
   this UG polls for position of needle in seconds, and uses this with
   the previous saved value to index a wav file and compute an audio
   buffer for the corresponding segment.
   @author Kees van den Doel (kvdoel@cs.ubc.ca)
*/
public class GramophoneGroove extends StreamingAudioGroove {

    /** Position server */
    Turntable turnTable;

    /**
       For derived classes
       @param bufferSize buffer size
     */
    public GramophoneGroove(int bufferSize) {
        super(bufferSize); // this is the internal buffer size
    }

    /** Construct Groove from named file.
        @param srate sampling rate in Hertz.
        @param bufferSize bufferSize of this Out
        @param fn Audio file name.
    */
    public GramophoneGroove(float srate,int bufferSize, String fn) throws UnsupportedAudioFileFormatException  {
        super(srate,bufferSize,fn);
    }

    /** Construct Groove from named URL.
        @param srate sampling rate in Hertz.
        @param bufferSize bufferSize of this Out
        @param url Audio file url name.
    */
    public GramophoneGroove(float srate,int bufferSize, URL url) throws UnsupportedAudioFileFormatException {
	super(srate,bufferSize,url);
    }

    /** Set Turntable server
        @param t Turntable which provides position info
     */
    public void setTurntable(Turntable t) {
        turnTable = t;
    }

    /**
       Abstract method of parent class
       @return the position (in seconds) of the needle into the audio data
     */
    public double getPositionOfNeedle() {
	return turnTable.getPos();
    }

}


