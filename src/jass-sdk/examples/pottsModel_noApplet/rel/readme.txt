Put your PottsModel.java file here.

To compile:

javac -classpath ".;jass.jar" *.java
(use ":" instead of ";" on UNIX systems)

To run (for example for q=1, N=10):

java -classpath ".;jass.jar" PottsDemo clap11025.wav 1 20

PottsDemo  will call your  class, do  a Monte  Carlo calculation  of the
magnetization which  is restarted if you  move the T or  bians slider or
press reset.  The calculation is  auralized by driving the clappers. You
can substitute some other audio files for fun.

