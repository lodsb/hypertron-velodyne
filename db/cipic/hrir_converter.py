#!/usr/bin/python
# -*- coding: utf-8 -*-
#
# CIPIC converter
# ---------------
#
# Simple script to convert the .mat files in the CIPIC_hrtf_database
# to text files named after their azimuth/elevation/channel (Left/Right).
# Simply run in the same dir, the .txt files will be placed in the same 
# dir(s) where the hrir_final.mat file resides.
#
# NOTE: also calculates the FFT and pads to 256 samples
#
#
# 2010 , lodsb // Niklas Klügel
# Released under GPLv3 License
#

import os;
import scipy.io;
import numpy.fft;
#import pylab;


hrtf_filename = "hrir_final";
# specified by hrir_data_documentation.pdf 
azimuthDeg = [-80, -65, -55, -45, -40, -35, -30,-25,-20,-15,-10,-5,0,5,10,15,20,25,30,35,40,45,55,65,80]

def elevationDeg(iteration):
	return -45.0 + (5.625*iteration)

filenr = 2;

def crawldir(cdir):
	for cfile in os.listdir(cdir):
		jdir = os.path.join(cdir, cfile)
		if os.path.isdir(jdir):
			crawldir(jdir)
		else:
			(shortname, extension) = os.path.splitext(cfile)
			if(extension == '.mat' and shortname == hrtf_filename):
				print "File : "+cdir
				extractIRs(jdir, cdir)

def extractIRs(cfile, path):
	mat = scipy.io.loadmat(cfile)
	convertArray(mat['hrir_l'],path, 'L');
	convertArray(mat['hrir_r'],path, 'R');

	

def convertArray(hrirArray, path, filename):	
	i = 0;
	for azArr in hrirArray:
		azdeg = azimuthDeg[i];
		i = i + 1;

		j = 0;
		for elevArr in azArr:
			eldeg = elevationDeg(j)
			j = j + 1;	
			cmplxArray = calcFreqDomain(elevArr);
			writeArray(cmplxArray, path, filename, azdeg, eldeg);
			
def calcFreqDomain(array):
	return numpy.fft.fft(array, 256);
	
	
	
def writeArray(complexArray, path, filename, azdeg, eldeg):
	file = open(os.path.join(path,filename+"_"+str(azdeg)+"_"+str(eldeg)+'.txt'), 'w');		
	
	for cmplx in complexArray:	
	  file.write(str(cmplx.real)+" "+str(cmplx.imag)+"\n");
	
	file.close();


def main():
	crawldir(".");


if __name__ == "__main__":
    main()
