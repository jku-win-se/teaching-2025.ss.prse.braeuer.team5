@echo off
set PATH_TO_FX="C:\Users\magda\javafx-sdk-21.0.7\lib"
set PATH_TO_JAR="%~dp0Lunchify.jar"
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -jar %PATH_TO_JAR% pause

