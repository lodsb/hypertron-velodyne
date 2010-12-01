/*
*   Class   Fmath
*
*   USAGE:  Mathematical class that supplements java.lang.Math and contains:
*               the main physical constants
*               trigonemetric functions absent from java.lang.Math
*               some useful additional mathematical functions
*               some conversion functions
*
*   WRITTEN BY: Dr Michael Thomas Flanagan
*
*   DATE:    June 2002
*   AMENDED: 25 July 2005
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's Java library on-line web page:
*   Fmath.html
*
*   Copyright (c) May 2005
*
*   PERMISSION TO COPY:
*   Permission to use, copy and modify this software and its documentation for
*   NON-COMMERCIAL purposes is granted, without fee, provided that an acknowledgement
*   to the author, Michael Thomas Flanagan at www.ee.ucl.ac.uk/~mflanaga, appears in all copies.
*
*   Dr Michael Thomas Flanagan makes no representations about the suitability
*   or fitness of the software for any or for a particular purpose.
*   Michael Thomas Flanagan shall not be liable for any damages suffered
*   as a result of using, modifying or distributing this software or its derivatives.
*
***************************************************************************************/

package jass.utils;

import java.util.Vector;

public class Fmath{

        // PHYSICAL CONSTANTS

        public static final double N_AVAGADRO = 6.0221419947e23;        /*      mol^-1          */
        public static final double K_BOLTZMANN = 1.380650324e-23;       /*      J K^-1          */
        public static final double H_PLANCK = 6.6260687652e-34;         /*      J s             */
        public static final double H_PLANCK_RED = H_PLANCK/(2*Math.PI); /*      J s             */
        public static final double C_LIGHT = 2.99792458e8;              /*      m s^-1          */
        public static final double R_GAS = 8.31447215;                  /*      J K^-1 mol^-1   */
        public static final double F_FARADAY = 9.6485341539e4;          /*      C mol^-1        */
        public static final double T_ABS = -273.15;                     /*      Celsius         */
        public static final double Q_ELECTRON = -1.60217646263e-19;     /*      C               */
        public static final double M_ELECTRON = 9.1093818872e-31;       /*      kg              */
        public static final double M_PROTON = 1.6726215813e-27;         /*      kg              */
        public static final double M_NEUTRON = 1.6749271613e-27;        /*      kg              */
        public static final double EPSILON_0 = 8.854187817e-12;         /*      F m^-1          */
        public static final double MU_0 = Math.PI*4e-7;                 /*      H m^-1 (N A^-2) */

        // MATHEMATICAL CONSTANTS
        public static final double EULER_CONSTANT_GAMMA = 0.5772156649015627;

        // METHODS
        // Log to base 10 of a double number
        public static double log10(double a){
            return Math.log(a)/Math.log(10.0D);
        }

        // Log to base 10 of a float number
        public static float log10(float a){
            double aa = (double) a;
            return (float) (Math.log(aa)/Math.log(10.0D));
        }

        // Square of a double number
        public static double square(double a){
            return a*a;
        }

        // Square of a float number
        public static float square(float a){
            return a*a;
        }

        // Square of an int number
        public static int square(int a){
            return a*a;
        }

        // factorial of n
        // argument and return are integer, therefore limited to 0<=n<=12
        // see below for long and double arguments
        public static int factorial(int n){
            if(n<0)throw new IllegalArgumentException("n must be a positive integer");
            if(n>12)throw new IllegalArgumentException("n must less than 13 to avoid integer overflow\nTry long or double argument");
            int f = 1;
            for(int i=1; i<=n; i++)f*=i;
            return f;
        }

        // factorial of n
        // argument and return are long, therefore limited to 0<=n<=20
        // see below for double argument
        public static long factorial(long n){
            if(n<0)throw new IllegalArgumentException("n must be a positive integer");
            if(n>20)throw new IllegalArgumentException("n must less than 21 to avoid long integer overflow\nTry double argument");
            long f = 1;
            for(int i=1; i<=n; i++)f*=i;
            return f;
        }

        // factorial of n
        // Argument is of type double but must be, numerically, an integer
        // factorial returned as double but is, numerically, should be an integer
        // numerical rounding may makes this an approximation after n = 21
        public static double factorial(double n){
            if(n<0 || (n-(int)n)!=0)throw new IllegalArgumentException("\nn must be a positive integer\nIs a Gamma funtion [Fmath.gamma(x)] more appropriate?");
            double f = 1.0D;
            int nn = (int)n;
            for(int i=1; i<=nn; i++)f*=i;
            return f;
        }

        // log to base e of the factorial of n
        // log[e](factorial) returned as double
        // numerical rounding may makes this an approximation
        public static double logFactorial(int n){
            if(n<0 || (n-(int)n)!=0)throw new IllegalArgumentException("\nn must be a positive integer\nIs a Gamma funtion [Fmath.gamma(x)] more appropriate?");
            double f = 0.0D;
            for(int i=2; i<=n; i++)f+=Math.log(i);
            return f;
        }

        // log to base e of the factorial of n
        // Argument is of type double but must be, numerically, an integer
        // log[e](factorial) returned as double
        // numerical rounding may makes this an approximation
        public static double logFactorial(double n){
        if(n<0 || (n-(int)n)!=0)throw new IllegalArgumentException("\nn must be a positive integer\nIs a Gamma funtion [Fmath.gamma(x)] more appropriate?");
            double f = 0.0D;
            int nn = (int)n;
            for(int i=2; i<=nn; i++)f+=Math.log(i);
            return f;
        }

        // Maximum of a 1D array of doubles, aa
        public static double maximum(double[] aa){
            int n = aa.length;
            double aamax=aa[0];
            for(int i=1; i<n; i++){
                if(aa[i]>aamax)aamax=aa[i];
            }
            return aamax;
        }

        // Maximum of a 1D array of floats, aa
        public static float maximum(float[] aa){
            int n = aa.length;
            float aamax=aa[0];
            for(int i=1; i<n; i++){
                if(aa[i]>aamax)aamax=aa[i];
            }
            return aamax;
        }

        // Maximum of a 1D array of ints, aa
        public static int maximum(int[] aa){
            int n = aa.length;
            int aamax=aa[0];
            for(int i=1; i<n; i++){
                if(aa[i]>aamax)aamax=aa[i];
            }
            return aamax;
        }

        // Maximum of a 1D array of longs, aa
        public static long maximum(long[] aa){
            long n = aa.length;
            long aamax=aa[0];
            for(int i=1; i<n; i++){
                if(aa[i]>aamax)aamax=aa[i];
            }
            return aamax;
        }

        // Minimum of a 1D array of doubles, aa
        public static double minimum(double[] aa){
            int n = aa.length;
            double aamin=aa[0];
            for(int i=1; i<n; i++){
                if(aa[i]<aamin)aamin=aa[i];
            }
            return aamin;
        }

        // Minimum of a 1D array of floats, aa
        public static float minimum(float[] aa){
            int n = aa.length;
            float aamin=aa[0];
            for(int i=1; i<n; i++){
                if(aa[i]<aamin)aamin=aa[i];
            }
            return aamin;
        }

        // Minimum of a 1D array of ints, aa
        public static int minimum(int[] aa){
            int n = aa.length;
            int aamin=aa[0];
            for(int i=1; i<n; i++){
                if(aa[i]<aamin)aamin=aa[i];
            }
            return aamin;
        }

        // Minimum of a 1D array of longs, aa
        public static long minimum(long[] aa){
            long n = aa.length;
            long aamin=aa[0];
            for(int i=1; i<n; i++){
                if(aa[i]<aamin)aamin=aa[i];
            }
            return aamin;
        }

