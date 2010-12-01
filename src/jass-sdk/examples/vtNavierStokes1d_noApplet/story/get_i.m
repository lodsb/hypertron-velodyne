function [y,x,len] = get_i()
  
%All section lengths are 0.396825 cm for each individual area value.

y = [

    .33
    .3
    .36
    .36
    .68
    .5
    2.43
    3.15
    2.66
    2.49
    
    3.39
    3.8
    3.78
    4.35
    4.5
    4.43
    4.68
    4.52
    4.15
    4.09
    
    3.51
    2.95
    2.03
    1.66
    1.38
    1.05
    .6
    .35
    .32
    .12
    
    .1
    .16
    .25
    .24
    .38
    .28
    .36
    .65
    1.58
    2.05
    
    2.01
    1.58
     ];

dL = 0.396825;
totLen = dL * length(y);
x = 0:(length(y)-1);
x = x*totLen/x(end);
len = x(end)