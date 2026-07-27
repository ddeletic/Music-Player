@echo off

::------------------------------------------------------------------------------
::
:: Connect the phone via USB.
::
:: Enable Head Unit Server in your phone's Android Auto settings (3-dot menu).
::

adb forward tcp:5277 tcp:5277

C:/Users/dejan/AppData/Local/Android/Sdk/extras/google/auto/desktop-head-unit.exe


