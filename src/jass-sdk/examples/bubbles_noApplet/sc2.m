rmin = .001;
rmax = .02;
N = 50;
fn='bubbles.sy';
rho_liq=1000;
rho_gas=1.29;
p=100000;
c=340;
gamma=1.4;
eta=7.8;
Kg=.025;
Cp=1000;



lmin = log(rmin);
lmax = log(rmax);
d_l = (lmax-lmin)/(N-1);
lr = lmin:d_l:lmax;
r = exp(lr);

[f d f_m] = fd(r,rho_liq,rho_gas,p,c,gamma,eta,Kg,Cp)

% make sy file

fdesc = fopen(fn,'w');

fprintf(fdesc,'nactive_freq:\n');
fprintf(fdesc,'%d\n',N);
fprintf(fdesc,'n_freq:\n');
fprintf(fdesc,'%d\n',N);
fprintf(fdesc,'n_points:\n');
fprintf(fdesc,'%d\n',1);
fprintf(fdesc,'frequency_scale:\n');
fprintf(fdesc,'%f\n',1);
fprintf(fdesc,'damping_scale:\n');
fprintf(fdesc,'%f\n',1);
fprintf(fdesc,'amplitude_scale:\n');
fprintf(fdesc,'%f\n',1);
fprintf(fdesc,'frequencies:\n');
fprintf(fdesc,'%f\n',f_m);
fprintf(fdesc,'dampings:\n');
fprintf(fdesc,'%f\n',d);
fprintf(fdesc,'amplitudes[point][freq]:\n');

for mode=1:N
  fprintf(fdesc,'%f\n',1);
end

fprintf(fdesc,'END\n');

fclose(fdesc);
