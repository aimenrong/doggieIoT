@echo off
set KAFKA_HOME=%cd%/..
%KAFKA_HOME%/bin/windows/kafka-server-start.bat %KAFKA_HOME%/config/windows/server.properties