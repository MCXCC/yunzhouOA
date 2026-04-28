@echo off
setlocal

echo Starting MinIO Server
echo ====================

:: Set MinIO configuration
set MINIO_ROOT_USER=minioadmin
set MINIO_ROOT_PASSWORD=minioadmin123

:: Get script directory
set SCRIPT_DIR=%~dp0

:: Set data directory
set MINIO_DATA_DIR=%SCRIPT_DIR%data

:: Check if MinIO executable exists
set MINIO_EXE=%SCRIPT_DIR%minio.windows-amd64.RELEASE.2025-09-07T16-13-09Z.exe
if not exist "%MINIO_EXE%" (
    echo ERROR: MinIO executable not found!
    echo Expected at: %MINIO_EXE%
    pause
    exit /b 1
)

:: Create data directory if not exists
if not exist "%MINIO_DATA_DIR%" (
    mkdir "%MINIO_DATA_DIR%"
)

:: Display configuration
echo.
echo Configuration:
echo   Username: %MINIO_ROOT_USER%
echo   Password: %MINIO_ROOT_PASSWORD%
echo   Data Dir: %MINIO_DATA_DIR%
echo   API:      http://localhost:9000
echo   Console:  http://localhost:9001
echo.
echo Starting server...
echo Press Ctrl+C to stop
echo.

:: Start MinIO
"%MINIO_EXE%" server "%MINIO_DATA_DIR%" --address :9000 --console-address :9001

:: Pause on error
if errorlevel 1 (
    echo.
    echo Server stopped with error: %errorlevel%
    pause
)