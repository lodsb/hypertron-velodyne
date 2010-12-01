
/* Kees van den Doel kvdoel@cs.ubc.ca 2002.
    Native interface for Java access.
*/

#include "include/jass_render_RTAudioIn.h"
#include "rtaudio-2.1.1/RtAudio.h"

static int stream;
static int buffer_size;
static float *localBuf;

/*
 * Class:     jass_render_RTAudioIn
 * Method:    initNativeSound
 * Signature: (IIII)J
 */
JNIEXPORT jlong JNICALL Java_jass_render_RTAudioIn_initNativeSound(JNIEnv *env, jobject obj, jint nchannels, jint srate, jint buffersizeJass, jint numRtAudioBuffers) {
  int device =0, inputdevice = 0, outputchannels = 0;
  buffer_size = buffersizeJass;
  RtAudio *pRtAudioObject;
  try { 
    pRtAudioObject = new RtAudio(&stream,device,
					outputchannels,inputdevice,
					(int)nchannels,RtAudio::RTAUDIO_FLOAT32,(int)srate,
					&buffer_size,numRtAudioBuffers); 
  } catch(RtError &) {
    return 0;
  }
  localBuf = (float *)pRtAudioObject->getStreamBuffer(stream);
  try {
    pRtAudioObject->startStream(stream);
  } catch(RtError &) {
    return 0;
  }
  return (jlong)pRtAudioObject;

}

/*
 * Class:     jass_render_RTAudioIn
 * Method:    readNativeSound
 * Signature: (J[FI)V
 */
JNIEXPORT void JNICALL Java_jass_render_RTAudioIn_readNativeSoundFloat(JNIEnv *env, jobject obj, jlong nativePointer, jfloatArray buf, jint bufsz) {
    RtAudio *pRtAudioObject = (RtAudio *)nativePointer;
    if(pRtAudioObject) {
	jsize len = env->GetArrayLength(buf);
	jfloat *body = env->GetFloatArrayElements(buf, 0);
	try {
	    pRtAudioObject->tickStream(stream);
	} catch(RtError &) {
	    return;
	}
	for (int i=0; i<len ; i++) {
	    body[i] = localBuf[i];
	}
	env->ReleaseFloatArrayElements(buf, body, 0);
    }
}

/*
 * Class:     jass_render_RTAudioIn
 * Method:    closeNativeSound
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_jass_render_RTAudioIn_closeNativeSound(JNIEnv *env, jobject obj, jlong nativePointer) {
	RtAudio *pRtAudioObject = (RtAudio *)nativePointer;
    if(pRtAudioObject) {
        pRtAudioObject->closeStream(stream);
        delete pRtAudioObject;
    }
}