        // Reverse the order of the elements of a 1D array of doubles, aa
        public static double[] reverseArray(double[] aa){
            int n = aa.length;
            double[] bb = new double[n];
            for(int i=0; i<n; i++){
               bb[i] = aa[n-1-i];
            }
            return bb;
        }

        // Reverse the order of the elements of a 1D array of floats, aa
        public static float[] reverseArray(float[] aa){
            int n = aa.length;
            float[] bb = new float[n];
            for(int i=0; i<n; i++){
               bb[i] = aa[n-1-i];
            }
            return bb;
        }

        // Reverse the order of the elements of a 1D array of ints, aa
        public static int[] reverseArray(int[] aa){
            int n = aa.length;
            int[] bb = new int[n];
            for(int i=0; i<n; i++){
               bb[i] = aa[n-1-i];
            }
            return bb;
        }

        // Reverse the order of the elements of a 1D array of longs, aa
        public static long[] reverseArray(long[] aa){
            int n = aa.length;
            long[] bb = new long[n];
            for(int i=0; i<n; i++){
               bb[i] = aa[n-1-i];
            }
            return bb;
        }

        // Reverse the order of the elements of a 1D array of char, aa
        public static char[] reverseArray(char[] aa){
            int n = aa.length;
            char[] bb = new char[n];
            for(int i=0; i<n; i++){
               bb[i] = aa[n-1-i];
            }
            return bb;
        }

        // return absolute values of an array of doubles
        public static double[] arrayAbs(double[] aa){
            int n = aa.length;
            double[] bb = new double[n];
            for(int i=0; i<n; i++){
               bb[i] = Math.abs(aa[i]);
            }
            return bb;
        }

        // return absolute values of an array of floats
        public static float[] arrayAbs(float[] aa){
            int n = aa.length;
            float[] bb = new float[n];
            for(int i=0; i<n; i++){
               bb[i] = Math.abs(aa[i]);
            }
            return bb;
        }

        // return absolute values of an array of long
        public static long[] arrayAbs(long[] aa){
            int n = aa.length;
            long[] bb = new long[n];
            for(int i=0; i<n; i++){
               bb[i] = Math.abs(aa[i]);
            }
            return bb;
        }

        // return absolute values of an array of int
        public static int[] arrayAbs(int[] aa){
            int n = aa.length;
            int[] bb = new int[n];
            for(int i=0; i<n; i++){
               bb[i] = Math.abs(aa[i]);
            }
            return bb;
        }


        // multiple all elements by a constant double[] by double -> double[]
        public static double[] arrayMultByConstant(double[] aa, double constant){
            int n = aa.length;
            double[] bb = new double[n];
            for(int i=0; i<n; i++){
               bb[i] = aa[i]*constant;
            }
            return bb;
        }

        // multiple all elements by a constant int[] by double -> double[]
        public static double[] arrayMultByConstant(int[] aa, double constant){
            int n = aa.length;
            double[] bb = new double[n];
            for(int i=0; i<n; i++){
               bb[i] = (double)aa[i]*constant;
            }
            return bb;
        }
        // multiple all elements by a constant double[] by int -> double[]
        public static double[] arrayMultByConstant(double[] aa, int constant){
            int n = aa.length;
            double[] bb = new double[n];
            for(int i=0; i<n; i++){
               bb[i] = aa[i]*(double)constant;
            }
            return bb;
        }

        // multiple all elements by a constant int[] by int -> double[]
        public static double[] arrayMultByConstant(int[] aa, int constant){
            int n = aa.length;
            double[] bb = new double[n];
            for(int i=0; i<n; i++){
               bb[i] = (double)(aa[i]*constant);
            }
            return bb;
        }

        // finds the value of nearest element value in array to the argument value
        public static double nearestElementValue(double[] array, double value){
            double diff = Math.abs(array[0] - value);
            double nearest = array[0];
            for(int i=1; i<array.length; i++){
                if(Math.abs(array[i] - value)<diff){
                    diff = Math.abs(array[i] - value);
                    nearest = array[i];
                }
            }
            return nearest;
        }

        // finds the index of nearest element value in array to the argument value
        public static int nearestElementIndex(double[] array, double value){
            double diff = Math.abs(array[0] - value);
            int nearest = 0;
            for(int i=1; i<array.length; i++){
                if(Math.abs(array[i] - value)<diff){
                    diff = Math.abs(array[i] - value);
                    nearest = i;
                }
            }
            return nearest;
        }

        // finds the value of nearest lower element value in array to the argument value
        public static double nearestLowerElementValue(double[] array, double value){
            double diff0 = 0.0D;
            double diff1 = 0.0D;
            double nearest = 0.0D;
            int ii = 0;
            boolean test = true;
            double min = array[0];
            while(test){
                if(array[ii]<min)min = array[ii];
                if((value - array[ii])>=0.0D){
                    diff0 = value - array[ii];
                    nearest = array[ii];
                    test = false;
                }
                else{
                    ii++;
                    if(ii>array.length-1){
                        nearest = min;
                        diff0 = min - value;
                        test = false;
                    }
                }
            }
            for(int i=0; i<array.length; i++){
                diff1 = value - array[i];
                if(diff1>=0.0D && diff1<diff0 ){
                    diff0 = diff1;
                    nearest = array[i];
                }
            }
            return nearest;
        }

        // finds the index of nearest lower element value in array to the argument value
        public static int nearestLowerElementIndex(double[] array, double value){
            double diff0 = 0.0D;
            double diff1 = 0.0D;
            int nearest = 0;
            int ii = 0;
            boolean test = true;
            double min = array[0];
            int minI = 0;
            while(test){
                if(array[ii]<min){
                    min = array[ii];
                    minI = ii;
                }
                if((value - array[ii])>=0.0D){
                    diff0 = value - array[ii];
                    nearest = ii;
                    test = false;
                }
                else{
                    ii++;
                    if(ii>array.length-1){
                        nearest = minI;
                        diff0 = min - value;
                        test = false;
                    }
                }
            }
            for(int i=0; i<array.length; i++){
                diff1 = value - array[i];
                if(diff1>=0.0D && diff1<diff0 ){
                    diff0 = diff1;
                    nearest = i;
                }
            }
            return nearest;
        }

        // finds the value of nearest higher element value in array to the argument value
        public static double nearestHigherElementValue(double[] array, double value){
            double diff0 = 0.0D;
            double diff1 = 0.0D;
            double nearest = 0.0D;
            int ii = 0;
            boolean test = true;
            double max = array[0];
            while(test){
                if(array[ii]>max)max = array[ii];
                if((array[ii] - value )>=0.0D){
                    diff0 = value - array[ii];
                    nearest = array[ii];
                    test = false;
                }
                else{
                    ii++;
                    if(ii>array.length-1){
                        nearest = max;
                        diff0 = value - max;
                        test = false;
                    }
                }
            }
            for(int i=0; i<array.length; i++){
                diff1 = array[i]- value;
                if(diff1>=0.0D && diff1<diff0 ){
                    diff0 = diff1;
                    nearest = array[i];
                }
            }
            return nearest;
        }

        // finds the index of nearest higher element value in array to the argument value
        public static int nearestHigherElementIndex(double[] array, double value){
            double diff0 = 0.0D;
            double diff1 = 0.0D;
            int nearest = 0;
            int ii = 0;
            boolean test = true;
            double max = array[0];
            int maxI = 0;
            while(test){
                if(array[ii]>max){
                    max = array[ii];
                    maxI = ii;
                }
                if((array[ii] - value )>=0.0D){
                    diff0 = value - array[ii];
                    nearest = ii;
                    test = false;
                }
                else{
                    ii++;
                    if(ii>array.length-1){
                        nearest = maxI;
                        diff0 = value - max;
                        test = false;
                    }
                }
            }
            for(int i=0; i<array.length; i++){
                diff1 = array[i]- value;
                if(diff1>=0.0D && diff1<diff0 ){
                    diff0 = diff1;
                    nearest = i;
                }
            }
            return nearest;
        }


