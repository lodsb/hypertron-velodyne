VERSION=1.25
#JCC=javac -source 1.5 -target 1.5
JCC=javac
SOURCEDIR=jass
RELDIR=jass-sdk
DATE = `date +%d%b-%Hh%M-%Y`
WWW=jass_www
WSYSDIR=C:/WINDOWS/SYSTEM
CYGBIN = c:/cygnusnext/bin

all:
	javac $(SOURCEDIR)/*/*.java examples/*/*.java
#	javac -source 1.5 -target 1.5 $(SOURCEDIR)/*/*.java examples/*/*.java

lint:
	javac -source 1.5 -target 1.5 -Xlint:unchecked $(SOURCEDIR)/*/*.java examples/*/*.java

html:
	echo "<HTML><HEAD><TITLE>Jass demos</TITLE></HEAD><BODY>" > $(RELDIR)/jass.html
	echo "<H1> Real-time audio synthesis using JASS</H1><p>" >> $(RELDIR)/jass.html
	echo "<img src=classes.jpg><p>" >> $(RELDIR)/jass.html	
	echo "<small>" >> $(RELDIR)/jass.html
	echo "JASS Version $(VERSION)<br>" >> $(RELDIR)/jass.html
	echo "Build date: `date`<p>" >> $(RELDIR)/jass.html
	echo "</small>" >> $(RELDIR)/jass.html
	cat examples/jass.html >> $(RELDIR)/jass.html

native:
	javah -d "platform/include" "jass.render.RTPlay"
	javah -d "platform/include" "jass.render.RTAudioIn"
	javah -d "platform/include" "jass.render.RTAudioFullDuplexRtAudio"
	javah -d "platform/include" "jass.render.MicroTime"

ex:
	javac -source 1.5 -target 1.5 examples/*/*.java

jar:
	jar cvf examples/jar/jass.jar $(SOURCEDIR)/engine/*.class $(SOURCEDIR)/render/*.class $(SOURCEDIR)/generators/*.class $(SOURCEDIR)/patches/*.class $(SOURCEDIR)/contact/*.class $(SOURCEDIR)/utils/*.class
	cp examples/jar/jass.jar ../artisynth/artisynth_2_0/lib/

rel:
	make clean
	rm -f $(RELDIR)
	javac -source 1.5 -target 1.5 $(SOURCEDIR)/engine/*.java $(SOURCEDIR)/render/*.java $(SOURCEDIR)/generators/*.java $(SOURCEDIR)/patches/*.java $(SOURCEDIR)/contact/*.java $(SOURCEDIR)/utils/*.java examples/*/*.java
	make jar	
	make native
	javadoc -author -d doc jass.engine jass.generators jass.render jass.patches jass.contact jass.utils
	mkdir $(RELDIR)
	mkdir $(RELDIR)/$(SOURCEDIR)
	cp -r $(SOURCEDIR)/engine $(RELDIR)/$(SOURCEDIR)/
	cp -r $(SOURCEDIR)/render $(RELDIR)/$(SOURCEDIR)/
	cp -r $(SOURCEDIR)/generators $(RELDIR)/$(SOURCEDIR)/
	cp -r $(SOURCEDIR)/patches $(RELDIR)/$(SOURCEDIR)/
	cp -r $(SOURCEDIR)/contact $(RELDIR)/$(SOURCEDIR)/
	cp -r $(SOURCEDIR)/utils $(RELDIR)/$(SOURCEDIR)/
	cp -r doc $(RELDIR)/
	cp -r platform $(RELDIR)/
	cp -r examples $(RELDIR)/
	make html
	echo "JASS Version $(VERSION)" > $(RELDIR)/info.txt
	echo "Build date:" >> $(RELDIR)/info.txt
	date >> $(RELDIR)/info.txt
	cp readme.txt $(RELDIR)/
	cp makefile $(RELDIR)/
	mkdir $(WWW)
	rm -r $(RELDIR)/examples/data/*
	mv $(RELDIR)/jass.html .
	zip -r $(WWW)/jass_nodata.zip $(RELDIR)/*
	cp -r examples/data $(RELDIR)/examples/
	zip -r $(WWW)/jass.zip $(RELDIR)/*
	cp -r examples/* $(WWW)/
	cp jass.html $(WWW)/jass.html
	mv jass.html $(WWW)/index.html
	cp $(RELDIR)/info.txt $(WWW)/
	cp -r doc $(WWW)/
	rm -rf $(WWW)/*_noApplet
	rm -f $(WWW)/*/makefile	
	rm -f $(WWW)/*/*.java

backup:
	make clean
	zip -r jass$(VERSION)-$(DATE).zip *

docs:
	javadoc -author -d doc jass.engine jass.generators jass.render jass.patches jass.contact jass.utils

clean:
	'rm' -rf *~ *.class */*~ */*.class */*/*~ */*/*.class doc/*.html doc/jass doc/resource doc/pack* doc/*.css \#* */\#* */*/*\# *.zip *.raw *.pk $(RELDIR) $(WWW) examples/*/*.dll *.zip */.#* */*/.#* */*/*/.#* */*/*/*/.#* */*/*/*/*/.#* */*/core examples/*/*.log examples/*/*.dll examples/*/*.so
	'rm' -f platform/linux/lib/*.o platform/linux/lib/alsa/*.o platform/linux/lib/oss/*.o platform/include/*.h
	'rm' -rf platform/windows/*/*.opt platform/windows/*/*.plg platform/windows/*/*.ncb platform/windows/*/*.o platform/windows/*/*/*.opt platform/windows/*/*/*.plg platform/windows/*/*/*.ncb platform/windows/*/*/*.o platform/windows/*/*/*/*.opt platform/windows/*/*/*/*.plg platform/windows/*/*/*/*.ncb platform/windows/*/*/*/*.o platform/windows/*/*/*/*/*.opt platform/windows/*/*/*/*/*.plg platform/windows/*/*/*/*/*.ncb platform/windows/*/*/*/*/*.o
	echo " " >tmp.obj
	'rm' `find . -name "*.obj"`
	echo " " >tmp.pch
	'rm' `find . -name "*.pch"`
	echo " " >tmp.idb
	'rm' `find . -name "*.idb"`
	echo " " >tmp.pdb
	'rm' `find . -name "*.pdb"`
	echo " " >tmp.ilk
	'rm' `find . -name "*.ilk"`
	echo " " >tmp.exe
	'rm' `find . -name "*.exe"`





