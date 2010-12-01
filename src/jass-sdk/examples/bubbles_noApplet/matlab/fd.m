function [f,d,f_m,d_th,d_rad]=fd(r,rho_liq,rho_gas,p,c,gamma,eta,Kg,Cp)

om_m = sqrt(3*gamma*p/rho_liq)./r;
d_rad = r.*om_m/c

d_vis = 4*eta./(om_m.*rho_liq.*r.*r)*0;

Dg = Kg/(rho_gas*Cp);
lD = sqrt(Dg./(2*om_m));
z = r ./ lD;
d_th = ((sinh(z)+sin(z))./(cosh(z) - cos(z)) - 2./z)./(z/(3*(gamma-1)) +(sinh(z)-sin(z))./(cosh(z)-cos(z)));

d_th = 8.e-4 ./sqrt(r)

delta = d_rad + d_th + d_vis;

d = om_m.*delta/2;
om_b = sqrt(om_m.*om_m-d.*d);
f = om_b/(2*pi);
f_m = om_m/(2*pi);

% trying various plots leads to a good fit in the range .1mm - 10cm:

% d_rad = .0137
% d_th = 8.e-4 r^-.5;