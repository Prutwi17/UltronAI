@echo off
@REM Maven Wrapper Script for Windows
@REM Executes Maven command using local Maven distribution

SET MAVEN_CMD=C:\Users\pruth\.m2\wrapper\dists\apache-maven-3.9.9-bin\d5e8846b\apache-maven-3.9.9\bin\mvn.cmd

IF EXIST "%MAVEN_CMD%" (
    "%MAVEN_CMD%" %*
) ELSE (
    mvn %*
)
