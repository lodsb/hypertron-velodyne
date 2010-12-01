rmin = 1/1000;
rmax = 1000/1000;;
N = 200;
lmin = log(rmin);
lmax = log(rmax);
d_l = (lmax-lmin)/(N-1);
lr = lmin:d_l:lmax;
r = exp(lr);

rho_liq=1000;
rho_gas=1.29;
p=100000;
c=1500;
gamma=1.4;
eta=.01;
Kg=.025;
Cp=1000;

[f d f_m,d_th,d_rad] = fd(r,rho_liq,rho_gas,p,c,gamma,eta,Kg,Cp);

delt = (1/pi) *d ./ f;

%semilogx(r*1000,d_th);
%plot(r,r.^1.5.*d_th);
plot(r,f./f_m);

axis tight
