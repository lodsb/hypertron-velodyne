Stuff for native methods.

In  jass-sdk   directory  "make  native"  will  make   header  files  in
platform/include. C++  source to make shared  libraries implementing the
native methods are in the subdirectories. Presently we have:

I) Real-time audio IO:

ASIODLL for Windows (ASIO callback)
rtaudio based  for LINUX OSS 
rtaudio based for LINUX ALSA
rtaudio based for Windows DirectSound
rtaudio based for Windows ASIO
rtaudio based for Macintosh OS/X

Put the  appropriate shared library  (the one you  want to use)  in your
LD_LIBRARY_PATH, or DYLD_LIBRARY_PATH on  the Mac. When selecting native
sound in SourcePlayer the right  library will be loaded. JassASIO.dll is
different in that you have to  specify with a SourcePlayer API call that
you want  to use this one. It  uses a callback scheme,  unlike the other
native implementations.

The native libraries are in 

jass-sdk/platform/linux/lib/oss (librtaudio.so for OSS)
jass-sdk/platform/linux/lib/alsa (librtaudio.so for ALSA)
jass-sdk/platform/windows/lib/directsound (rtaudio.dll)
jass-sdk/platform/windows/lib/asio (rtaudio.dll and JassASIO.dll)
jass-sdk/platform/macosx/lib/ (librtaudio.jnilib)

Source for them:

-
platform/
Has .cpp files implementing native methods
-
platform/include/
has JNI generated headers (run "make native" from jass-sdk to regenerate
them).  
-
platform/linux/  
has makefiles to make  librtaudio.so based on OSS or ALSA
platform/maxosx/  
has makefile to make  librtaudio.jnilib
-
platform/rtaudio-2.1.1/
has unmodified source of RtAudio ( Copyright (c) 2001-2002 Gary P. Scavone ).
-
platform/windows/JassASIO/
VS6 project to make JassASIO.dll (callback based)
-
platform/windows/rtaudio/DirectSound/
VS6 project to make rtaudio.dll based on DirectSound
-
platform/windows/rtaudio/asio/
VS6 project to make rtaudio.dll based on ASIO

----

II) Native timer:

On   LINUX   use  Makefile.generic,   which   puts  libMicroTime.so   in
platform/linux/lib. The windows library is in platform/windows/lib/.
The source is platform/jass_render_MicroTime.cpp with a VS6 project
in platform/windows.



