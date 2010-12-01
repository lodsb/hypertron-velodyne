x = load('v.dat');
srate = 44100;
%plot(x(:,1)/srate * 1000,(x(:,2)));
plot((x(:,2)));
axis tight