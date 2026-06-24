@REM Maven Wrapper script for Windows
@REM Downloads and runs Maven if not already cached

@echo off
setlocal

set WRAPPER_VERSION=3.9.9
set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%WRAPPER_VERSION%

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Downloading Maven %WRAPPER_VERSION%...
    mkdir "%MAVEN_HOME%" 2>nul
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%WRAPPER_VERSION%/apache-maven-%WRAPPER_VERSION%-bin.zip' -OutFile '%TEMP%\maven.zip' -UseBasicParsing"
    powershell -Command "Expand-Archive -Path '%TEMP%\maven.zip' -DestinationPath '%TEMP%\maven' -Force"
    xcopy /E /I /Q /Y "%TEMP%\maven\apache-maven-%WRAPPER_VERSION%\*" "%MAVEN_HOME%\" >nul
    del "%TEMP%\maven.zip" 2>nul
    rmdir /S /Q "%TEMP%\maven" 2>nul
    echo Maven %WRAPPER_VERSION% installed.
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
