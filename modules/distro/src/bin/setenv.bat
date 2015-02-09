@echo off

rem
rem Handle specific scripts; the SCRIPT_NAME is exactly the name of the XP
rem script; for example server.bat, server-debug.bat, ...
rem
rem if "%XP_SCRIPT%" == "SCRIPT_NAME" (
rem   Actions go here...
rem )
rem

rem
rem General settings which should be applied for all scripts go here; please keep
rem in mind that it is possible that scripts might be executed more than once, e.g.
rem in example of the start script where the start script is executed first and the
rem karaf script afterwards.
rem

rem
rem The following section shows the possible configuration options for the default
rem XP scripts.
rem

rem Location of Java installation
rem SET JAVA_HOME

rem Java options
rem SET JAVA_OPTS

rem Java debug options
rem SET JAVA_DEBUG_OPTS

rem Enonic XP home folder
rem SET XP_HOME

rem Additional available Enonic XP options
rem SET XP_OPTS


