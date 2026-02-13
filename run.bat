@echo off
REM Politècnics Football Manager - Compilar i Executar
REM Aquest script compila i executa l'aplicació

echo ╔════════════════════════════════════════════════════════════════╗
echo ║      ⚽ POLITÈCNICS FOOTBALL MANAGER ⚽                        ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

echo 📁 Creant directori bin...
if not exist bin mkdir bin

echo.
echo 🔨 Compilant aplicació...
javac -d bin -sourcepath src -encoding UTF-8 src/Main.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Error en la compilació!
    echo.
    echo ⚠️ Assegura't que Java JDK està instal·lat i JAVA_HOME està configurat.
    echo.
    echo 💡 Per comprovar Java: java -version
    echo 💡 Per comprovar javac: javac -version
    echo.
    pause
    exit /b 1
)

echo ✅ Compilació completada amb èxit!
echo.
echo 🚀 Executant aplicació...
echo.

java -cp bin Main

echo.
echo 👋 Aplicació finalitzada.
pause