        // finds the value of nearest element value in array to the argument value
        public static int nearestElementValue(int[] array, int value){
            int diff = (int) Math.abs(array[0] - value);
            int nearest = array[0];
            for(int i=1; i<array.length; i++){
               if((int) Math.abs(array[i] - value)<diff){
                    diff = (int)Math.abs(array[i] - value);
                    nearest = array[i];
                }
            }
            return nearest;
        }

        // finds the index of nearest element value in array to the argument value
        public static int nearestElementIndex(int[] array, int value){
            int diff = (int) Math.abs(array[0] - value);
            int nearest = 0;
            for(int i=1; i<array.length; i++){
                if((int) Math.abs(array[i] - value)<diff){
                    diff = (int)Math.abs(array[i] - value);
                    nearest = i;
                }
            }
            return nearest;
        }

        // finds the value of nearest lower element value in array to the argument value
        public static int nearestLowerElementValue(int[] array, int value){
            int diff0 = 0;
            int diff1 = 0;
            int nearest = 0;
            int ii = 0;
            boolean test = true;
            int min = array[0];
            while(test){
                if(array[ii]<min)min = array[ii];
                if((value - array[ii])>=0){
                    diff0 = value - array[ii];
                    nearest = array[ii];
                    test = false;
                }
                else{
                    ii++;
                    if(ii>array.length-1){
                        nearest = min;
                        diff0 = min - value;
                        test = false;
                    }
                }
            }
            for(int i=0; i<array.length; i++){
                diff1 = value - array[i];
                if(diff1>=0 && diff1<diff0 ){
                    diff0 = diff1;
                    nearest = array[i];
                }
            }
            return nearest;
        }

        // finds the index of nearest lower element value in array to the argument value
        public static int nearestLowerElementIndex(int[] array, int value){
            int diff0 = 0;
            int diff1 = 0;
            int nearest = 0;
            int ii = 0;
            boolean test = true;
            int min = array[0];
            int minI = 0;
            while(test){
                if(array[ii]<min){
                    min = array[ii];
                    minI = ii;
                }
                if((value - array[ii])>=0){
                    diff0 = value - array[ii];
                    nearest = ii;
                    test = false;
                }
                else{
                    ii++;
                    if(ii>array.length-1){
                        nearest = minI;
                        diff0 = min - value;
                        test = false;
                    }
                }
            }
            for(int i=0; i<array.length; i++){
                diff1 = value - array[i];
                if(diff1>=0 && diff1<diff0 ){
                    diff0 = diff1;
                    nearest = i;
                }
            }
            return nearest;
        }

        // finds the value of nearest higher element value in array to the argument value
        public static int nearestHigherElementValue(int[] array, int value){
            int diff0 = 0;
            int diff1 = 0;
            int nearest = 0;
            int ii = 0;
            boolean test = true;
            int max = array[0];
            while(test){
                if(array[ii]>max)max = array[ii];
                if((array[ii] - value )>=0){
                    diff0 = value - array[ii];
                    nearest = array[ii];
                    test = false;
                }
                else{
                    ii++;
                    if(ii>array.length-1){
                        nearest = max;
                        diff0 = value - max;
                        test = false;
                    }
                }
            }
            for(int i=0; i<array.length; i++){
                diff1 = array[i]- value;
                if(diff1>=0 && diff1<diff0 ){
                    diff0 = diff1;
                    nearest = array[i];
                }
            }
            return nearest;
        }

        // finds the index of nearest higher element value in array to the argument value
        public static int nearestHigherElementIndex(int[] array, int value){
            int diff0 = 0;
            int diff1 = 0;
            int nearest = 0;
            int ii = 0;
            boolean test = true;
            int max = array[0];
            int maxI = 0;
            while(test){
                if(array[ii]>max){
                    max = array[ii];
                    maxI = ii;
                }
                if((array[ii] - value )>=0){
                    diff0 = value - array[ii];
                    nearest = ii;
                    test = false;
                }
                else{
                    ii++;
                    if(ii>array.length-1){
                        nearest = maxI;
                        diff0 = value - max;
                        test = false;
                    }
                }
            }
            for(int i=0; i<array.length; i++){
                diff1 = array[i]- value;
                if(diff1>=0 && diff1<diff0 ){
                    diff0 = diff1;
                    nearest = i;
                }
            }
            return nearest;
        }


        // recast an array of float as doubles
        public static double[] floatTOdouble(float[] aa){
            int n = aa.length;
            double[] bb = new double[n];
            for(int i=0; i<n; i++){
               bb[i] = (double)aa[i];
            }
            return bb;
        }

        // recast an array of int as double
        public static double[] intTOdouble(int[] aa){
            int n = aa.length;
            double[] bb = new double[n];
            for(int i=0; i<n; i++){
               bb[i] = (double)aa[i];
            }
            return bb;
        }

        // recast an array of int as float
        public static float[] intTOfloat(int[] aa){
            int n = aa.length;
            float[] bb = new float[n];
            for(int i=0; i<n; i++){
               bb[i] = (float)aa[i];
            }
            return bb;
        }

        // recast an array of int as long
        public static long[] intTOlong(int[] aa){
            int n = aa.length;
            long[] bb = new long[n];
            for(int i=0; i<n; i++){
               bb[i] = (long)aa[i];
            }
            return bb;
        }

        // recast an array of long as double
        // BEWARE POSSIBLE LOSS OF PRECISION
        public static double[] longTOdouble(long[] aa){
            int n = aa.length;
            double[] bb = new double[n];
            for(int i=0; i<n; i++){
               bb[i] = (double)aa[i];
            }
            return bb;
        }

        // recast an array of long as float
        // BEWARE POSSIBLE LOSS OF PRECISION
        public static float[] longTOfloat(long[] aa){
            int n = aa.length;
            float[] bb = new float[n];
            for(int i=0; i<n; i++){
               bb[i] = (float)aa[i];
            }
            return bb;
        }

        // recast an array of short as double
        public static double[] shortTOdouble(short[] aa){
            int n = aa.length;
            double[] bb = new double[n];
            for(int i=0; i<n; i++){
               bb[i] = (double)aa[i];
            }
            return bb;
        }

         // recast an array of short as float
        public static float[] shortTOfloat(short[] aa){
            int n = aa.length;
            float[] bb = new float[n];
            for(int i=0; i<n; i++){
               bb[i] = (float)aa[i];
            }
            return bb;
        }

        // recast an array of short as long
        public static long[] shortTOlong(short[] aa){
            int n = aa.length;
            long[] bb = new long[n];
            for(int i=0; i<n; i++){
               bb[i] = (long)aa[i];
            }
            return bb;
        }

        // recast an array of short as int
        public static int[] shortTOint(short[] aa){
            int n = aa.length;
            int[] bb = new int[n];
            for(int i=0; i<n; i++){
               bb[i] = (int)aa[i];
            }
            return bb;
        }

        // recast an array of byte as double
        public static double[] byteTOdouble(byte[] aa){
            int n = aa.length;
            double[] bb = new double[n];
            for(int i=0; i<n; i++){
               bb[i] = (int)aa[i];
            }
            return bb;
        }

