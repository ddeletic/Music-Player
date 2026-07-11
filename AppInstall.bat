@echo off
set MY_DIR=%~dp0
::set APK_DIR=app\core\release
set APK_DIR=app\build\outputs\apk\core\release
cd %MY_DIR%\%APK_DIR%\





for /F "eol=| delims=" %%I in ('dir "*.apk" /A-D /B /O-D /TW 2^>nul') DO (
    set "APK_FILE=%%I"
    goto FoundHexFile
)
echo No *.apk file found!
goto :end

:FoundHexFile
echo Installing "<root>\%APK_DIR%\%APK_FILE%"
adb install .\%APK_FILE%


:end
cd %MY_DIR%
pause
