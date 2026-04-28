@echo off
setlocal enabledelayedexpansion

echo Stopping MinIO Server
echo ====================

:: Find and stop MinIO processes
echo Searching for MinIO processes...

:: Find minio processes
for /f "tokens=2 delims==" %%a in ('wmic process where "name like '%%minio%%'" get ProcessId /value 2^>nul ^| findstr /i "ProcessId"') do (
    set PID=%%a
    if defined PID (
        echo Found MinIO process with PID: !PID!
        taskkill /PID !PID! /F
        if !errorlevel! equ 0 (
            echo Successfully stopped MinIO process with PID: !PID!
        ) else (
            echo Failed to stop MinIO process with PID: !PID!
        )
    )
)

:: Alternative method: kill by image name
echo.
echo Checking for any remaining MinIO processes...
tasklist /FI "IMAGENAME eq minio*" 2>nul | findstr /I "minio" >nul
if !errorlevel! equ 0 (
    echo Found MinIO processes, attempting to stop them...
    taskkill /IM "minio*" /F
    if !errorlevel! equ 0 (
        echo Successfully stopped all MinIO processes
    ) else (
        echo Some MinIO processes could not be stopped
    )
) else (
    echo No MinIO processes found running
)

echo.
echo MinIO stop operation completed
pause