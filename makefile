JAVAC = javac
JFLAGS = -g

PACKAGE = src/edu/fit/cs/cn/

default: Compile.class

Compile.class: $(PACKAGE)main/FPMain.java
	$(JAVAC) -d bin $(JFLAGS) $(PACKAGE)/*/*.java 

clean: 
	$(RM) -r bin/*
