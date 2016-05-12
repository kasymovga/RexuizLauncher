JAVAC=javac
JAVAOPTS=-Xlint:unchecked
.PHONY: all

CLASS_FILES=com.rexuiz.main.Main.class com.rexuiz.gui.GraphicalUserInterface.class com.rexuiz.main.Fetcher.class com.rexuiz.main.Runner.class com.rexuiz.main.AppConstants.class com.rexuiz.file.FileList.class com.rexuiz.file.FileListItem.class

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
