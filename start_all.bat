@echo off
setlocal
echo ===================================================
echo   FOOTBALL MANAGER - STARTUP SCRIPT
echo ===================================================
echo.

set "ROOT_DIR=%~dp0"
cd /d "%ROOT_DIR%"

REM 1. Setup JDK 17
echo [1/4] Ensuring JDK 17 is installed...
powershell -ExecutionPolicy Bypass -File "setup_jdk.ps1"
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to download JDK 17.
    pause
    exit /b 1
)

set "LOCAL_JAVA_HOME=%ROOT_DIR%.tools\jdk-17"
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
powershell -ExecutionPolicy Bypass -File "setup_maven.ps1" -JavaHomeParam "%LOCAL_JAVA_HOME%"

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to setup Maven.
    pause
    exit /b 1
)

REM 3. Start Backend
echo.
echo [3/4] Starting Backend...
REM We use /D to specify the working directory for the new process to be the root, 
REM so it can find start_backend.bat and the football-manager-api folder.
start "Football Manager Backend" /D "%ROOT_DIR%" cmd /k "call start_backend.bat "%LOCAL_JAVA_HOME%""

REM Wait a bit for backend to initialize (optional, but nice)
timeout /t 5 /nobreak >nul

REM 4. Start Frontend
echo.
echo [4/4] Starting Frontend...
if exist "%ROOT_DIR%football-manager-client" (
    start "Football Manager Client" /D "%ROOT_DIR%football-manager-client" cmd /k "echo Starting Next.js... & npm run dev"
) else (
    echo [ERROR] football-manager-client directory not found!
    pause
)

echo.
echo ===================================================
echo   Services are launching in separate windows.
echo   Frontend: http://localhost:3000
echo   Backend:  http://localhost:8080/api
echo ===================================================
echo   You can close this window now, or keep it open.
pause
