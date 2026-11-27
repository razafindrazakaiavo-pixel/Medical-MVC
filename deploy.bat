@echo off
REM Medical - compilation et deploiement vers Tomcat 10.1
setlocal
cd /d "%~dp0"

if not defined TOMCAT_HOME set "TOMCAT_HOME=D:\tomcat\apache-tomcat-10.1.55-windows-x64\apache-tomcat-10.1.55"

 echo [1/2] Compilation du projet...
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0build.ps1"
if errorlevel 1 (
    echo Echec de la compilation.
    exit /b 1
)

if /i "%~1"=="--build-only" (
    echo Compilation terminee : Medical.war
    exit /b 0
)

 echo [2/2] Deploiement vers Tomcat...
if not exist "%TOMCAT_HOME%\webapps\" (
    echo Dossier Tomcat introuvable : %TOMCAT_HOME%\webapps
    exit /b 1
)
copy /y "%~dp0Medical.war" "%TOMCAT_HOME%\webapps\Medical.war" >nul
if errorlevel 1 (
    echo Echec de la copie du WAR vers Tomcat.
    exit /b 1
)
echo Medical.war deploye.
echo Demarrez Tomcat avec start-tomcat.bat si necessaire.
echo Application : http://localhost:8081/Medical/index.html
echo Adaptez le port si votre configuration Tomcat est differente.
endlocal