        // recast an array of byte as float
        public static float[] byteTOfloat(byte[] aa){
            int n = aa.length;
            float[] bb = new float[n];
            for(int i=0; i<n; i++){
               bb[i] = (float)aa[i];
            }
            return bb;
        }

        // recast an array of byte as long
        public static long[] byteTOlong(byte[] aa){
            int n = aa.length;
            long[] bb = new long[n];
            for(int i=0; i<n; i++){
               bb[i] = (long)aa[i];
            }
            return bb;
        }

        // recast an array of byte as int
        public static int[] byteTOint(byte[] aa){
            int n = aa.length;
            int[] bb = new int[n];
            for(int i=0; i<n; i++){
               bb[i] = (int)aa[i];
            }
            return bb;
        }

        // recast an array of byte as short
        public static short[] byteTOshort(byte[] aa){
            int n = aa.length;
            short[] bb = new short[n];
            for(int i=0; i<n; i++){
               bb[i] = (short)aa[i];
            }
            return bb;
        }

        // recast an array of double as int
        // BEWARE OF LOSS OF PRECISION
        public static int[] doubleTOint(double[] aa){
            int n = aa.length;
            int[] bb = new int[n];
            for(int i=0; i<n; i++){
               bb[i] = (int)aa[i];
            }
            return bb;
        }

        // print an array of doubles to screen
        // No line returns except at the end
        public static void print(double[] aa){
            for(int i=0; i<aa.length; i++){
                System.out.print(aa[i]+"   ");
            }
            System.out.println();
        }

        // print an array of doubles to screen
        // with line returns
        public static void println(double[] aa){
            for(int i=0; i<aa.length; i++){
                System.out.println(aa[i]+"   ");
            }
        }

        // print an array of floats to screen
        // No line returns except at the end
        public static void print(float[] aa){
            for(int i=0; i<aa.length; i++){
                System.out.print(aa[i]+"   ");
            }
            System.out.println();
        }

        // print an array of floats to screen
        // with line returns
        public static void println(float[] aa){
            for(int i=0; i<aa.length; i++){
                System.out.println(aa[i]+"   ");
            }
        }

        // print an array of ints to screen
        // No line returns except at the end
        public static void print(int[] aa){
            for(int i=0; i<aa.length; i++){
                System.out.print(aa[i]+"   ");
            }
            System.out.println();
        }

        // print an array of ints to screen
        // with line returns
        public static void println(int[] aa){
            for(int i=0; i<aa.length; i++){
                System.out.println(aa[i]+"   ");
            }
        }

        // print an array of longs to screen
        // No line returns except at the end
        public static void print(long[] aa){
            for(int i=0; i<aa.length; i++){
                System.out.print(aa[i]+"   ");
            }
            System.out.println();
        }

        // print an array of longs to screen
        // with line returns
        public static void println(long[] aa){
            for(int i=0; i<aa.length; i++){
                System.out.println(aa[i]+"   ");
            }
        }

        // print an array of char to screen
        // No line returns except at the end
        public static void print(char[] aa){
            for(int i=0; i<aa.length; i++){
                System.out.print(aa[i]+"   ");
            }
            System.out.println();
        }

        // print an array of char to screen
        // with line returns
        public static void println(char[] aa){
            for(int i=0; i<aa.length; i++){
                System.out.println(aa[i]+"   ");
            }
        }

        // print an array of String to screen
        // No line returns except at the end
        public static void print(String[] aa){
            for(int i=0; i<aa.length; i++){
                System.out.print(aa[i]+"   ");
            }
            System.out.println();
        }

        // print an array of Strings to screen
        // with line returns
        public static void println(String[] aa){
            for(int i=0; i<aa.length; i++){
                System.out.println(aa[i]+"   ");
            }
        }

        // sort elements in an array of doubles into ascending order
        // using selection sort method
        // returns Vector containing the original array, the sorted array
        //  and an array of the indices of the sorted array
        public static Vector selectSortVector(double[] aa){
            int index = 0;
            int lastIndex = -1;
            int n = aa.length;
            double holdb = 0.0D;
            int holdi = 0;
            double[] bb = new double[n];
            int[] indices = new int[n];
            for(int i=0; i<n; i++){
                bb[i]=aa[i];
                indices[i]=i;
            }

            while(lastIndex != n-1){
                index = lastIndex+1;
                for(int i=lastIndex+2; i<n; i++){
                    if(bb[i]<bb[index]){
                        index=i;
                    }
                }
                lastIndex++;
                holdb=bb[index];
                bb[index]=bb[lastIndex];
                bb[lastIndex]=holdb;
                holdi=indices[index];
                indices[index]=indices[lastIndex];
                indices[lastIndex]=holdi;
            }
            Vector vec = new Vector();
            vec.addElement(aa);
            vec.addElement(bb);
            vec.addElement(indices);
            return vec;
        }

        // sort elements in an array of doubles into ascending order
        // using selection sort method
        public static double[] selectionSort(double[] aa){
            int index = 0;
            int lastIndex = -1;
            int n = aa.length;
            double hold = 0.0D;
            double[] bb = new double[n];
            for(int i=0; i<n; i++){
                bb[i]=aa[i];
            }

            while(lastIndex != n-1){
                index = lastIndex+1;
                for(int i=lastIndex+2; i<n; i++){
                    if(bb[i]<bb[index]){
                        index=i;
                    }
                }
                lastIndex++;
                hold=bb[index];
                bb[index]=bb[lastIndex];
                bb[lastIndex]=hold;
            }
            return bb;
        }

        // sort elements in an array of floats into ascending order
        // using selection sort method
        public static float[] selectionSort(float[] aa){
            int index = 0;
            int lastIndex = -1;
            int n = aa.length;
            float hold = 0.0F;
            float[] bb = new float[n];
            for(int i=0; i<n; i++){
                bb[i]=aa[i];
            }

            while(lastIndex != n-1){
                index = lastIndex+1;
                for(int i=lastIndex+2; i<n; i++){
                    if(bb[i]<bb[index]){
                        index=i;
                    }
                }
                lastIndex++;
                hold=bb[index];
                bb[index]=bb[lastIndex];
                bb[lastIndex]=hold;
            }
            return bb;
        }

        // sort elements in an array of ints into ascending order
        // using selection sort method
        public static int[] selectionSort(int[] aa){
            int index = 0;
            int lastIndex = -1;
            int n = aa.length;
            int hold = 0;
            int[] bb = new int[n];
            for(int i=0; i<n; i++){
                bb[i]=aa[i];
            }

            while(lastIndex != n-1){
                index = lastIndex+1;
                for(int i=lastIndex+2; i<n; i++){
                    if(bb[i]<bb[index]){
                        index=i;
                    }
                }
                lastIndex++;
                hold=bb[index];
                bb[index]=bb[lastIndex];
                bb[lastIndex]=hold;
            }
            return bb;
        }

        // sort elements in an array of longs into ascending order
        // using selection sort method
        public static long[] selectionSort(long[] aa){
            int index = 0;
            int lastIndex = -1;
            int n = aa.length;
            long hold = 0L;
            long[] bb = new long[n];
            for(int i=0; i<n; i++){
                bb[i]=aa[i];
            }

            while(lastIndex != n-1){
                index = lastIndex+1;
                for(int i=lastIndex+2; i<n; i++){
                    if(bb[i]<bb[index]){
                        index=i;
                    }
                }
                lastIndex++;
                hold=bb[index];
                bb[index]=bb[lastIndex];
                bb[lastIndex]=hold;
            }
            return bb;
        }


        /*      returns -1 if x < 0 else returns 1   */
        //  double version
        public static double sign(double x){
            if (x<0.0){
                return -1.0;
            }
            else{
                return 1.0;
            }
        }

