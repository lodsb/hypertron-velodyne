% Demo Kelly-Lochbaum-yhtälöistä

clear
close all

%S = [1 2 5 2 1 10]; % putkien pinta-alat, viimeinen 'ulkona'
S = [1 4 3 1 2 10];
len = 0.03; % putken pituus, m
v = 340; % äänen nopeus, m/s
k = (S(1:end-1)-S(2:end))./(S(1:end-1)+S(2:end)); %heijastuskertoimet

fs = round( v/len); % näytteenottotaajuus
%x = zeros(1,1000);
x=rand(1,5000)-.5;
x = x ./(1:length(x));
x(1) = 1; %lasketaan 1000 tapin impulssivaste

g = .5; % heijastuminen glottiksessa
d = .95; % hihasta otettu 'häviö-kerroin'

F0 = zeros(1,length(S)); % eteenpäin kulkevat aallot ennen viivettä,
                         % eka = glottis
F1 = zeros(1,length(S)-1); % eteenpäin kulkevat aallot viiveen jälk.
B0 = zeros(1,length(S)); % taaksepäin kulkevat aallot viiveen jälk.
                         % (signaalin etenemissuuntaan)
B1 = zeros(1,length(S)); % taaksepäin ennen viivettä

for n = 1:length(x),
   F0old = F0;
   F1old = F1;
   B0old = B0;
   B1old = B1;
   B0(1) = B1old(1);
   F0(1) = B0(1)*g + x(n); % glottis-heräte + heijastus
   F1(1) = F0old(1);
   if ( length(B0) < 3),
      B1(1) = 0;
   else
      B1(1) = B1(2)*d*(1+k(1)) + F1(1)*d*k(1);
   end
   for i = 2:length(F1),
      B0(i) = B1old(i);
      F0(i) = F1(i-1)*d*(1-k(i-1)) + B0(i)*d*(-k(i-1));
      F1(i) = F0old(i-1);
      B1(i) = B1(i+1)*d*(1+k(i)) + F1(i)*d*k(i);
   end
   B0(end) = 0; % ei heijastuksia takaisin
   F0(end) = F1(end)*d*(1-k(end));
   y(n) = F0(end); % ulostulo talteen
end

figure
freqz(y,1,1024,fs);
title('Taajuusvaste')
sound(y,fs)


