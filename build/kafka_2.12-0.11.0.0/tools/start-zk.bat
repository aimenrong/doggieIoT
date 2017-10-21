@echo off
set KAFKA_HOME=%cd%/..
%KAFKA_HOME%/bin/windows/zookeeper-server-start.bat %KAFKA_HOME%/config/windows/zookeeper.properties