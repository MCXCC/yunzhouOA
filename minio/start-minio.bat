@echo off
setlocal enabledelayedexpansion

echo MinIO Server Starter
echo ====================

:: Set MinIO configuration
set MINIO_ROOT_USER=minioadmin
set MINIO_ROOT_PASSWORD=minioadmin

:: Set data directory (change this path as needed)
set MINIO_DATA_DIR=%~dp0data

:: Set console and API addresses
set MINIO_BROWSER_ADDRESS=:9001
set MINIO_ADDRESS=:9000

:: Create data directory if not exists
if not exist "%MINIO_DATA_DIR%" (
    mkdir "%MINIO_DATA_DIR%"
    echo Created data directory: %MINIO_DATA_DIR%
)

:: Check if MinIO executable exists
set MINIO_EXE=%~dp0minio.windows-amd64.RELEASE.2025-09-07T16-13-09Z.exe
if not exist "%MINIO_EXE%" (
    echo ERROR: MinIO executable not found at: %MINIO_EXE%
    pause
    exit /b 1
)

:: Display configuration
echo.
echo Configuration:
echo   MinIO Root User: %MINIO_ROOT_USER%
echo   MinIO Root Password: %MINIO_ROOT_PASSWORD%
echo   Data Directory: %MINIO_DATA_DIR%
echo   API Address: http://localhost:9000
echo   Console Address: http://localhost:9001
echo.

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