@echo off
rem Compila o projeto (Windows). Requer JDK 11+.
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > fontes.txt
javac -encoding UTF-8 -d out -cp "lib\*" @fontes.txt
set "codigo=%ERRORLEVEL%"
del fontes.txt
if not "%codigo%"=="0" (
    echo Erro na compilacao.
    exit /b %codigo%
)
echo Compilado com sucesso.
