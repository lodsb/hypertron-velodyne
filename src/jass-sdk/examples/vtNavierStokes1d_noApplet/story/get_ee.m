function [y,x,len] = get_ee()
  
%All section lengths are 0.396825 cm for each individual area value.

y = [

     1.3276
    .8755
    1.049
    1.9227
    3.0005
    4.214
    4.9179
    
    4.6136
    3.8357
    2.9857
    2.0362
    1.2298
    0.7524
    0.4881
    
    0.3952
    0.4252
    0.5448
    0.9772
    1.5362
     
     ];

dL = .875;
x = (0:(length(y)-1))*dL;
len = x(end);