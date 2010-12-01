function damping(f1,f2)
  % plot fitted versus actual damping term
    
    om1 = 2*pi*f1;
    om2 = 2*pi*f2;
    rho = 1.14;
    c=350;
    mu = 0.0000186;
    dW1 = sqrt(2*pi*mu*om1/(rho*c*c));
    dW2 = sqrt(2*pi*mu*om2/(rho*c*c));
    betat = (dW2-dW1)/((om2/c)^2 - (om1/c)^2 );
    alpha = dW1 - betat*(om1/c)^2;
    
    f = 1:1:6000;
    om = 2*pi*f;
    
    dWexact = sqrt(2*pi*mu*om/(rho*c*c));
    dWappr = alpha + betat*(om/c).*(om/c);
    
    plot(f,dWexact);
    hold on
    plot(f,dWappr,'r');
    hold off
    
    