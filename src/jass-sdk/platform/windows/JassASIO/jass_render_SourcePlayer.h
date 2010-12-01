/* */
#include <jni.h>


#include <stdio.h>
#include <math.h>

/* Header for class jass_render_SourcePlayer */
#ifndef _Included_jass_render_SourcePlayer
#define _Included_jass_render_SourcePlayer
#ifdef __cplusplus
extern "C" {
#endif
#undef jass_render_SourcePlayer_bigEndian
#define jass_render_SourcePlayer_bigEndian 0L
#undef jass_render_SourcePlayer_DEFAULT_BUFFERSIZE
#define jass_render_SourcePlayer_DEFAULT_BUFFERSIZE 1024L


	
/*	Prepare ASIO for Input and/or Output */
JNIEXPORT jint JNICALL Java_jass_render_SourcePlayer_initAsioJass(JNIEnv *env, jobject obj, jint srate, jint bSize, jint outputchannelNumber, jint inputchannelNumber, jobject myInputObj);
JNIEXPORT void JNICALL Java_jass_render_SourcePlayer_cleanupASIO(JNIEnv *env, jobject obj);
int destroyASIO();



#ifdef __cplusplus
}
#endif
#endif
