@echo off
REM Generar JavaDoc per a Politècnics Football Manager

echo ╔════════════════════════════════════════════════════════════════╗
echo ║      📚 GENERAR JAVADOC - Football Manager                   ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

echo 📁 Creant directori docs...
if not exist docs mkdir docs

echo.
echo 📝 Generant documentació JavaDoc...

javadoc -d docs -sourcepath src -subpackages model:comparators:utils -encoding UTF-8 -charset UTF-8 -docencoding UTF-8

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Error generant JavaDoc!
    pause
    exit /b 1
)

echo.
echo ✅ JavaDoc generat amb èxit!
echo.
echo 📂 La documentació està a: docs\index.html
echo.
echo 🌐 Obrint JavaDoc al navegador...
start docs\index.html

pause