        /*      returns -1 if x < 0 else returns 1   */
        //  float version
        public static float sign(float x){
            if (x<0.0F){
                return -1.0F;
            }
            else{
                return 1.0F;
            }
        }

        /*      returns -1 if x < 0 else returns 1   */
        //  int version
        public static int sign(int x){
            if (x<0){
                return -1;
            }
            else{
                return 1;
            }
        }

        /*      returns -1 if x < 0 else returns 1   */
        // long version
        public static long sign(long x){
            if (x<0){
                return -1;
            }
            else{
                return 1;
            }
        }

        // UNIT CONVERSIONS

        // Converts radians to degrees
        public static double radToDeg(double rad){
            return  rad*180.0D/Math.PI;
        }

        // Converts degrees to radians
        public static double degToRad(double deg){
            return  deg*Math.PI/180.0D;
        }

        // Converts electron volts(eV) to corresponding wavelength in nm
        public static double evToNm(double ev){
            return  1e+9*C_LIGHT/(-ev*Q_ELECTRON/H_PLANCK);
        }

        // Converts wavelength in nm to matching energy in eV
        public static double nmToEv(double nm)
        {
            return  C_LIGHT/(-nm*1e-9)*H_PLANCK/Q_ELECTRON;
        }

        // Converts moles per litre to percentage weight by volume
        public static double molarToPercentWeightByVol(double molar, double molWeight){
            return  molar*molWeight/10.0D;
        }

        // Converts percentage weight by volume to moles per litre
        public static double percentWeightByVolToMolar(double perCent, double molWeight){
            return  perCent*10.0D/molWeight;
        }

        // Converts Celsius to Kelvin
        public static double celsiusToKelvin(double cels){
            return  cels-T_ABS;
        }

        // Converts Kelvin to Celsius
        public static double kelvinToCelsius(double kelv){
            return  kelv+T_ABS;
        }

        // Converts Celsius to Fahrenheit
        public static double celsiusToFahren(double cels){
            return  cels*(9.0/5.0)+32.0;
        }

        // Converts Fahrenheit to Celsius
        public static double fahrenToCelsius(double fahr){
            return  (fahr-32.0)*5.0/9.0;
        }

        // Converts calories to Joules
        public static double calorieToJoule(double cal){
            return  cal*4.1868;
        }

        // Converts Joules to calories
        public static double jouleToCalorie(double joule){
            return  joule*0.23884;
        }

        // Converts grams to ounces
        public static double gramToOunce(double gm){
            return  gm/28.3459;
        }

        // Converts ounces to grams
        public static double ounceToGram(double oz){
            return  oz*28.3459;
        }

        // Converts kilograms to pounds
        public static double kgToPound(double kg){
            return  kg/0.4536;
        }

        // Converts pounds to kilograms
        public static double poundToKg(double pds){
            return  pds*0.4536;
        }

        // Converts kilograms to tons
        public static double kgToTon(double kg){
            return  kg/1016.05;
        }

        // Converts tons to kilograms
        public static double tonToKg(double tons){
            return  tons*1016.05;
        }

        // Converts millimetres to inches
        public static double millimetreToInch(double mm){
            return  mm/25.4;
        }

        // Converts inches to millimetres
        public static double inchToMillimetre(double in){
            return  in*25.4;
        }

        // Converts feet to metres
        public static double footToMetre(double ft){
            return  ft*0.3048;
        }

        // Converts metres to feet
        public static double metreToFoot(double metre){
            return  metre/0.3048;
        }

        // Converts yards to metres
        public static double yardToMetre(double yd){
            return  yd*0.9144;
        }

        // Converts metres to yards
        public static double metreToYard(double metre){
            return  metre/0.9144;
        }

        // Converts miles to kilometres
        public static double mileToKm(double mile){
            return  mile*1.6093;
        }

        // Converts kilometres to miles
        public static double kmToMile(double km){
            return  km/1.6093;
        }

        // Converts UK gallons to litres
        public static double gallonToLitre(double gall){
            return  gall*4.546;
        }

        // Converts litres to UK gallons
        public static double litreToGallon(double litre){
            return  litre/4.546;
        }

        // Converts UK quarts to litres
        public static double quartToLitre(double quart){
            return  quart*1.137;
        }

        // Converts litres to UK quarts
        public static double litreToQuart(double litre){
            return  litre/1.137;
        }

        // Converts UK pints to litres
        public static double pintToLitre(double pint){
            return  pint*0.568;
        }

        // Converts litres to UK pints
        public static double litreToPint(double litre){
            return  litre/0.568;
        }

        // Converts UK gallons per mile to litres per kilometre
        public static double gallonPerMileToLitrePerKm(double gallPmile){
            return  gallPmile*2.825;
        }

        // Converts litres per kilometre to UK gallons per mile
        public static double litrePerKmToGallonPerMile(double litrePkm){
            return  litrePkm/2.825;
        }

        // Converts miles per UK gallons to kilometres per litre
        public static double milePerGallonToKmPerLitre(double milePgall){
            return  milePgall*0.354;
        }

        // Converts kilometres per litre to miles per UK gallons
        public static double kmPerLitreToMilePerGallon(double kmPlitre){
            return  kmPlitre/0.354;
        }

        // Converts UK fluid ounce to American fluid ounce
        public static double fluidOunceUKtoUS(double flOzUK){
            return  flOzUK*0.961;
        }

        // Converts American fluid ounce to UK fluid ounce
        public static double fluidOunceUStoUK(double flOzUS){
            return  flOzUS*1.041;
        }

        // Converts UK pint to American liquid pint
        public static double pintUKtoUS(double pintUK){
            return  pintUK*1.201;
        }

        // Converts American liquid pint to UK pint
        public static double pintUStoUK(double pintUS){
            return  pintUS*0.833;
        }

        // Converts UK quart to American liquid quart
        public static double quartUKtoUS(double quartUK){
            return  quartUK*1.201;
        }

        // Converts American liquid quart to UK quart
        public static double quartUStoUK(double quartUS){
            return  quartUS*0.833;
        }

        // Converts UK gallon to American gallon
        public static double gallonUKtoUS(double gallonUK){
            return  gallonUK*1.201;
        }

        // Converts American gallon to UK gallon
        public static double gallonUStoUK(double gallonUS){
            return  gallonUS*0.833;
        }

        // Converts UK pint to American cup
        public static double pintUKtoCupUS(double pintUK){
            return  pintUK/0.417;
        }

        // Converts American cup to UK pint
        public static double cupUStoPintUK(double cupUS){
            return  cupUS*0.417;
        }

        // Calculates body mass index (BMI) from height (m) and weight (kg)
        public static double calcBMImetric(double height, double weight){
            return  weight/(height*height);
        }

        // Calculates body mass index (BMI) from height (ft) and weight (lbs)
        public static double calcBMIimperial(double height, double weight){
                height = Fmath.footToMetre(height);
                weight = Fmath.poundToKg(weight);
            return  weight/(height*height);
        }

        // Calculates weight (kg) to give a specified BMI for a given height (m)
        public static double calcWeightFromBMImetric(double bmi, double height){
            return bmi*height*height;
        }

        // Calculates weight (lbs) to give a specified BMI for a given height (ft)
        public static double calcWeightFromBMIimperial(double bmi, double height){
            height = Fmath.footToMetre(height);
            double weight = bmi*height*height;
            weight = Fmath.kgToPound(weight);
            return  weight;
        }


        // ADDITIONAL TRIGONOMETRIC FUNCTIONS

