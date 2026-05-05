@echo off
setlocal

set "TOMCAT=C:\Tomcat10\apache-tomcat-10.1.24"
set "CONTEXT=Forage"
set "OLD_CONTEXT=ManagerApp"
set "WARPATH=%TOMCAT%\webapps\%CONTEXT%.war"
set "ZIPPATH=%TEMP%\%CONTEXT%.zip"

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
if /I not "%OLD_CONTEXT%"=="%CONTEXT%" (
    if exist "%TOMCAT%\webapps\%OLD_CONTEXT%.war" del /f /q "%TOMCAT%\webapps\%OLD_CONTEXT%.war"
    if exist "%TOMCAT%\webapps\%OLD_CONTEXT%" rmdir /s /q "%TOMCAT%\webapps\%OLD_CONTEXT%"
)

echo Building project (mvn clean package)...
cd /d "%~dp0" >nul
call mvn clean package
if errorlevel 1 (
  echo Maven build failed; will attempt WAR creation from current folder
  set BUILD_FAILED=1
)

REM If Maven produced a WAR in target, use it; otherwise fall back to jar/zip of current dir
if not defined BUILD_FAILED (
  for %%f in (target\*.war) do set WARFILE=%%f
)

if defined WARFILE (
  echo Using built WAR: %WARFILE%
  copy /Y "%WARFILE%" "%WARPATH%" >nul || goto :ERR_BUILD
  goto :BUILD_DONE
)

echo Creating WAR from current directory...
pushd "%~dp0" || goto :ERR_BUILD
where jar >nul 2>&1
if errorlevel 1 goto :ZIP_FALLBACK

echo Using jar to create WAR at "%WARPATH%"...
jar -cf "%WARPATH%" . || goto :ERR_BUILD_POP
goto :BUILD_DONE

:ZIP_FALLBACK
echo jar not found in PATH, using PowerShell ZIP fallback...
if exist "%ZIPPATH%" del /f /q "%ZIPPATH%"
powershell -NoProfile -Command "Compress-Archive -Path * -DestinationPath '%ZIPPATH%' -Force" || goto :ERR_BUILD_POP
move /y "%ZIPPATH%" "%WARPATH%" >nul || goto :ERR_BUILD_POP

:BUILD_DONE
popd

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

