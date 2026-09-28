@echo off
TITLE Tienda FernanShop
cls
echo =========================================
echo       Iniciando Tienda FernanShop
echo =========================================

IF NOT EXIST bin (
    mkdir bin
)

echo Compilando codigo fuente...
dir /s /B src\main\java\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt

cls
java -cp bin Main

echo.
pause
