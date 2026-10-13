#!/bin/bash
# Compila o projeto (Linux/Mac). Requer JDK 11+.
mkdir -p out
javac -encoding UTF-8 -d out -cp "lib/*" $(find src -name "*.java") && echo "Compilado com sucesso."
