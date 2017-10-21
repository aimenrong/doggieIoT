@echo off

rem Using pushd popd to set BASE_DIR to the absolute path
pushd %~dp0..\..
set BASE_DIR=%CD%
echo BASE_DIR = %BASE_DIR%
popd

set CLASSPATH=%BASE_DIR%/conf

rem Which java to use
IF ["%JAVA_HOME%"] EQU [""] (
	set JAVA=java
) ELSE (
	set JAVA="%JAVA_HOME%/bin/java"
)

set COMMAND=%JAVA% -Dconfig.path=%BASE_DIR%/conf/spc.properties -jar %BASE_DIR%/libs/StreamProcessCenter.jar
rem echo.
rem echo %COMMAND%
rem echo.

%COMMAND%