        // Returns the length of the hypotenuse of a and b
        // i.e. sqrt(a*a+b*b) [without unecessary overflow or underflow]
        // double version
        public static double hypot(double aa, double bb){
            double amod=Math.abs(aa);
            double bmod=Math.abs(bb);
            double cc = 0.0D, ratio = 0.0D;
            if(amod==0.0){
                cc=bmod;
            }
            else{
                if(bmod==0.0){
                    cc=amod;
                }
                else{
                    if(amod>=bmod){
                        ratio=bmod/amod;
                        cc=amod*Math.sqrt(1.0 + ratio*ratio);
                    }
                    else{
                        ratio=amod/bmod;
                        cc=bmod*Math.sqrt(1.0 + ratio*ratio);
                    }
                }
            }
            return cc;
        }

        // Returns the length of the hypotenuse of a and b
        // i.e. sqrt(a*a+b*b) [without unecessary overflow or underflow]
        // float version
        public static float hypot(float aa, float bb){
            return (float) hypot((double) aa, (double) bb);
        }

        // Angle (in radians) subtended at coordinate C
        // given x, y coordinates of all apices, A, B and C, of a triangle
        public static double angle(double xAtA, double yAtA, double xAtB, double yAtB, double xAtC, double yAtC){

            double ccos = Fmath.cos(xAtA, yAtA, xAtB, yAtB, xAtC, yAtC);
            return Math.acos(ccos);
        }

        // Angle (in radians) between sides sideA and sideB given all side lengths of a triangle
        public static double angle(double sideAC, double sideBC, double sideAB){

            double ccos = Fmath.cos(sideAC, sideBC, sideAB);
            return Math.acos(ccos);
        }

        // Sine of angle subtended at coordinate C
        // given x, y coordinates of all apices, A, B and C, of a triangle
        public static double sin(double xAtA, double yAtA, double xAtB, double yAtB, double xAtC, double yAtC){
            double angle = Fmath.angle(xAtA, yAtA, xAtB, yAtB, xAtC, yAtC);
            return Math.sin(angle);
        }

        // Sine of angle between sides sideA and sideB given all side lengths of a triangle
        public static double sin(double sideAC, double sideBC, double sideAB){
            double angle = Fmath.angle(sideAC, sideBC, sideAB);
            return Math.sin(angle);
        }

        // Sine given angle in radians
        // for completion - returns Math.sin(arg)
        public static double sin(double arg){
            return Math.sin(arg);
        }

        // Inverse sine
        // Fmath.asin Checks limits - Java Math.asin returns NaN if without limits
        public static double asin(double a){
            if(a<-1.0D && a>1.0D) throw new IllegalArgumentException("Fmath.asin argument (" + a + ") must be >= -1.0 and <= 1.0");
            return Math.asin(a);
        }

        // Cosine of angle subtended at coordinate C
        // given x, y coordinates of all apices, A, B and C, of a triangle
        public static double cos(double xAtA, double yAtA, double xAtB, double yAtB, double xAtC, double yAtC){
            double sideAC = Fmath.hypot(xAtA - xAtC, yAtA - yAtC);
            double sideBC = Fmath.hypot(xAtB - xAtC, yAtB - yAtC);
            double sideAB = Fmath.hypot(xAtA - xAtB, yAtA - yAtB);
            return Fmath.cos(sideAC, sideBC, sideAB);
        }

        // Cosine of angle between sides sideA and sideB given all side lengths of a triangle
        public static double cos(double sideAC, double sideBC, double sideAB){
            return 0.5D*(sideAC/sideBC + sideBC/sideAC - (sideAB/sideAC)*(sideAB/sideBC));
        }

         // Cosine given angle in radians
         // for completion - returns Java Math.cos(arg)
        public static double cos(double arg){
            return Math.cos(arg);
        }

        // Inverse cosine
        // Fmath.asin Checks limits - Java Math.asin returns NaN if without limits
        public static double acos(double a){
            if(a<-1.0D || a>1.0D) throw new IllegalArgumentException("Fmath.acos argument (" + a + ") must be >= -1.0 and <= 1.0");
            return Math.acos(a);
        }

        // Tangent of angle subtended at coordinate C
        // given x, y coordinates of all apices, A, B and C, of a triangle
        public static double tan(double xAtA, double yAtA, double xAtB, double yAtB, double xAtC, double yAtC){
            double angle = Fmath.angle(xAtA, yAtA, xAtB, yAtB, xAtC, yAtC);
            return Math.tan(angle);
        }

        // Tangent of angle between sides sideA and sideB given all side lengths of a triangle
        public static double tan(double sideAC, double sideBC, double sideAB){
            double angle = Fmath.angle(sideAC, sideBC, sideAB);
            return Math.tan(angle);
        }

        // Tangent given angle in radians
        // for completion - returns Math.tan(arg)
        public static double tan(double arg){
            return Math.tan(arg);
        }

        // Inverse tangent
        // for completion - returns Math.atan(arg)
        public static double atan(double a){
            return Math.atan(a);
        }

        // Inverse tangent - ratio numerator and denominator provided
        // for completion - returns Math.atan2(arg)
        public static double atan2(double a, double b){
            return Math.atan2(a, b);
        }

        // Cotangent
        public static double cot(double a){
            return 1.0D/Math.tan(a);
        }

        // Inverse cotangent
        public static double acot(double a){
            return Math.atan(1.0D/a);
        }

        // Inverse cotangent - ratio numerator and denominator provided
        public static double acot2(double a, double b){
            return Math.atan2(b, a);
        }

        // Secant
        public static double sec(double a){
            return 1.0/Math.cos(a);
        }

        // Inverse secant
        public static double asec(double a){
            if(a<1.0D && a>-1.0D) throw new IllegalArgumentException("asec argument (" + a + ") must be >= 1 or <= -1");
            return Math.acos(1.0/a);
        }

        // Cosecant
        public static double csc(double a){
            return 1.0D/Math.sin(a);
        }

        // Inverse cosecant
        public static double acsc(double a){
            if(a<1.0D && a>-1.0D) throw new IllegalArgumentException("acsc argument (" + a + ") must be >= 1 or <= -1");
            return Math.asin(1.0/a);
        }

        // Exsecant
        public static double exsec(double a){
            return (1.0/Math.cos(a)-1.0D);
        }

        // Inverse exsecant
        public static double aexsec(double a){
            if(a<0.0D && a>-2.0D) throw new IllegalArgumentException("aexsec argument (" + a + ") must be >= 0.0 and <= -2");
            return Math.asin(1.0D/(1.0D + a));
        }

        // Versine
        public static double vers(double a){
            return (1.0D - Math.cos(a));
        }

        // Inverse  versine
        public static double avers(double a){
            if(a<0.0D && a>2.0D) throw new IllegalArgumentException("avers argument (" + a + ") must be <= 2 and >= 0");
            return Math.acos(1.0D - a);
        }

        // Coversine
        public static double covers(double a){
            return (1.0D - Math.sin(a));
        }

        // Inverse coversine
        public static double acovers(double a){
            if(a<0.0D && a>2.0D) throw new IllegalArgumentException("acovers argument (" + a + ") must be <= 2 and >= 0");
            return Math.asin(1.0D - a);
        }

        // Haversine
        public static double hav(double a){
            return 0.5D*Fmath.vers(a);
        }

        // Inverse haversine
        public static double ahav(double a){
            if(a<0.0D && a>1.0D) throw new IllegalArgumentException("ahav argument (" + a + ") must be >= 0 and <= 1");
            return 0.5D*Fmath.vers(a);
        }

