function y = bb(r,dt,slope,fn)
  
  nb = length(r);
  r = r/1000;
  fs = 44100;
  t = 0:1/fs:1;
  maxt = max(t);
  N = length(t)
  y = 0* t;
  for( k=1:nb )
	del = round(fs*dt(k));
	if(del < 1)
	  del = 1;
	end
	f = 3/r(k)
	d = .13/r(k) + .0072/r(k)^1.5
	sl = slope * d/10;
	fsl = f*(1 + sl*t/maxt);
	y(del:N) = y(del:N) + sin(2*pi* fsl(1:N-del+1) .* t(1:N-del+1)) .* exp(-d*t(1:N-del+1));
  end

sound(y,fs);
maxy = max(abs(y));
y = .9 * y/maxy;

wavwrite(y,fs,fn);
