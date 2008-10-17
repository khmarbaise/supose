@echo off

::SET BP=C:/

SET CP=
SET CP=%CP%;.
SET CP=%CP%;.\lib\commons-cli-2.0-SNAPSHOT.jar
SET CP=%CP%;.\lib\commons-collections-3.1.jar
SET CP=%CP%;.\lib\commons-logging-1.0.4.jar
SET CP=%CP%;.\lib\commons-logging-api.jar
SET CP=%CP%;.\lib\ganymed.jar
SET CP=%CP%;.\lib\log4j-1.2.11.jar
SET CP=%CP%;.\lib\bcmail-jdk14-132.jar
SET CP=%CP%;.\lib\bcprov-jdk14-132.jar
SET CP=%CP%;.\lib\FontBox-0.1.0-dev.jar
SET CP=%CP%;.\lib\lucene-core-2.3.1.jar
SET CP=%CP%;.\lib\PDFBox-0.7.3.jar
SET CP=%CP%;.\lib\poi-3.0.2-FINAL-20080204.jar
SET CP=%CP%;.\lib\poi-contrib-3.0.2-FINAL-20080204.jar
SET CP=%CP%;.\lib\poi-scratchpad-3.0.2-FINAL-20080204.jar
SET CP=%CP%;.\lib\quartz-all-1.6.0.jar
SET CP=%CP%;.\lib\ini4j-0.2.6.jar
SET CP=%CP%;.\lib\svnkit.jar
SET CP=%CP%;.\lib\ganymed.jar
SET CP=%CP%;.\lib\jbossall-client.jar
SET CP=%CP%;.\lib\antlr-2.7.7.jar
SET CP=%CP%;.\lib\antlr-3.0.jar
SET CP=%CP%;.\lib\stringtemplate-3.0.jar
SET CP=%CP%;.\lib\@SUPOSE@


java -Xms512m -Xmx1024m -cp %CP% com.soebes.supose.cli.SuposeCLI %*
