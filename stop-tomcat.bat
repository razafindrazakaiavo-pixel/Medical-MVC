@echo off
REM Arrete le Tomcat utilise par start-tomcat.bat.
setlocal
if not defined JAVA_HOME set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.14.7-hotspot"
if not defined TOMCAT_HOME set "TOMCAT_HOME=D:\tomcat\apache-tomcat-10.1.55-windows-x64\apache-tomcat-10.1.55"
set "CATALINA_HOME=%TOMCAT_HOME%"
set "CATALINA_BASE=%TOMCAT_HOME%"
if not exist "%TOMCAT_HOME%\bin\shutdown.bat" (
    echo Tomcat introuvable : %TOMCAT_HOME%
    exit /b 1
)
call "%TOMCAT_HOME%\bin\shutdown.bat"
exit /b %errorlevel%
