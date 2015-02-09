@echo off

setlocal
set DIRNAME=%~dp0%
set ARGS=%*

call "%DIRNAME%server.sh" %ARGS%
