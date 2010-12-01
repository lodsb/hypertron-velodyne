
/* Kees van den Doel kvdoel@cs.ubc.ca 2002.
    Native interface for Java access.
*/

#include "include/jass_render_RTAudioFullDuplexRtAudio.h"
#include "rtaudio-2.1.1/RtAudio.h"

static int stream;
static int buffer_size;
static float *localBuf;

/*
 * Class:     jass_render_RTAudioFullDUplexRtAudio
 * Method:    initNativeSound
 * Signature: (IIII)J
 */
JNIEXPORT jlong JNICALL Java_jass_render_RTAudioFullDuplexRtAudio_initNativeSound(JNIEnv *env, jobject obj, jint nchannels, jint srate, jint buffersizeJass, jint numRtAudioBuffers) {
  int device =0, inputdevice = 0;
  buffer_size = buffersizeJass;
  RtAudio *pRtAudioObject;
  try { 
    pRtAudioObject = new RtAudio(&stream,device,
				 (int) nchannels,inputdevice,
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
 * Class:     jass_render_RTAudioFullDuplexRtAudio
 * Method:    writeReadNativeSoundFloat
 * Signature: (J[FI[FI)V
 */
JNIEXPORT void JNICALL Java_jass_render_RTAudioFullDuplexRtAudio_writeReadNativeSoundFloat
(JNIEnv *env, jobject obj, jlong nativePointer, jfloatArray outbuf, jint outbufsz, jfloatArray readbuf, jint readbufsz) {
  RtAudio *pRtAudioObject = (RtAudio *)nativePointer;
  if(pRtAudioObject) {
      jsize len = env->GetArrayLength(outbuf);
      jfloat *outbody = env->GetFloatArrayElements(outbuf, 0);
      for (int i=0; i<len ; i++) {
	  localBuf[i] = outbody[i];
      }
      env->ReleaseFloatArrayElements(outbuf, outbody, 0);
      try {
	  pRtAudioObject->tickStream(stream);
      } catch(RtError &) {
      }
      jfloat *readbody = env->GetFloatArrayElements(readbuf, 0);
      for (int k=0; k<len ; k++) {
	  readbody[k]= localBuf[k];
      }
      env->ReleaseFloatArrayElements(readbuf, readbody, 0);
  }
}

/*
 * Class:     jass_render_RTAudioFullDuplexRtAudio
 * Method:    closeNativeSound
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_jass_render_RTAudioFullDuplexRtAudio_closeNativeSound(JNIEnv *env, jobject obj, jlong nativePointer) {
  RtAudio *pRtAudioObject = (RtAudio *)nativePointer;
  if(pRtAudioObject) {
    pRtAudioObject->closeStream(stream);
    delete pRtAudioObject;
  }
}

