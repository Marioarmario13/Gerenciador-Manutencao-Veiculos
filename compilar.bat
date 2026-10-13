@echo off
rem Compila o projeto (Windows). Requer JDK 11+.
if not exist out mkdir out
dir /s /b src\*.java > fontes.txt
javac -encoding UTF-8 -d out -cp "lib\*" @fontes.txt
del fontes.txt
echo Concluido.
pause
