addpath('C:\aakees\ubc\emg\jean\semgWalk\src')
x1 = load('in1.m');
%x2 = load('in2.m');
y = load('out.dat');
srate = 2000;
%subplot(2,2,1)
%plot(x1(:,1)/srate,x1(:,2));
%subplot(2,2,3)
%plot(y(:,1)/srate,y(:,2));

% wavelets
fmin = 10;
fmax = 30;

resolutionPar = 4;
r_f=1;
% build freq. list for wavelet transform
freqList = [fmin];
k = 2;
while(1)
  freqList(k) = freqList(k-1)/(10*r_f*resolutionPar) + freqList(k-1);
  if(freqList(k)>fmax)
    break;
  end
  k = k + 1;
end
fsnew = 3*fmax;
x1 = resample(x1,round(fsnew),round(srate));
y = resample(y,round(fsnew),round(srate));
[hx1,f,t1]=morletTransform(x1(:,2),fsnew,resolutionPar,freqList);
[hy,f,ty]=morletTransform(y(:,2),fsnew,resolutionPar,freqList);
subplot(2,1,1)
pcolor(t1,f,sqrt(abs(hx1)));
xlabel 't'
ylabel 'f'
title 'stimulus'
shading interp;
colorbar;
subplot(2,1,2)
pcolor(ty,f,sqrt(abs(hy)));
xlabel 't'
ylabel 'f'
title 'output'
shading interp;
colorbar;