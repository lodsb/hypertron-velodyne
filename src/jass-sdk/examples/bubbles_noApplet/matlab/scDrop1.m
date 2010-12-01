%scores:
% used for droptest: scbb.m this analyses results
names = {'kees','tim','richard','zarin','ron','carm','kevin','asher','billdeacon','anthony','ward','imanbrouwer','jules','henley','lenstroh','jimmutch','mrarruda','mpaone','reynald'};
nnames = length(names)
ndrops = 21;
scores = [
	2 1 3 2 2 1 1 3 1 1 1 1 2 1 2 1 3 1 1
	1 1 4 3 2 1 3 4 1 3 1 2 1 1 3 1 3 2 2
	3 3 5 3 3 1 4 4 1 4 1 2 2 1 3 2 3 2 2
	2 1 3 3 3 1 1 3 1 2 2 3 1 1 3 2 4 3 1
	3 4 5 3 4 1 4 3 1 3 2 3 3 1 5 3 4 4 2
	  
	3 4 5 4 4 1 3 2 1 5 2 4 3 2 4 3 5 5 3
	3 1 1 2 3 1 1 1 1 1 3 3 2 1 2 3 3 4 1
	4 3 3 4 2 1 2 1 5 5 4 4 3 2 3 4 3 5 3
	4 2 1 5 4 2 3 1 5 5 3 4 4 1 3 4 3 5 1
	5 4 1 3 5 4 1 1 1 3 4 2 3 1 3 3 2 3 1
	 
	5 4 3 3 5 5 1 1 5 5 3 4 3 2 3 3 2 4 4
	4 4 3 5 5 1 1 1 5 5 2 3 3 4 4 4 2 5 2
	4 2 2 4 5 1 3 1 1 1 1 2 4 1 4 4 2 4 3
	4 1 1 2 4 1 3 1 1 4 2 3 2 3 3 2 1 5 3
	4 1 3 3 4 1 1 1 1 4 1 4 2 3 3 3 1 3 4
	 
	1 1 1 2 4 1 1 1 1 3 2 4 4 1 4 3 1 4 3
	1 1 1 2 4 2 1 1 1 4 3 3 4 1 1 2 1 5 3
	1 1 1 1 4 3 1 1 1 4 3 3 4 2 1 2 1 3 3
	1 1 3 1 4 2 1 1 1 3 4 2 2 4 1 3 1 2 2
	1 1 1 1 4 4 1 1 1 3 2 1 3 5 1 3 1 3 2
	 
	1 1 3 1 4 4 1 1 1 3 3 1 4 5 1 2 1 1 2
		 ];
% xi = 0
scoresFlat = scores(1:3:19,:);
% xi = 0.05;
scoresInt = scores(2:3:20,:);
% xi = 0.1;
scoresRise = scores(3:3:21,:);
scoresAv = (scoresFlat+scoresInt+scoresRise)/3;

avFlat = mean(scoresFlat');
avInt = mean(scoresInt');
avRise = mean(scoresRise');
avTot = mean(scoresAv');

varFlat = var(scoresFlat');
varInt = var(scoresInt');
varRise = var(scoresRise');
varTot = var(scoresAv');

%bubble radii in mm
r = [10 7  4  2 1 .5 .3];
rstr = sprintf('%2.1f, ',r);
rstr = strcat('r=',rstr);
subplot(2,2,1)
%boxplot(scoresAv',1);
errorbar(1:7,avTot,sqrt(varTot),sqrt(varTot),'x');
axis([0 8 0 5]);
title 'average'
xlabel(rstr);
ylabel 'rating'
subplot(2,2,2)
%boxplot(scoresFlat',1);
errorbar(1:7,avFlat,sqrt(varFlat),sqrt(varFlat),'x');
axis([0 8 0 5]);
title 'xi=0'
xlabel(rstr);
ylabel 'rating'
subplot(2,2,3)
%boxplot(scoresInt',1);
errorbar(1:7,avInt,sqrt(varInt),sqrt(varInt),'x');
axis([0 8 0 5]);
title 'xi=0.05'
xlabel(rstr);
ylabel 'rating'
subplot(2,2,4)
%boxplot(scoresRise',1);
errorbar(1:7,avRise,sqrt(varRise),sqrt(varRise),'x');
axis([0 8 0 5]);
title 'xi=0.1'
xlabel(rstr);
ylabel 'rating'