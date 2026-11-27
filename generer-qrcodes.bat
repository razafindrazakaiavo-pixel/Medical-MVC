@echo off
REM Genere les QR codes manquants des patients de la base.
setlocal
cd /d "%~dp0"
set "JAVA_EXE=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot\bin\java.exe"
if not exist "%JAVA_EXE%" set "JAVA_EXE=java"
"%JAVA_EXE%" -cp "build\classes;lib\*" iavo.main.service.QrCodeService
exit /b %errorlevel%
