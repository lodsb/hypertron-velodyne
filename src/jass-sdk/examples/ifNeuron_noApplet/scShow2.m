addpath('C:\aakees\ubc\emg\jean\semgWalk\src')
x1 = load('in1.m');
%x2 = load('in2.m');
y = load('out.dat');
srate = 2000;
%subplot(2,2,1)
%plot(x1(:,1)/srate,x1(:,2));
%subplot(2,2,3)
%plot(y(:,1)/srate,y(:,2));
doPlot = 1;
nfft=32*2;
fmax = 100;
fsamp = 2000;
p=.01;
xin = x1(:,2);
xout = y(:,2);

xin = resample(xin,2*fmax,fsamp);
xout = resample(xout,2*fmax,fsamp);
xin = xin - mean(xin);
xout = xout - mean(xout);
fsampnew = 2*fmax;
[corr,freq] = crossFreqCorr(xin,xout,nfft,fsampnew,p,doPlot);
axis tight