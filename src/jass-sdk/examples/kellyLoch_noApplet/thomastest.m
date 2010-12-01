n =10;

a = rand(n,1)-.5;
b = rand(n,1)-.5;
c = rand(n,1)-.5;
d = rand(n,1)-.5;
a(1) = 0;
c(n)=0;

x = zeros(n,1);
bb = b;
dd = d;

for k=2:n
  m = a(k)/bb(k-1);
  bb(k) = bb(k) - m*c(k-1);
  dd(k) = dd(k) - m*dd(k-1);
end
dd(n)=dd(n)/bb(n);
for k=n-1:-1:1
  dd(k)=(dd(k)-c(k)*dd(k+1))/bb(k);
end
x = dd;

%test
k=1;
res = b(k)*x(k)+c(k)*x(k+1) - d(k);
fprintf('%g\n',res);
for k=2:n-1
  res = a(k)*x(k-1)+b(k)*x(k)+c(k)*x(k+1) - d(k);
  fprintf('%g\n',res);
end
k=n;
res = a(k)*x(k-1)+b(k)*x(k) - d(k);
fprintf('%g\n',res);
