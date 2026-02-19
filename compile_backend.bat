@echo off
set "JAVA_HOME=%~dp0.tools\jdk-17"
set "MAVEN_HOME=%~dp0.tools\apache-maven-3.9.6"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

echo JAVA_HOME=%JAVA_HOME%
echo Maven=%MAVEN_HOME%

cd football-manager-api
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo Compilation Failed!
    exit /b 1
)
echo Compilation Success!
