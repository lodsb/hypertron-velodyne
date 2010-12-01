import java.util.*;
/** Server returns position of turntable in seconds
 */
public class Turntable {
    static double currentPos=0;
    double currentTime = 0;
    double dt;
    double freq = .5;
    double v=1; // velocity

    public Turntable(float srate,int bufferSize) {
        dt = bufferSize/srate; // assume mono file
    }
    
    public double getPos() {
        currentTime +=dt;
        //v = 1+Math.cos(2*Math.PI*freq*currentTime);
        currentPos += v*dt;
        return currentPos;
    }
}
