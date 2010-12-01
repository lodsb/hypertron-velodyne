% used for droptest

k=1;
N = 44101;
NN = N * 21;
yy = zeros(1,NN);
for r = [10 7  4  2 1 .5 .3]
  for sl = [0 .5 1]
	fn = sprintf('B%d__%2.1f_%2.1f.wav',k,r,sl);
	y = bb(r,0,sl,fn);
	yy(1+N*(k-1):N*k) = y; 
	k = k + 1;
  end
end

wavwrite(yy,44100,'all.wav');