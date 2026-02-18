@echo off
set "PROVIDED_JAVA_HOME=%~1"

title Football Manager Backend

echo ==========================================
echo   Starting Backend Service
echo ==========================================

REM 1. Set Java Environment
if not "%PROVIDED_JAVA_HOME%"=="" (
    echo [INFO] Using provided JDK: %PROVIDED_JAVA_HOME%
    set "JAVA_HOME=%PROVIDED_JAVA_HOME%"
    set "PATH=%PROVIDED_JAVA_HOME%\bin;%PATH%"
) else (
    echo [INFO] Using system Java (if available)
)

REM 2. Set Maven Environment
REm This path matches what setup_maven.ps1 uses
set "MAVEN_BIN=%~dp0.tools\apache-maven-3.9.6\bin"
if exist "%MAVEN_BIN%" (
    echo [INFO] Adding local Maven to PATH: %MAVEN_BIN%
    set "PATH=%MAVEN_BIN%;%PATH%"
)

REM 3. Run Application
cd football-manager-api
echo.
echo Executing: mvn spring-boot:run
echo.
mvn spring-boot:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Backend failed to start.
    echo Common reasons:
    echo  - Port 8080 is already in use
    echo  - Maven dependencies failed to download
    pause
)
