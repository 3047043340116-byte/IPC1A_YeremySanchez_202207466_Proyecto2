@echo off
cd /d "%~dp0"
java -jar dist\ResiPark.jar
if errorlevel 1 pause
