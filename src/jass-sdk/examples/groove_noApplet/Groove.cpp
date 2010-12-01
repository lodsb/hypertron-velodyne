#include "Groove.h"

// native interface to hardware


/*
 * Class:     Groove
 * Method:    getPositionOfNeedleFromHardware
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_Groove_getPositionOfNeedleFromHardware(JNIEnv *, jobject) {
    // placeholder code:
    static double t=0;
    return t += 32./44100;
}

