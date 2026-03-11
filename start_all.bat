@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo   FOOTBALL MANAGER - ROBUST STARTUP SCRIPT
echo ===================================================

set "ROOT_DIR=%~dp0"
set "TOOLS_DIR=%ROOT_DIR%.tools"
set "JDK_DIR=%TOOLS_DIR%\jdk-17"
set "MAVEN_DIR=%TOOLS_DIR%\apache-maven-3.9.6"

echo [1/3] Configuring Environment...

REM Check JDK
if not exist "%JDK_DIR%\bin\java.exe" (
    echo [INFO] JDK not found at %JDK_DIR%, trying setup...
    powershell -ExecutionPolicy Bypass -File "setup_jdk.ps1"
)

if not exist "%JDK_DIR%\bin\java.exe" (
    echo [ERROR] JDK 17 could not be found or installed!
    pause
    exit /b 1
)

set "JAVA_HOME=%JDK_DIR%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Check Maven
if not exist "%MAVEN_DIR%\bin\mvn.cmd" (
    echo [INFO] Maven not found, trying setup...
    powershell -ExecutionPolicy Bypass -File "setup_maven.ps1" -JavaHomeParam "%JAVA_HOME%"
)

if not exist "%MAVEN_DIR%\bin\mvn.cmd" (
    echo [ERROR] Maven could not be found or installed!
    pause
    exit /b 1
)

set "MAVEN_HOME=%MAVEN_DIR%"
set "PATH=%MAVEN_HOME%\bin;%PATH%"

echo [OK] Environment configured correctly.
echo     JAVA_HOME: %JAVA_HOME%
echo     Maven:     %MAVEN_HOME%
echo.

REM 2. Start Backend
echo [2/3] Starting Backend in new window...
start "Football Manager Backend" cmd /k "set JAVA_HOME=%JDK_DIR%&& set PATH=%JDK_DIR%\bin;%MAVEN_DIR%\bin;%PATH%&& cd /d %ROOT_DIR%football-manager-api && echo Starting Spring Boot... && mvn spring-boot:run"

REM Wait for backend to at least start booting
timeout /t 5 /nobreak >nul

REM 3. Start Frontend
echo [3/3] Starting Frontend in new window...
start "Football Manager Client" cmd /k "cd /d %ROOT_DIR%football-manager-client && echo Starting Next.js... && npm run dev"

echo.
echo ===================================================
echo   Services are launching!
echo   Frontend: http://localhost:3000
echo   Backend API: http://localhost:8080/api
echo ===================================================
echo   Check the other two windows for logs/errors.
echo.
pause
