
/* Kees van den Doel kvdoel@cs.ubc.ca 2001.
    Native interface for Java access.
*/

#include "../../include/jass_render_RTPlay.h"
#include "RtAudio.h"

/*
 * Class:     RTPlay
 * Method:    closeNativeSound
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_jass_render_RTPlay_closeNativeSound(JNIEnv *env, jobject obj, jlong nativePointer) {
	RtAudio *pRtAudioObject = (RtAudio *)nativePointer;
	delete pRtAudioObject;
}


/*
 * Class:     jass_render_RTPlay
 * Method:    initNativeSound
 * Signature: (III)J
 */
JNIEXPORT jlong JNICALL Java_jass_render_RTPlay_initNativeSound__III(JNIEnv *env, jobject obj, jint nchannels, jint srate, jint num_fragments) {
    RtAudio *pRtAudioObject = new RtAudio((int)nchannels,(MY_FLOAT)srate,"play",-1,(int)num_fragments);                           
    return (jlong)pRtAudioObject;
}


/*
 * Class:     RTPlay
 * Method:    initNativeSound
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_jass_render_RTPlay_initNativeSound__II(JNIEnv *env, jobject obj, jint nchannels, jint srate) {
    RtAudio *pRtAudioObject = new RtAudio((int)nchannels,(MY_FLOAT)srate,"play");                           
    return (jlong)pRtAudioObject;
}

/*
 * Class:     RTPlay
 * Method:    writeNativeSound
 * Signature: (J[SI)V
 */
JNIEXPORT void JNICALL Java_jass_render_RTPlay_writeNativeSound(JNIEnv *env, jobject obj, jlong nativePointer, jshortArray buf, jint bufsz) {
    RtAudio *pRtAudioObject = (RtAudio *)nativePointer;
    static const int MAXBUF = 44100;
    static short localBuf[MAXBUF];
    if(bufsz>MAXBUF) {
        bufsz = MAXBUF; // better safe than sorry
    }

    jsize len = env->GetArrayLength(buf);
    jshort *body = env->GetShortArrayElements(buf, 0);
    for (int i=0; i<len ; i++) {
        localBuf[i] = body[i];
    }
    env->ReleaseShortArrayElements(buf, body, 0);

    pRtAudioObject->playBuffer(localBuf,(int)bufsz);
}
