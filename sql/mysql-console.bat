@echo off
chcp 65001 >nul
setlocal

rem ============================================================
rem  Open a MySQL console for database movie_dashboard
rem  Double click this file, type SQL, press Enter.
rem  Type "exit" to quit.
rem ============================================================
set "MYSQL_BIN=D:\mysql\bin\mysql.exe"
set "DB_USER=root"
set "DB_PASS=root"

if not exist "%MYSQL_BIN%" (
    echo [INFO] mysql.exe not found at the path above, fallback to PATH
    set "MYSQL_BIN=mysql"
)

echo ============================================================
echo   MySQL console - database: movie_dashboard
echo   Type SQL and press Enter. Type "exit" to quit.
echo   Try:  SHOW TABLES;
echo         SELECT COUNT(*) FROM fact_rating;
echo ============================================================
echo.

"%MYSQL_BIN%" -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 movie_dashboard

if errorlevel 1 (
    echo.
    echo [ERROR] Cannot connect. Check MySQL service and the account in this file.
    pause
)