        // Sinc
        public static double sinc(double a){
            if(Math.abs(a)<1e-40){
                return 1.0D;
            }
            else{
                return Math.sin(a)/a;
            }
        }

        //Hyperbolic sine of a double number
        public static double sinh(double a){
            return 0.5D*(Math.exp(a)-Math.exp(-a));
        }

        // Inverse hyperbolic sine of a double number
        public static double asinh(double a){
            double sgn = 1.0D;
            if(a<0.0D){
                sgn = -1.0D;
                a = -a;
            }
            return sgn*Math.log(a+Math.sqrt(a*a+1.0D));
        }

        //Hyperbolic cosine of a double number
        public static double cosh(double a){
            return 0.5D*(Math.exp(a)+Math.exp(-a));
        }

        // Inverse hyperbolic cosine of a double number
        public static double acosh(double a){
            if(a<1.0D) throw new IllegalArgumentException("acosh real number argument (" + a + ") must be >= 1");
            return Math.log(a+Math.sqrt(a*a-1.0D));
        }

        //Hyperbolic tangent of a double number
        public static double tanh(double a){
            return sinh(a)/cosh(a);
        }

        // Inverse hyperbolic tangent of a double number
        public static double atanh(double a){
            double sgn = 1.0D;
            if(a<0.0D){
                sgn = -1.0D;
                a = -a;
            }
            if(a>1.0D) throw new IllegalArgumentException("atanh real number argument (" + sgn*a + ") must be >= -1 and <= 1");
            return 0.5D*sgn*(Math.log(1.0D + a)-Math.log(1.0D - a));
        }

        //Hyperbolic cotangent of a double number
        public static double coth(double a){
            return 1.0D/tanh(a);
        }

        // Inverse hyperbolic cotangent of a double number
        public static double acoth(double a){
            double sgn = 1.0D;
            if(a<0.0D){
                sgn = -1.0D;
                a = -a;
            }
            if(a<1.0D) throw new IllegalArgumentException("acoth real number argument (" + sgn*a + ") must be <= -1 or >= 1");
            return 0.5D*sgn*(Math.log(1.0D + a)-Math.log(a - 1.0D));
        }

        //Hyperbolic secant of a double number
        public static double sech(double a){
                return 1.0D/cosh(a);
        }

        // Inverse hyperbolic secant of a double number
        public static double asech(double a){
            if(a>1.0D || a<0.0D) throw new IllegalArgumentException("asech real number argument (" + a + ") must be >= 0 and <= 1");
            return 0.5D*(Math.log(1.0D/a + Math.sqrt(1.0D/(a*a) - 1.0D)));
        }

        //Hyperbolic cosecant of a double number
        public static double csch(double a){
                return 1.0D/sinh(a);
        }

        // Inverse hyperbolic cosecant of a double number
        public static double acsch(double a){
            double sgn = 1.0D;
            if(a<0.0D){
                sgn = -1.0D;
                a = -a;
            }
            return 0.5D*sgn*(Math.log(1.0/a + Math.sqrt(1.0D/(a*a) + 1.0D)));
        }

        // MANTISSA ROUNDING (TRUNCATING)
        // Rounds the mantissa of a double to prec places
        public static double truncate(double x, int prec){
            if(prec<0)return x;
            if(x==0.0D)return x;
            if(Fmath.isNaN(x))return x;
            if(Fmath.isPlusInfinity(x))return x;
            if(Fmath.isMinusInfinity(x))return x;

            if(x==1.0D/0.0D || x==-1.0D/0.0D)return x;
            String ss = "", newss = "";
            Double xx = new Double(x);

            ss = xx.toString();
            newss = ssround(ss, prec);

            return Double.parseDouble(newss);
        }

        // Rounds the mantissa of a float to prec places
        public static float truncate(float x, int prec){
            if(prec<0)return x;
            if(x==0.0F)return x;
            if(Fmath.isNaN(x))return x;
            if(Fmath.isPlusInfinity(x))return x;
            if(Fmath.isMinusInfinity(x))return x;

            String ss = "", newss = "";
            Float xx = new Float(x);

            ss = xx.toString();
            newss = ssround(ss, prec);
            return Float.parseFloat(newss);
        }

        // method for Fmath.truncate
        private static String ssround(String ss, int prec){
            String newss1 = "", newss2 = "", newss3 = "";
            int posdot = 0, pose = 0, posf = 0, posplace = 0;

            if(prec<0)throw new IllegalArgumentException("precision less than zero places");

            posf = ss.length()-1;
            pose = ss.indexOf('E');
            posdot = ss.indexOf('.');
            posplace = posdot+prec;

            if(pose>0){
                 // If exponent present
                 if(posplace<pose){
                    // if more digits than truncation requires
                    if(posplace+1<pose){
                        newss1 = subround(ss.substring(0,posplace+2));
                    }
                    else{
                        newss1 = ss.substring(0,posplace+1);
                    }
                }
                else{
                    newss1 = ss.substring(0,pose);
                }
                newss2 = ss.substring(pose, posf+1);
                newss3 = newss1.concat(newss2);
            }
            else{
                // no exponent present
                if(posplace<posf){
                    if(posplace+1<=posf){
                        newss3 = subround(ss.substring(0,posplace+2));
                    }
                    else{
                        newss3 = ss.substring(0,posplace+1);
                    }
                }
                else{
                    newss3 = ss;
                }
            }
            return newss3;
        }

        // method for the ssround method
        // performs truncation
        private static String subround(String ss){
            int ii=0, jj=0, posdot = 0;
            int n = ss.length();
            int[] iss = new int[n+1];
            boolean finish = false;

            String newss = "";

            posdot=ss.indexOf('.');
            for(int i=0; i<n; i++){
                iss[i] = (int)ss.charAt(i);
            }
            if(iss[n-1]<52){
                finish=true;
            }

            ii=n-2;
            while(!finish){
                jj=ii;
                if(jj==posdot)jj--;
                iss[jj]++;
                if(iss[jj]<58){
                    finish=true;
                }
                else{
                    if(jj+1==posdot){
                        for(int k=n-1; k>=posdot; k--){
                            iss[k+1]=iss[k];
                        }
                        iss[posdot]=48;
                        iss[posdot-1]=49;
                        n++;
                        finish=true;
                    }
                    else{
                        iss[jj]=48;
                    }
                }
                ii--;
            }
            StringBuffer strbuff = new StringBuffer();
            for(int k=0; k<n-1; k++){
                strbuff.append((char)iss[k]);
            }
            newss=strbuff.toString();
            return newss;
        }

        // Returns true if x is infinite, i.e. is equal to either plus or minus infinity
        // x is double
        public static boolean isInfinity(double x){
            boolean test=false;
            if(x==Double.POSITIVE_INFINITY || x==Double.NEGATIVE_INFINITY)test=true;
            return test;
        }

        // Returns true if x is infinite, i.e. is equal to either plus or minus infinity
        // x is float
        public static boolean isInfinity(float x){
            boolean test=false;
            if(x==Float.POSITIVE_INFINITY || x==Float.NEGATIVE_INFINITY)test=true;
            return test;
        }

        // Returns true if x is plus infinity
        // x is double
        public static boolean isPlusInfinity(double x){
            boolean test=false;
            if(x==Double.POSITIVE_INFINITY)test=true;
            return test;
        }

        // Returns true if x is plus infinity
        // x is float
        public static boolean isPlusInfinity(float x){
            boolean test=false;
            if(x==Float.POSITIVE_INFINITY)test=true;
            return test;
        }

        // Returns true if x is minus infinity
        // x is double
        public static boolean isMinusInfinity(double x){
            boolean test=false;
            if(x==Double.NEGATIVE_INFINITY)test=true;
            return test;
        }

