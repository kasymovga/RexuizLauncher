JAVAC=javac
JAVAOPTS=-Xlint:unchecked
.PHONY: all clean

CLASS_FILES=com/rexuiz/main/Main.class com/rexuiz/gui/GraphicalUserInterface.class com/rexuiz/main/Fetcher.class com/rexuiz/main/Runner.class com/rexuiz/main/AppConstants.class com/rexuiz/file/FileList.class com/rexuiz/file/FileListItem.class

all: RexuizLauncher.jar

%.class : %.java
	$(JAVAC) $(JAVAOPTS) $<

clean :
	rm -f $(CLASS_FILES) RexuizLauncher.jar

RexuizLauncher.jar : $(CLASS_FILES) MANIFEST.MF
	rm -f $@
	jar cfm $@ MANIFEST.MF `find -name \*class`
