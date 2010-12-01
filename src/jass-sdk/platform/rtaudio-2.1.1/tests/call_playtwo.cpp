/******************************************/
/*
  call_playtwo.cpp
  by Gary P. Scavone, 2002.

  Test executable using two streams with
  callbacks.
*/
/******************************************/

#include "RtAudio.h"
#include <iostream.h>

/*
typedef signed long  MY_TYPE;
#define FORMAT RtAudio::RTAUDIO_SINT24
#define SCALE  2147483647.0

typedef char  MY_TYPE;
#define FORMAT RtAudio::RTAUDIO_SINT8
#define SCALE  127.0

typedef signed short  MY_TYPE;
#define FORMAT RtAudio::RTAUDIO_SINT16
#define SCALE  32767.0

typedef signed long  MY_TYPE;
#define FORMAT RtAudio::RTAUDIO_SINT32
#define SCALE  2147483647.0

typedef float  MY_TYPE;
#define FORMAT RtAudio::RTAUDIO_FLOAT32
#define SCALE  1.0
*/

typedef double  MY_TYPE;
#define FORMAT RtAudio::RTAUDIO_FLOAT64
#define SCALE  1.0
#define BASE_RATE1 0.005
#define BASE_RATE2 0.004

void usage(void) {
  /* Error function in case of incorrect command-line
     argument specifications
  */
  cout << "\nuseage: call_twostreams N fs\n";
  cout << "    where N = number of channels,\n";
  cout << "    and fs = the sample rate.\n\n";
  exit(0);
}

int chans;

int saw1(char *buffer, int buffer_size, void *data)
{
  int i, j;
  extern int chans;
  MY_TYPE *my_buffer = (MY_TYPE *) buffer;
  double *my_data = (double *) data;

  for (i=0; i<buffer_size; i++) {
    for (j=0; j<chans; j++) {
      *my_buffer++ = (MY_TYPE) (my_data[j] * SCALE);
      my_data[j] += BASE_RATE1 * (j+1+(j*0.1));
      if (my_data[j] >= 1.0) my_data[j] -= 2.0;
    }
  }

  return 0;
}

int saw2(char *buffer, int buffer_size, void *data)
{
  int i, j;
  extern int chans;
  MY_TYPE *my_buffer = (MY_TYPE *) buffer;
  double *my_data = (double *) data;

  for (i=0; i<buffer_size; i++) {
    for (j=0; j<chans; j++) {
      *my_buffer++ = (MY_TYPE) (my_data[j] * SCALE);
      my_data[j] += BASE_RATE2 * (j+1+(j*0.1));
      if (my_data[j] >= 1.0) my_data[j] -= 2.0;
    }
  }

  return 0;
}

int main(int argc, char *argv[])
{
  int device, buffer_size, stream1 = 0, stream2 = 0, fs;
  double *data1 = 0;
  double *data2 = 0;
  RtAudio *audio;
  char input;

  // minimal command-line checking
  if (argc != 3) usage();

  chans = (int) atoi(argv[1]);
  fs = (int) atoi(argv[2]);

  // Open the realtime output device
  buffer_size = 512;
  device = 0; // default device
  try {
    audio = new RtAudio();
  }
  catch (RtError &) {
    exit(EXIT_FAILURE);
  }

  try {
    stream1 = audio->openStream(device, chans, 0, 0,
                                FORMAT, fs, &buffer_size, 8);
    stream2 = audio->openStream(device, chans, 0, 0,
                                FORMAT, fs, &buffer_size, 8);
  }
  catch (RtError &) {
    goto cleanup;
  }

  data1 = (double *) calloc(chans, sizeof(double));
  data2 = (double *) calloc(chans, sizeof(double));

  try {
    audio->setStreamCallback(stream1, &saw1, (void *)data1);
    audio->setStreamCallback(stream2, &saw2, (void *)data2);
    audio->startStream(stream1);
    audio->startStream(stream2);
  }
  catch (RtError &) {
    goto cleanup;
  }

  cout << "\nRunning two streams ... press <enter> to quit.\n";
  cin.get(input);

  cout << "\nStopping both streams.\n";
  try {
    audio->stopStream(stream1);
    audio->stopStream(stream2);
  }
  catch (RtError &) {
    goto cleanup;
  }

  cout << "\nPress <enter> to restart streams:\n";
  cin.get(input);

  try {
    audio->startStream(stream1);
    audio->startStream(stream2);
  }
  catch (RtError &) {
    goto cleanup;
  }

  cout << "\nRunning two streams (quasi-duplex) ... press <enter> to quit.\n";
  cin.get(input);

  try {
    audio->stopStream(stream1);
    audio->stopStream(stream2);
  }
  catch (RtError &) {
  }

 cleanup:
  audio->closeStream(stream1);
  audio->closeStream(stream2);
  delete audio;
  if (data1) free(data1);
  if (data2) free(data2);

  return 0;
}
