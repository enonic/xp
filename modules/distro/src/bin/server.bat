@echo off

setlocal
set DIRNAME=%~dp0%
set PROGNAME=%~nx0%
set ARGS=%*

SET XP_SCRIPT=%PROGNAME%
if exist "%DIRNAME%setenv.bat" (
call "%DIRNAME%setenv.bat"
)

if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_EXE=%JAVA_HOME%\bin\java.exe
if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
set XP_INSTALL=%DIRNAME%..
set DEFAULT_JAVA_OPTS=-Djsse.enableSNIExtension=false
set DEFAULT_JAVA_DEBUG_OPTS=-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005

if "%JAVA_OPTS%" == "" set JAVA_OPTS=%DEFAULT_JAVA_OPTS%
if "%JAVA_DEBUG_OPTS%" == "" set JAVA_DEBUG_OPTS=%DEFAULT_JAVA_DEBUG_OPTS%

IF "%1"=="debug" set JAVA_OPTS=%JAVA_OPTS% %JAVA_DEBUG_OPTS%

:execute
"%JAVA_EXE%" %JAVA_OPTS% -Dxp.install="%XP_INSTALL%" -Dfile.encoding=UTF8 %XP_OPTS% -classpath "%XP_INSTALL%\lib\*" com.enonic.xp.launcher.LauncherMain %ARGS%
endlocal
