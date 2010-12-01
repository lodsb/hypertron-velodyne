This project implements the native  interface with the ASIO drivers on a
windows  machine. 

This project will output the JassASIO.dll file for use with JASS.

This   project  needs  the   ASIOSDK2.   It   can  be   downloaded  from
http://www.steinberg.net/developers/ASIO2SDKAbout.phtml.     Unzip   the
ASIOSDK2  folder  and  place  the  following files  directly  into  this
project:  asio.cpp  asiodrivers.cpp  asiolist.cpp  asio.h  asiodrivers.h
asiolist.h asiosys.h iasiodrv.h

Make sure the directory platform/include  is in the include path for the
development environment.

After  building the  project, take  the output  file,  JassASIO.dll, and
place it in the directory with  the application you are running, or make
sure the LOAD_LIBRARY path can find it.

DirectX must be installed on a machine to use ASIO.

BUG: The  use of Control-C to  terminate a program that  uses both input
and  output will  cause a  windows 2000  machine with  the  Audigy gamer
installed  to hang.  To prevent  this explicitly  shut down  ASIO before
exiting.

