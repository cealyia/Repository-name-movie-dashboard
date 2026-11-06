@echo off
chcp 65001 >nul
setlocal

rem ============================================================
rem  Run kpi_check.sql and print the result tables
rem  Use it to compare the dashboard KPI with the database
rem ============================================================
set "MYSQL_BIN=D:\mysql\bin\mysql.exe"
set "DB_USER=root"
set "DB_PASS=root"

if not exist "%MYSQL_BIN%" (
    echo [INFO] mysql.exe not found at the path above, fallback to PATH
    set "MYSQL_BIN=mysql"
)

echo ============================================================
echo   KPI check - movie_dashboard
echo ============================================================
echo.

"%MYSQL_BIN%" -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 -t < "%~dp0kpi_check.sql"

echo.
echo [DONE] Compare with the dashboard:
echo   movie=9729  ratings=27918  avg=6.96  users=155  max=100  high=46.5
pause