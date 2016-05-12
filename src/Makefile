JAVAC=javac
JAVAOPTS=-Xlint:unchecked
.PHONY: all

CLASS_FILES=Main.class GraphicalUserInterface.class Fetcher.class Runner.class AppConstants.class FileList.class FileListItem.class

all: RexuizLauncher.jar

%.class : %.java
	$(JAVAC) $(JAVAOPTS) $<

RexuizLauncher.jar : $(CLASS_FILES) MANIFEST.MF
	rm -rf JAR $@
	mkdir -p JAR/META-INF
	install -m 644 MANIFEST.MF JAR/META-INF
	install -m 644 $(CLASS_FILES) JAR
	cd JAR && zip -r ../$@ *
	rm -rf JAR
