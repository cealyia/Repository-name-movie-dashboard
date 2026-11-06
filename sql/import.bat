@echo off
setlocal enabledelayedexpansion

rem ============================================================
rem  Movie Dashboard - import data into MySQL
rem  Usage: double click, or run "import.bat" in this folder
rem  Edit the three variables below if your MySQL account differs
rem ============================================================
set "MYSQL_BIN=D:\mysql\bin\mysql.exe"
set "DB_USER=root"
set "DB_PASS=root"

set "SCRIPT_DIR=%~dp0"

if not exist "%MYSQL_BIN%" (
    echo [INFO] %MYSQL_BIN% not found, fallback to mysql in PATH
    set "MYSQL_BIN=mysql"
)

echo ============================================================
echo   Movie Dashboard - Database Import
echo   database: movie_dashboard
echo   user    : %DB_USER%
echo ============================================================
echo.

for %%F in (01_schema.sql 02_dim_movie.sql 03_fact_rating.sql 04_fact_tag.sql) do (
    echo ^>^>^> running %%F ...
    "%MYSQL_BIN%" -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 < "%SCRIPT_DIR%%%F"
    if errorlevel 1 (
        echo.
        echo [FAILED] %%F returned an error. Check the message above.
        echo          Common causes: MySQL service stopped, wrong password, wrong port.
        pause
        exit /b 1
    )
)

echo.
echo ^>^>^> verify row counts ...
"%MYSQL_BIN%" -u%DB_USER% -p%DB_PASS% --default-character-set=utf8mb4 -e "USE movie_dashboard; SELECT (SELECT COUNT(*) FROM dim_movie) AS movies, (SELECT COUNT(*) FROM fact_rating) AS ratings, (SELECT COUNT(*) FROM fact_tag) AS tags;"

echo.
echo [DONE] Import finished. Expect: movies=9742  ratings=100836  tags=3683
pause
