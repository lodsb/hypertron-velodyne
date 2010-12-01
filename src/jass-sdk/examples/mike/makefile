all:
	javac -classpath ".$(CPSEP)../../" *.java

r1:
	java -classpath ".$(CPSEP)../../" MikeIn javasound 64 100000 0 "outmixer" "Java Sound Audio Engine"

r2:
	java -classpath ".$(CPSEP)../../" MikeIn rtaudio  1024 0 -1 "foo"

r3:
	java -classpath ".$(CPSEP)../../" MikeIn asio 256 -1 -1 "foo"

r4:
	java -classpath ".$(CPSEP)../../" MikeIn rtaudioFullDuplex 1024 4 -1 "foo"
