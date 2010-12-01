function to2jass(area,x,tractLen,N,fnout)
%
% convert the area function data files from
% http://www.haskins.yale.edu/facilities/vowels.html
% speech synthesizer to a format that can be read by the jass
% control panel. N = number of area control points.
% Haskins data has segments = 0.875cm
  
  % now interpolate on even spaced grid with N points
  xi = (0:N-1)*tractLen/(N-1);
  Ai = interp1(x,area,xi);
  subplot(2,1,1)
  plot(x,area);
  title 'org'
  subplot(2,1,2)
  plot(xi,Ai);
  title 'interp'
  fout = fopen(fnout,'w');
  createJassImport(xi,Ai,tractLen,fout);
  fclose(fout);


function  createJassImport(xi,Ai,tractLen,fout)
  begin.f1str = 'f1 ';
  begin.f1val=250;
  begin.f1min=10;
  begin.f1max=800;
  begin.f2str = 'f2 ';
  begin.f2val=2000;
  begin.f2min=801;
  begin.f2max=10000;
  begin.dstr = 'u_xx mult ';
  begin.dval = 10;
  begin.dmin =.0;
  begin.dmax =100;
  begin.wstr = 'u mult ';
  begin.wval = 1;
  begin.wmin =.0;
  begin.wmax =1000;
    
  begin.wcstr = 'wal coeff ';
  begin.wcval =.1;
  begin.wcmin =.0;
  begin.wcmax = 1;

  fprintf(fout,'%s\n',begin.f1str);
  fprintf(fout,'%f\n',begin.f1val);
  fprintf(fout,'%f\n',begin.f1min);
  fprintf(fout,'%f\n',begin.f1max);
  fprintf(fout,'%s\n',begin.f2str);
  fprintf(fout,'%f\n',begin.f2val);
  fprintf(fout,'%f\n',begin.f2min);
  fprintf(fout,'%f\n',begin.f2max);
  fprintf(fout,'%s\n',begin.dstr);
  fprintf(fout,'%f\n',begin.dval);
  fprintf(fout,'%f\n',begin.dmin);
  fprintf(fout,'%f\n',begin.dmax);
  fprintf(fout,'%s\n',begin.wstr);
  fprintf(fout,'%f\n',begin.wval);
  fprintf(fout,'%f\n',begin.wmin);
  fprintf(fout,'%f\n',begin.wmax);
  fprintf(fout,'%s\n',begin.wcstr);
  fprintf(fout,'%f\n',begin.wcval);
  fprintf(fout,'%f\n',begin.wcmin);
  fprintf(fout,'%f\n',begin.wcmax);
  fprintf(fout,'%s\n','length');
  fprintf(fout,'%f\n',tractLen/100);
  fprintf(fout,'%f\n',.15);
  fprintf(fout,'%f\n',.68);
  
  area.min = .0;
  area.max = 20;
  for k = 1:length(xi);
    fprintf(fout,'A(%d)\n',k-1);
    fprintf(fout,'%f\n',Ai(k));
    fprintf(fout,'%f\n',area.min);
    fprintf(fout,'%f\n',area.max);    
  end

function [area,x,tractLen]=readData(fin)
  
%
% convert Birkholz format area function to jass conrol panel with N area
% sections
  
  fin = fopen(fin,'r');
  rc = 0;
  data_block = 0;
  data_block_old = 0;
  non_numeric_lines_seen = 0;
  arrIndex = 1; 
  area0 = [];
  x0 = [];
  tractLen = 0;
  while(rc ~= -1)
    rc = fgets(fin);
    isNumeric = isstrprop(rc,'digit');
    if(isNumeric(1))
      rc = strrep(rc,',','.');
      y = sscanf(rc,'%f');
      if(data_block == 1)
        x0(arrIndex) = y;     
      elseif(data_block == 2)
        area0(arrIndex) = y;
      end
      arrIndex = arrIndex + 1;
      %fprintf('NUMBER: %f\n',y);            
    else
      %fprintf('ELSE: %s',rc);
      non_numeric_lines_seen =   non_numeric_lines_seen + 1;
      if(non_numeric_lines_seen == 2)
        data_block = 1;
      elseif(non_numeric_lines_seen == 6)
        data_block = 2;
      elseif(non_numeric_lines_seen > 6)
        rc = -1;
      end
      if(data_block_old ~= data_block)
        arrIndex = 1;
        data_block_old = data_block;
      end

    end
  end

  tractLen = x0(end);
  x1 = [x0(1) x0(2:2:end)];
  for k=1:length(x1)-1;
    x(k) =  (x1(k)+x1(k+1))/2;
  end
  x = x - x(1);
  x = x * tractLen/x(end);

  area = area0(2:2:end);
  
  fclose(fin);