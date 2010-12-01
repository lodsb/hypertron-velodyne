
/* Kees van den Doel kvdoel@cs.ubc.ca 2002.
    Native interface for Java access.
*/

#include "include/jass_render_RTPlay.h"
#include "rtaudio-2.1.1/RtAudio.h"

static int stream;
static int buffer_size;
static float *localBuf;


/*
 * Class:     jass_render_RTPlay
 * Method:    initNativeSound
 * Signature: (IIII)J
 */
JNIEXPORT jlong JNICALL Java_jass_render_RTPlay_initNativeSound(JNIEnv *env, jobject obj, jint nchannels, jint srate, jint num_fragments,jint numberOfBuffers) {
  int device =0, inputdevice = 0, inputchannels = 0;
  buffer_size = num_fragments;
  RtAudio *pRtAudioObject;
  try {
    pRtAudioObject = new RtAudio(&stream,device,
					(int)nchannels,inputdevice,
					inputchannels,RtAudio::RTAUDIO_FLOAT32,(int)srate,
					&buffer_size,numberOfBuffers); 
  } catch(RtError &) {
      return 0;
  }
  try {
    localBuf = (float *)pRtAudioObject->getStreamBuffer(stream);
    pRtAudioObject->startStream(stream);
  } catch(RtError &) {
    return 0;
  }
  return (jlong)pRtAudioObject;
}

/*
 * Class:     jass_render_RTPlay
 * Method:    writeNativeSoundFloat
 * Signature: (J[FI)V
 */
JNIEXPORT void JNICALL Java_jass_render_RTPlay_writeNativeSoundFloat(JNIEnv *env, jobject obj, jlong nativePointer, jfloatArray buf, jint bufsz) {
    RtAudio *pRtAudioObject = (RtAudio *)nativePointer;
    if(pRtAudioObject) {
	jsize len = env->GetArrayLength(buf);
	jfloat *body = env->GetFloatArrayElements(buf, 0);
	for (int i=0; i<len ; i++) {
	    localBuf[i] = body[i];
	}
	env->ReleaseFloatArrayElements(buf, body, 0);
        try {
	    pRtAudioObject->tickStream(stream);
        } catch(RtError &) {
        }
    }
}

/*
 * Class:     RTPlay
 * Method:    closeNativeSound
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_jass_render_RTPlay_closeNativeSound(JNIEnv *env, jobject obj, jlong nativePointer) {
	RtAudio *pRtAudioObject = (RtAudio *)nativePointer;
    if(pRtAudioObject) {
        pRtAudioObject->stopStream(stream);
        pRtAudioObject->closeStream(stream);
        delete pRtAudioObject;
    }
}
