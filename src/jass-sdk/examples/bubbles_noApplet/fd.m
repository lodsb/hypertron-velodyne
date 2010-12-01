function [f,d,f_m]=fd(r,rho_liq,rho_gas,p,c,gamma,eta,Kg,Cp)

om_m = sqrt(3*gamma*p/rho_liq)./r;
d_rad = r.*om_m/c;

d_vis = 4*eta./(om_m.*rho_liq.*r.*r);
Dg = Kg/(rho_gas*Cp);
lD = sqrt(Dg./(2*om_m));
z=r/lD;
d_th = ((sinh(z)+sin(z))/(cosh(z) - cos(z)) - 2/z)/(z/(3*(gamma-1)) + (sinh(z)-sin(z))/(cosh(z)-cos(z)));
delta = d_rad + d_th + d_vis;

d = om_m.*delta/2;
om_b = sqrt(om_m.*om_m-d.*d);
f = om_b/(2*pi);
f_m = om_m/(2*pi);
