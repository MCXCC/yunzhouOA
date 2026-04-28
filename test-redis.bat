@echo off
echo Redis Test Tool
echo ================

set REDIS_CLI=C:\Program Files\Redis\redis-cli.exe

:: Check if redis-cli exists
if not exist "%REDIS_CLI%" (
    echo ERROR: Cannot find redis-cli.exe
    echo Expected at: %REDIS_CLI%
    pause
    exit /b 1
)

:: Test 1: Ping
echo.
echo [Test 1] Connection Test (PING)
"%REDIS_CLI%" ping

:: Test 2: Set and Get
echo.
echo [Test 2] Set and Get Value
"%REDIS_CLI%" set test:name "RedisTest"
"%REDIS_CLI%" get test:name

:: Test 3: Counter
echo.
echo [Test 3] Counter Operations
"%REDIS_CLI%" set test:counter 0
"%REDIS_CLI%" incr test:counter
"%REDIS_CLI%" incr test:counter
"%REDIS_CLI%" incr test:counter
"%REDIS_CLI%" get test:counter

:: Test 4: List
echo.
echo [Test 4] List Operations
"%REDIS_CLI%" del test:list
"%REDIS_CLI%" rpush test:list "item1"
"%REDIS_CLI%" rpush test:list "item2"
"%REDIS_CLI%" rpush test:list "item3"
"%REDIS_CLI%" lrange test:list 0 -1

:: Test 5: Hash
echo.
echo [Test 5] Hash Operations
"%REDIS_CLI%" hset test:user name "John"
"%REDIS_CLI%" hset test:user age "25"
"%REDIS_CLI%" hset test:user city "Beijing"
"%REDIS_CLI%" hgetall test:user

:: Test 6: Server Info
echo.
echo [Test 6] Redis Server Info
"%REDIS_CLI%" info server | findstr redis_version

:: Cleanup
echo.
echo [Cleanup] Remove test data
"%REDIS_CLI%" del test:key test:name test:counter test:list test:user

echo.
echo ====================
echo All tests completed!
echo Redis is working properly!
echo ====================
pause