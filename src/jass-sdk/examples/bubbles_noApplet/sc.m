r = .0017;
rho_liq=1000;
rho_gas=1.29;
p=100000;
c=340;
gamma=1.4;
eta=7.8;
Kg=.025;
Cp=1000;

[f d f_m] = fd(r,rho_liq,rho_gas,p,c,gamma,eta,Kg,Cp)


%plot(r,f./d);
