
// The following ifdef block is the standard way of creating macros which make exporting 
// from a DLL simpler. All files within this DLL are compiled with the STKDLL_EXPORTS
// symbol defined on the command line. this symbol should not be defined on any project
// that uses this DLL. This way any other project whose source files include this file see 
// STKDLL_API functions as being imported from a DLL, wheras this DLL sees symbols
// defined with this macro as being exported.

#ifndef __STKDLL_H_INC
#define __STKDLL_H_INC

#ifdef STKDLL_EXPORTS
#define STKDLL_API __declspec(dllexport)
#else
#define STKDLL_API __declspec(dllimport)
#endif

#define __OS_Win_
#define __STK_REALTIME_

// This class is exported from the stkdll.dll
/*
class STKDLL_API CStkdll {
public:
	CStkdll(void);
	// TODO: add your methods here.
};

extern STKDLL_API int nStkdll;

STKDLL_API int fnStkdll(void);
*/

#endif
