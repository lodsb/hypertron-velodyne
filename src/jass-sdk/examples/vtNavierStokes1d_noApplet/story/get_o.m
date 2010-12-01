function [y,x,len] = get_o()
  
%All section lengths are 0.396825 cm for each individual area value.

y = [
.18
    .17
    .23
    .28
    .59
    1.46
    1.6
    1.11
    .82
    1.01
    
    2.72
    2.71
    1.96
    1.92
    1.7
    1.66
    .152
    1.28
    1.44
    1.28
    
    .89
    1.25
    1.38
    1.09
    .71
    .46
    .39
    .32
    .57
    1.06
    
    1.38
    2.29
    2.99
    3.74
    4.39
    5.38
    7.25
    7
    4.57
    2.75
    
    1.48
    .68
    .39
    .14
     ];

dL = 0.396825;
totLen = dL * length(y);
x = 0:(length(y)-1);
x = x*totLen/x(end);
len = x(end)