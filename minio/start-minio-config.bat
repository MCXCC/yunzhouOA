@echo off
setlocal enabledelayedexpansion

echo MinIO Server Starter (Config File Version)
echo ===========================================

:: Set script directory
set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%"

:: Check if MinIO executable exists
set MINIO_EXE=%SCRIPT_DIR%minio.windows-amd64.RELEASE.2025-09-07T16-13-09Z.exe
if not exist "%MINIO_EXE%" (
    echo ERROR: MinIO executable not found at: %MINIO_EXE%
    echo Please ensure minio.windows-amd64.RELEASE.2025-09-07T16-13-09Z.exe is in the same directory.
    pause
    exit /b 1
)

:: Check if config file exists
if not exist "%SCRIPT_DIR%config.env" (
    echo ERROR: Configuration file not found at: %SCRIPT_DIR%config.env
    echo Please create config.env file first.
    pause
    exit /b 1
)

:: Load configuration from config.env
echo Loading configuration from config.env...
for /f "tokens=1,* delims==" %%a in ('type "%SCRIPT_DIR%config.env" ^| findstr /v "^#" ^| findstr /v "^$"') do (
    set "%%a=%%b"
    set "%%a=!%%a:%%~dp0=%SCRIPT_DIR%!"
)

:: Replace relative paths with absolute paths
if defined MINIO_DATA_DIR (
    set "MINIO_DATA_DIR=!MINIO_DATA_DIR:./=%SCRIPT_DIR%!"
    set "MINIO_DATA_DIR=!MINIO_DATA_DIR:\=/!"
)

:: Set defaults if not defined
if not defined MINIO_ROOT_USER set MINIO_ROOT_USER=minioadmin
if not defined MINIO_ROOT_PASSWORD set MINIO_ROOT_PASSWORD=minioadmin123
if not defined MINIO_DATA_DIR set MINIO_DATA_DIR=%SCRIPT_DIR%data
if not defined MINIO_ADDRESS set MINIO_ADDRESS=:9000
if not defined MINIO_BROWSER_ADDRESS set MINIO_BROWSER_ADDRESS=:9001

:: Create data directory if not exists
if not exist "%MINIO_DATA_DIR%" (
    mkdir "%MINIO_DATA_DIR%"
    echo Created data directory: %MINIO_DATA_DIR%
)

:: Display configuration
echo.
echo Configuration:
echo   MinIO Root User: %MINIO_ROOT_USER%
echo   MinIO Root Password: %MINIO_ROOT_PASSWORD%
echo   Data Directory: %MINIO_DATA_DIR%
echo   API Address: http://localhost%MINIO_ADDRESS%
echo   Console Address: http://localhost%MINIO_BROWSER_ADDRESS%
echo.

:: Set environment variables for MinIO
set MINIO_ROOT_USER=%MINIO_ROOT_USER%
set MINIO_ROOT_PASSWORD=%MINIO_ROOT_PASSWORD%

:: Start MinIO server
echo Starting MinIO server...
echo Press Ctrl+C to stop the server
echo.

"%MINIO_EXE%" server "%MINIO_DATA_DIR%" --address %MINIO_ADDRESS% --console-address %MINIO_BROWSER_ADDRESS%

:: If MinIO exits, pause to show any error messages
if errorlevel 1 (
    echo.
    echo MinIO server stopped with error code: %errorlevel%
    pause
)