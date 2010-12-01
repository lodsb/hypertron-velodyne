% convert area functions at
% http://www.haskins.yale.edu/facilities/vowels.html
% All section lengths are 0.875 cm for each individual area value.

N=40;
[area,x,tractLen] = get_i();
to2jass(area,x,tractLen,N,'story_i.txt');

[area,x,tractLen] = get_o();
to2jass(area,x,tractLen,N,'story_o.txt');
