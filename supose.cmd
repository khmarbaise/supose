@echo off

::SET BP=C:/

SET CP=
SET CP=%CP%;.
SET CP=%CP%;.\lib\antlr-2.7.7.jar
SET CP=%CP%;.\lib\antlr-3.0.jar
SET CP=%CP%;.\lib\asm-3.1.jar
SET CP=%CP%;.\lib\bcmail-jdk14-136.jar
SET CP=%CP%;.\lib\bcprov-jdk14-136.jar
SET CP=%CP%;.\lib\commons-cli-2.0-DEV.jar
SET CP=%CP%;.\lib\commons-codec-1.3.jar
SET CP=%CP%;.\lib\commons-io-1.4.jar
SET CP=%CP%;.\lib\commons-lang-2.1.jar
SET CP=%CP%;.\lib\commons-logging-1.1.jar
SET CP=%CP%;.\lib\commons-collections-3.1.jar
SET CP=%CP%;.\lib\fontbox-0.1.0.jar
SET CP=%CP%;.\lib\icu4j-3.8.jar
SET CP=%CP%;.\lib\ini4j-0.4.0-jdk14.jar
SET CP=%CP%;.\lib\jempbox-0.2.0.jar
SET CP=%CP%;.\lib\log4j-1.2.14.jar
SET CP=%CP%;.\lib\lucene-core-2.4.0.jar
SET CP=%CP%;.\lib\nekohtml-1.9.9.jar
SET CP=%CP%;.\lib\pdfbox-0.7.3.jar
SET CP=%CP%;.\lib\poi-3.1-FINAL.jar
SET CP=%CP%;.\lib\poi-scratchpad-3.1-FINAL.jar
SET CP=%CP%;.\lib\quartz-${quartz.version}.jar
SET CP=%CP%;.\lib\stringtemplate-3.0.jar
SET CP=%CP%;.\lib\jna-3.0.9.jar
SET CP=%CP%;.\lib\supose-${project.version}.jar
SET CP=%CP%;.\lib\svnkit-1.2.2.5405.jar
SET CP=%CP%;.\lib\tika-0.2.jar
SET CP=%CP%;.\lib\trilead-ssh2-build213-svnkit-1.2-patch.jar
SET CP=%CP%;.\lib\xercesImpl-2.8.1.jar
SET CP=%CP%;.\lib\xml-apis-1.3.03.jar

java -Xms512m -Xmx1024m -cp %CP% com.soebes.supose.cli.SuposeCLI %*
