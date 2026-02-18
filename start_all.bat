@echo off
echo ===================================================
echo   FOOTBALL MANAGER - STARTUP SCRIPT
echo ===================================================
echo.

REM 1. Setup JDK 17 (Crucial for Lombok/Spring Boot compatibility)
echo [1/4] Ensuring JDK 17 is installed...
powershell -ExecutionPolicy Bypass -File "setup_jdk.ps1"
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to download JDK 17.
    pause
    exit /b 1
)

REM Set JAVA_HOME to the downloaded portable JDK
set "LOCAL_JAVA_HOME=%~dp0.tools\jdk-17"
echo [INFO] Using Portable JDK: %LOCAL_JAVA_HOME%

REM Validate
if not exist "%LOCAL_JAVA_HOME%\bin\java.exe" (
    echo [ERROR] Expected JDK at %LOCAL_JAVA_HOME% but not found.
    pause
    exit /b 1
)

REM 2. Setup Maven
echo.
echo [2/4] Checking Maven...
REM Pass our portable JDK to setup_maven to ensure it validates against the *correct* Java
powershell -ExecutionPolicy Bypass -File "setup_maven.ps1" -JavaHomeParam "%LOCAL_JAVA_HOME%"

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to setup Maven.
    pause
    exit /b 1
)

REM 3. Start Backend
echo.
echo [3/4] Starting Backend...
REM Use start_backend.bat with our portable JDK
start "Football Manager Backend" cmd /k "start_backend.bat "%LOCAL_JAVA_HOME%""

REM 4. Start Frontend
echo.
echo [4/4] Starting Frontend...
cd football-manager-client
start "Football Manager Client" cmd /k "echo Starting Next.js... & npm run dev"
cd ..

echo.
echo ===================================================
echo   Services are launching...
echo   Frontend: http://localhost:3000
echo   Backend:  http://localhost:8080/api
echo ===================================================
pause
