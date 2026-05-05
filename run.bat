@echo off
setlocal

set "TOMCAT=C:\Tomcat10\apache-tomcat-10.1.24"
set "CONTEXT=Forage"
set "OLD_CONTEXT=ManagerApp"
set "WARPATH=%TOMCAT%\webapps\%CONTEXT%.war"
set "ZIPPATH=%TEMP%\%CONTEXT%.zip"
set "WORKPATH=%TOMCAT%\work\Catalina\localhost\%CONTEXT%"

echo Deploy %CONTEXT% to %WARPATH%
if not exist "%TOMCAT%\bin\startup.bat" goto :ERR_TOMCAT

set "CATALINA_HOME=%TOMCAT%"
set "CATALINA_BASE=%TOMCAT%"
if not exist "%TOMCAT%\temp" mkdir "%TOMCAT%\temp"

echo Stopping Tomcat...
call "%TOMCAT%\bin\shutdown.bat" >nul 2>&1

echo Removing old deployment...
if exist "%WARPATH%" del /f /q "%WARPATH%"
if exist "%TOMCAT%\webapps\%CONTEXT%" rmdir /s /q "%TOMCAT%\webapps\%CONTEXT%"
if exist "%WORKPATH%" rmdir /s /q "%WORKPATH%"
if /I not "%OLD_CONTEXT%"=="%CONTEXT%" (
    if exist "%TOMCAT%\webapps\%OLD_CONTEXT%.war" del /f /q "%TOMCAT%\webapps\%OLD_CONTEXT%.war"
    if exist "%TOMCAT%\webapps\%OLD_CONTEXT%" rmdir /s /q "%TOMCAT%\webapps\%OLD_CONTEXT%"
)

echo Building project (mvn clean package)...
cd /d "%~dp0" >nul
call mvn clean package
if errorlevel 1 (
  echo Maven build failed.
  goto :ERR_BUILD
)

set "WARFILE=target\%CONTEXT%.war"
if not exist "%WARFILE%" (
  echo WAR not found: %WARFILE%
  goto :ERR_BUILD
)

echo Using built WAR: %WARFILE%
copy /Y "%WARFILE%" "%WARPATH%" >nul || goto :ERR_BUILD

echo Starting Tomcat...
call "%TOMCAT%\bin\startup.bat" || goto :ERR_START
echo Deployment termine. Ouvrez: http://localhost:8080/%CONTEXT%/
exit /b 0

:ERR_BUILD_POP
popd

:ERR_BUILD
echo Erreur lors de la creation du WAR.
exit /b 1

:ERR_START
echo Erreur au demarrage de Tomcat.
exit /b 1

:ERR_TOMCAT
echo Tomcat introuvable dans %TOMCAT%. Modifiez le chemin TOMCAT dans run.bat.
exit /b 1

