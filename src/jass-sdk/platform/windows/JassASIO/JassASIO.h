
#ifndef __JassASIO_H_INC
#define __JassASIO_H_INC

#ifdef JassASIO_EXPORTS
#define JassASIO_API __declspec(dllexport)
#else
#define JassASIO_API __declspec(dllimport)
#endif

#define __OS_Win_
#define __STK_REALTIME_

#endif
