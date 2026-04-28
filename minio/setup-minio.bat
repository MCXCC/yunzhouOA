@echo off
setlocal enabledelayedexpansion

echo MinIO Server Setup
echo ==================

:: Set script directory
set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%"

:: Check if MinIO executable exists
set MINIO_EXE=%SCRIPT_DIR%minio.windows-amd64.RELEASE.2025-09-07T16-13-09Z.exe
if not exist "%MINIO_EXE%" (
    echo ERROR: MinIO executable not found!
    echo Expected location: %MINIO_EXE%
    echo.
    echo Please ensure the following file exists:
    echo   minio.windows-amd64.RELEASE.2025-09-07T16-13-09Z.exe
    pause
    exit /b 1
)

echo ✓ MinIO executable found

:: Create default directories
if not exist "%SCRIPT_DIR%data" (
    mkdir "%SCRIPT_DIR%data"
    echo ✓ Created data directory
) else (
    echo ✓ Data directory already exists
)

:: Check if config file exists, create if not
if not exist "%SCRIPT_DIR%config.env" (
    echo ✓ Creating default config.env file
    (
        echo # MinIO Configuration File
        echo # ========================
        echo.
        echo # MinIO访问密钥和秘密密钥
        echo MINIO_ROOT_USER=minioadmin
        echo MINIO_ROOT_PASSWORD=minioadmin123
        echo.
        echo # 数据存储目录
        echo MINIO_DATA_DIR=./data
        echo.
        echo # 服务地址配置
        echo MINIO_ADDRESS=:9000
        echo MINIO_BROWSER_ADDRESS=:9001
    ) > "%SCRIPT_DIR%config.env"
) else (
    echo ✓ config.env already exists
)

:: Display setup summary
echo.
echo Setup completed successfully!
echo.
echo Configuration files created:
echo   - config.env (configuration file)
echo   - start-minio.bat (simple starter)
echo   - start-minio-config.bat (config file starter)
echo.
echo Default credentials:
echo   Username: minioadmin
echo   Password: minio123
echo.
echo To start MinIO server:
echo   Option 1: Run start-minio.bat
echo   Option 2: Run start-minio-config.bat
echo.
echo MinIO will be available at:
echo   API:      http://localhost:9000
echo   Console:  http://localhost:9001
echo.
pause