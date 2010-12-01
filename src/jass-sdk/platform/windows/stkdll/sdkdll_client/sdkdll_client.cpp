// sdkdll_client.cpp : Defines the entry point for the console application.
//
/*
Kees van den Doel kvdoel@cs.ubc.ca 2001
A simple test of latency using keyboard.
*/
#include "stdafx.h"

#define __OS_Win_
#define __STK_REALTIME_

#include <stdio.h>
#include "../RtAudio.h"

int main(int argc, char* argv[])
{
	printf("Hello World!\n");
    int channels = 1;
    int default_device = -1;
    RtAudio rta(channels, (MY_FLOAT)44100, "play",default_device,4);
    int bufsz=441;
    short *buf = new short[bufsz];
    for(int i=0;i<bufsz;i++) {
        double x = sin(i*2*3.14159
            *1000/44100);
        buf[i] = (short)(x*20000);
    }
    //while(1) {
    getchar();
    int k=0;
    while(1) {
        rta.playBuffer(buf,bufsz);
        printf("%d\n",k++);
    }
    getchar();
	return 0;
}

