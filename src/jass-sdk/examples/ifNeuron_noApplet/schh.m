% plot some HH stuff

v = -100.5:1:160.5;

an = (0.1-0.01*v)./(exp(1-.1*v)-1);
bn = .125 * exp(-v/80);
am = (2.5-0.1*v)./(exp(2.5-0.1*v)-1);
bm = 4*exp(-v/18);
ah = .07*exp(-v/20);
bh = 1./(exp(3-0.1*v)+1);

tn = 1./(an+bn);
n0 = an .* tn;
tm = 1./(am+bm);
m0 = am .* tm;
th = 1./(ah+bh);
h0 = ah .* th;

figure(3)
clf
plot(v,am)
hold on
%plot(v,bm,'r')
axis tight
figure(1)
clf
plot(v,n0);
hold on
plot(v,m0,'r');
plot(v,h0,'g');
legend('n','m','h');
axis tight

figure(2)
clf
%plot(v,tn);
hold on
plot(v,tm,'r');
%plot(v,th,'g');
legend('n','m','h');
axis tight
