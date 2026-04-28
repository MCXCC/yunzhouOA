# MinIO Server Setup for Windows

## Quick Start

1. **Run setup first** (optional but recommended):
   ```
   setup-minio.bat
   ```

2. **Start MinIO server** (choose one):
   - `start-minio.bat` - Simple starter with default config
   - `start-minio-config.bat` - Starter that reads from config.env

## Files Description

- `minio.windows-amd64.RELEASE.2025-09-07T16-13-09Z.exe` - MinIO server executable
- `config.env` - Configuration file for MinIO settings
- `start-minio.bat` - Simple startup script with hardcoded defaults
- `start-minio-config.bat` - Startup script that reads from config.env
- `setup-minio.bat` - Setup script to create directories and default config
- `data/` - Default data storage directory (created automatically)

## Configuration

Edit `config.env` to change:
- **MINIO_ROOT_USER** - Admin username (default: minioadmin)
- **MINIO_ROOT_PASSWORD** - Admin password (default: minioadmin123)
- **MINIO_DATA_DIR** - Data storage directory (default: ./data)
- **MINIO_ADDRESS** - API address (default: :9000)
- **MINIO_BROWSER_ADDRESS** - Console address (default: :9001)

## Access MinIO

After starting, access MinIO at:
- **API Endpoint**: http://localhost:9000
- **Web Console**: http://localhost:9001

## Default Credentials

- **Username**: minioadmin
- **Password**: minioadmin123

⚠️ **Change these credentials in production!**

## Troubleshooting

1. **Port already in use**: Change MINIO_ADDRESS and MINIO_BROWSER_ADDRESS in config.env
2. **Permission denied**: Run as Administrator or choose different data directory
3. **Executable not found**: Ensure minio.windows-amd64.RELEASE.2025-09-07T16-13-09Z.exe is in the same directory

## Notes

- Data is stored in the `data/` directory by default
- MinIO server logs appear in the console window
- Press Ctrl+C to stop the server
- For production use, consider enabling HTTPS and changing default credentials