        // Returns true if x is minus infinity
        // x is float
        public static boolean isMinusInfinity(float x){
            boolean test=false;
            if(x==Float.NEGATIVE_INFINITY)test=true;
            return test;
        }


        // Returns true if x is 'Not a Number' (NaN)
        // x is double
        public static boolean isNaN(double x){
            boolean test=false;
            if(x!=x)test=true;
            return test;
        }

        // Returns true if x is 'Not a Number' (NaN)
        // x is float
        public static boolean isNaN(float x){
            boolean test=false;
            if(x!=x)test=true;
            return test;
        }

        // Returns true if x equals y
        // x and y are double
        // x may be float within range, PLUS_INFINITY, NEGATIVE_INFINITY, or NaN
        // NB!! This method treats two NaNs as equal
        public static boolean isEqual(double x, double y){
            boolean test=false;
            if(Fmath.isNaN(x)){
                if(Fmath.isNaN(y))test=true;
            }
            else{
                if(Fmath.isPlusInfinity(x)){
                    if(Fmath.isPlusInfinity(y))test=true;
                }
                else{
                    if(Fmath.isMinusInfinity(x)){
                        if(Fmath.isMinusInfinity(y))test=true;
                    }
                    else{
                        if(x==y)test=true;
                    }
                }
            }
            return test;
        }

        // Returns true if x equals y
        // x and y are float
        // x may be float within range, PLUS_INFINITY, NEGATIVE_INFINITY, or NaN
        // NB!! This method treats two NaNs as equal
        public static boolean isEqual(float x, float y){
            boolean test=false;
            if(Fmath.isNaN(x)){
                if(Fmath.isNaN(y))test=true;
            }
            else{
                if(Fmath.isPlusInfinity(x)){
                    if(Fmath.isPlusInfinity(y))test=true;
                }
                else{
                    if(Fmath.isMinusInfinity(x)){
                        if(Fmath.isMinusInfinity(y))test=true;
                    }
                    else{
                        if(x==y)test=true;
                    }
                }
            }
            return test;
        }

        // Returns true if x equals y
        // x and y are int
        public static boolean isEqual(int x, int y){
            boolean test=false;
            if(x==y)test=true;
            return test;
        }

        // Returns true if x equals y
        // x and y are char
        public static boolean isEqual(char x, char y){
            boolean test=false;
            if(x==y)test=true;
            return test;
        }

        // Returns true if x equals y
        // x and y are Strings
        public static boolean isEqual(String x, String y){
            boolean test=false;
            if(x.equals(y))test=true;
            return test;
        }

        // Returns true if x is an even number, false if x is an odd number
        // x is int
        public static boolean isEven(int x){
            boolean test=false;
            if(x%2 == 0.0D)test=true;
            return test;
        }

        // Returns 0 if x == y
        // Returns -1 if x < y
        // Returns 1 if x > y
        // x and y are double
        public static int compare(double x, double y){
            Double X = new Double(x);
            Double Y = new Double(y);
            return X.compareTo(Y);
        }

        // Returns 0 if x == y
        // Returns -1 if x < y
        // Returns 1 if x > y
        // x and y are int
        public static int compare(int x, int y){
            Integer X = new Integer(x);
            Integer Y = new Integer(y);
            return X.compareTo(Y);
        }

        // Returns 0 if x == y
        // Returns -1 if x < y
        // Returns 1 if x > y
        // x and y are long
        public static int compare(long x, long y){
            Long X = new Long(x);
            Long Y = new Long(y);
            return X.compareTo(Y);
        }

        // Returns 0 if x == y
        // Returns -1 if x < y
        // Returns 1 if x > y
        // x and y are float
        public static int compare(float x, float y){
            Float X = new Float(x);
            Float Y = new Float(y);
            return X.compareTo(Y);
        }

        // Returns 0 if x == y
        // Returns -1 if x < y
        // Returns 1 if x > y
        // x and y are short
        public static int compare(byte x, byte y){
            Byte X = new Byte(x);
            Byte Y = new Byte(y);
            return X.compareTo(Y);
        }

        // Returns 0 if x == y
        // Returns -1 if x < y
        // Returns 1 if x > y
        // x and y are short
        public static int compare(short x, short y){
            Short X = new Short(x);
            Short Y = new Short(y);
            return X.compareTo(Y);
        }

        // Returns true if x is an even number, false if x is an odd number
        // x is float but must hold an integer value
        public static boolean isEven(float x){
            double y=Math.floor(x);
            if(((double)x - y)!= 0.0D)throw new IllegalArgumentException("the argument is not an integer");
            boolean test=false;
            y=Math.floor(x/2.0F);
            if(((double)(x/2.0F)-y) == 0.0D)test=true;
            return test;
        }

        // Returns true if x is an even number, false if x is an odd number
        // x is double but must hold an integer value
        public static boolean isEven(double x){
            double y=Math.floor(x);
            if((x - y)!= 0.0D)throw new IllegalArgumentException("the argument is not an integer");
            boolean test=false;
            y=Math.floor(x/2.0F);
            if((x/2.0D-y) == 0.0D)test=true;
            return test;
        }

        // Returns true if x is an odd number, false if x is an even number
        // x is int
        public static boolean isOdd(int x){
            boolean test=true;
            if(x%2 == 0.0D)test=false;
            return test;
        }

        // Returns true if x is an odd number, false if x is an even number
        // x is float but must hold an integer value
        public static boolean isOdd(float x){
            double y=Math.floor(x);
            if(((double)x - y)!= 0.0D)throw new IllegalArgumentException("the argument is not an integer");
            boolean test=true;
            y=Math.floor(x/2.0F);
            if(((double)(x/2.0F)-y) == 0.0D)test=false;
            return test;
        }

        // Returns true if x is an odd number, false if x is an even number
        // x is double but must hold an integer value
        public static boolean isOdd(double x){
            double y=Math.floor(x);
            if((x - y)!= 0.0D)throw new IllegalArgumentException("the argument is not an integer");
            boolean test=true;
            y=Math.floor(x/2.0F);
            if((x/2.0D-y) == 0.0D)test=false;
            return test;
        }

        // Returns true if year (argument) is a leap year
        public static boolean leapYear(int year){
            boolean test = false;

            if(year%4 != 0){
                 test = false;
            }
            else{
                if(year%400 == 0){
                    test=true;
                }
                else{
                    if(year%100 == 0){
                        test=false;
                    }
                    else{
                        test=true;
                    }
                }
            }
            return test;
        }

        // Returns milliseconds since 0 hours 0 minutes 0 seconds on 1 Jan 1970
        public static long dateToJavaMilliS(int year, int month, int day, int hour, int min, int sec){

            long[] monthDays = {0L, 31L, 28L, 31L, 30L, 31L, 30L, 31L, 31L, 30L, 31L, 30L, 31L};
            long ms = 0L;

            long yearDiff = 0L;
            int yearTest = year-1;
            while(yearTest>=1970){
                yearDiff += 365;
                if(Fmath.leapYear(yearTest))yearDiff++;
                yearTest--;
            }
            yearDiff *= 24L*60L*60L*1000L;

            long monthDiff = 0L;
            int monthTest = month -1;
            while(monthTest>0){
                monthDiff += monthDays[monthTest];
                if(Fmath.leapYear(year))monthDiff++;
                monthTest--;
            }

            monthDiff *= 24L*60L*60L*1000L;

            ms = yearDiff + monthDiff + day*24L*60L*60L*1000L + hour*60L*60L*1000L + min*60L*1000L + sec*1000L;

            return ms;
        }

}



