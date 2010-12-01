
// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the ASIOJASS_EXPORTS
// symbol defined on the command line. this symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// ASIOJASS_API functions as being imported from a DLL, wheras this DLL sees symbols
// defined with this macro as being exported.

#ifndef __ASIOJASS_H_INC
#define __ASIOJASS_H_INC

#ifdef ASIOJASS_EXPORTS
#define ASIOJASS_API __declspec(dllexport)
#else
#define ASIOJASS_API __declspec(dllimport)
#endif

#define __OS_Win_
#define __STK_REALTIME_
// This class is exported from the asioJass.dll
/*class ASIOJASS_API CAsioJass {
public:
	CAsioJass(void);
	// TODO: add your methods here.
};

extern ASIOJASS_API int nAsioJass;

ASIOJASS_API int fnAsioJass(void);
*/

#endif