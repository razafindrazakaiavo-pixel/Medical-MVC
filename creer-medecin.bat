@echo off
setlocal
cd /d "%~dp0"
set "JAVA_EXE=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot\bin\java.exe"
if not exist "%JAVA_EXE%" set "JAVA_EXE=java"
if "%~1"=="" (
    echo Usage : creer-medecin.bat identifiant
    exit /b 1
)
"%JAVA_EXE%" -cp "build\classes;lib\*" iavo.main.service.UserService "%~1"
exit /b %errorlevel%
