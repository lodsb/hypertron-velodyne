This is  the project  to implements the  native methods on  Windows.  It
uses 3 classes from the Synthesis  Toolkit Version 3.2 by Perry R. Cook,
1995-2000 and Gary P. Scavone, 1997-2000. See "readme_stk.txt", which is
copied from their STK, for the rules of using their code.


main dir has dll project. produces stkdll.dll, which dumps STK lowest
level classes  in a dll, and  also a minimal JNI  interface.  Copies the
Release dll automatically to the right places for Java and C++ clients.

You have to set your compile environment to see the JDK .h header files,
depending on where your Java SDK is installed.

sdkdll_client  is a  sample  C++ client  for  testing UI  with text  has
latency of 44ms.



