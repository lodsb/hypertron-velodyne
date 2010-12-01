all:
	javac -target 1.5 -source 1.5 -classpath ".$(CPSEP)../../" *.java

r:
	java -classpath ".$(CPSEP)../../" PottsDemo ../data/clap11025.wav .5 200

r1:
	java -classpath ".$(CPSEP)../../" PottsDemo ../data/sine11025.wav 1 40

r4:
	java -classpath ".$(CPSEP)../../" PottsDemo ../data/clap11025.wav 4 50

r15:
	java -classpath ".$(CPSEP)../../" PottsDemo ../data/clap11025.wav 15 20


s:
	cp PottsDemo.java rel/
	cp PottsModel.class rel/
	cp PottsModelBase.java rel/
