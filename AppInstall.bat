@echo off
set MY_DIR=%~dp0
cd %MY_DIR%\app\build\outputs\apk\core\release\


::adb install .\musicplayer-4-core-release.apk



for /F "eol=| delims=" %%I in ('dir "*.apk" /A-D /B /O-D /TW 2^>nul') DO (
    set "APK_FILE=%%I"
    goto FoundHexFile
)
echo No *.apk file found!
goto :end

:FoundHexFile
echo Installing "<root>\app\build\outputs\apk\core\release\%APK_FILE%"
adb install .\%APK_FILE%


:end
pause
