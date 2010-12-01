To make shared libraries:

I) Native audio IO:

From jass-sdk/platform/linux do this:

1) cd   rtaudio-x.x.x   directory    and   make   everything   following
  instructions,  for  OSS  or  ALSA.  Actually you  only  need  to  make
  RtAudio.o, but it would not hurt to run the tests. See the "INSTALL" 
  instructions there. I do not make this automatic as it uses RtAudio "as-is"
  from Gary Scavone.

2) Copy what you made in step 1 als follows:
   cp             jass-sdk/platform/rtaudio-2.1.1/tests/Release/RtAudio.o
   jass-sdk/platform/linux/lib/oss/ or to jass-sdk/platform/linux/lib/alsa/

3) edit   jass-sdk/platform/linux/Makefile  to   point   to  your   java
   installation directory etc.  

4) make -f Makefile.oss rtaudio (or oss->alsa)

---

II) The MicroTime libraries:

make -f Makefile.generic



