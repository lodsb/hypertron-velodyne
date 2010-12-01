RM = /bin/rm
SRC_PATH = ../
JAVADIR = /usr/java/j2sdk1.4.1_01 	
INCLUDE = -I$(JAVADIR)/include -I$(JAVADIR)/include/linux
OBJECT_PATH = .

CC       = g++
DEFS     = 
CFLAGS   = -O2
CFLAGS  += -Wall $(INCLUDE)
LIBRARY += 


all: java native

java:
	javac -classpath ".$(CPSEP)../../" *.java


native: java
	javah -classpath ".:./jass.jar" Groove
	$(CC) $(CFLAGS) $(DEFS) -shared -o libGroove.so Groove.cpp

#$(CC) $(CFLAGS) $(DEFS) -shared -o $(OBJECT_PATH)/libGroove.so Groove.cpp

run:
	java -classpath ".$(CPSEP)../../" GrooveDemo 32 ../data/256 bier-1min.wav

# run with javasound
runjs:
	java -classpath ".$(CPSEP)../../" GrooveDemo 32 0 ../data/bier-1min.wav

clean:
	rm -f *.class *.o Groove.h *.log *~
