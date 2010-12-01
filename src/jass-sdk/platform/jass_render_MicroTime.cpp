#include <jni.h>
#include "include/jass_render_MicroTime.h"

#ifdef __LINUX_OSS__

#include <sys/time.h>
#include <unistd.h> 

static struct timeval tv;
static struct timezone tz;
#endif

#ifdef WIN32

#include <windows.h>

#endif

JNIEXPORT jdouble JNICALL Java_jass_render_MicroTime_getMicroTime(JNIEnv * j, jclass c) {
  double timev;
#ifdef __LINUX_OSS__
  gettimeofday(&tv,&tz);
  timev = tv.tv_usec/1000000.0 + tv.tv_sec;
#endif
#ifdef WIN32
  LONGLONG freq=0,cnt=0;
  LARGE_INTEGER lFrequency,lPerformanceCount; 
  lFrequency.QuadPart = freq;
  QueryPerformanceFrequency(&lFrequency);

  lPerformanceCount.QuadPart = cnt; 
  
  QueryPerformanceCounter(&lPerformanceCount);
  if(lFrequency.QuadPart != 0) {
      timev = ((double)lPerformanceCount.QuadPart)/lFrequency.QuadPart;
  }

#endif
  return  timev;
}
