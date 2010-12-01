
/* Kees van den Doel kvdoel@cs.ubc.ca 2001.
    Native interface for Java access.
*/

#include "../../include/jass_render_RTAudioIn.h"
#include "RtAudio.h"

/*
 * Class:     jass_render_RTAudioIn
 * Method:    initNativeSound
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_jass_render_RTAudioIn_initNativeSound__II(JNIEnv *env, jobject obj, jint nchannels, jint srate) {
    RtAudio *pRtAudioObject = new RtAudio((int)nchannels,(MY_FLOAT)srate,"record");                           
    return (jlong)pRtAudioObject;
}

/*
 * Class:     jass_render_RTAudioIn
 * Method:    initNativeSound
 * Signature: (III)J
 */
JNIEXPORT jlong JNICALL Java_jass_render_RTAudioIn_initNativeSound__III(JNIEnv *env, jobject obj, jint nchannels, jint srate, jint num_fragments) {
    RtAudio *pRtAudioObject = new RtAudio((int)nchannels,(MY_FLOAT)srate,"record",-1,(int)num_fragments);                           
    return (jlong)pRtAudioObject;
}

/*
 * Class:     jass_render_RTAudioIn
 * Method:    readNativeSound
 * Signature: (J[SI)V
 */
JNIEXPORT void JNICALL Java_jass_render_RTAudioIn_readNativeSound(JNIEnv *env, jobject obj, jlong nativePointer, jshortArray buf, jint bufsz) {
    RtAudio *pRtAudioObject = (RtAudio *)nativePointer;
    static const int MAXBUF = 44100;
    static short localBuf[MAXBUF];
    if(bufsz>MAXBUF) {
        bufsz = MAXBUF; // better safe than sorry
    }
    pRtAudioObject->recordBuffer(localBuf, (int)bufsz);
    jsize len = env->GetArrayLength(buf);
    jshort *body = env->GetShortArrayElements(buf, 0);
    for (int i=0; i<len ; i++) {
        body[i] = localBuf[i];
    }
    env->ReleaseShortArrayElements(buf, body, 0);
}

/*
 * Class:     jass_render_RTAudioIn
 * Method:    closeNativeSound
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_jass_render_RTAudioIn_closeNativeSound(JNIEnv *env, jobject obj, jlong nativePointer) {
	RtAudio *pRtAudioObject = (RtAudio *)nativePointer;
	delete pRtAudioObject;
}

