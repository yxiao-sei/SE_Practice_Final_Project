@echo off
REM 启动脚本 - 分离式部署 (Windows)
REM 使用方法: start.bat [profile]
REM 例如: start.bat dev

REM 设置默认profile
if "%1"=="" (
    set PROFILE=dev
) else (
    set PROFILE=%1
)

REM 获取脚本所在目录
set SCRIPT_DIR=%~dp0
REM 项目根目录
set PROJECT_DIR=%SCRIPT_DIR%..

REM 设置JAVA_OPTS
set JAVA_OPTS=-Xms512m -Xmx1024m -Dspring.profiles.active=%PROFILE%

REM 设置classpath
set CLASSPATH=%PROJECT_DIR%\lib\*;%PROJECT_DIR%\app\ecnu-authserver-*.war

REM 启动应用
echo Starting ECNU Auth Server with profile: %PROFILE%
echo Classpath: %CLASSPATH%
echo Java options: %JAVA_OPTS%

java %JAVA_OPTS% -cp "%CLASSPATH%" org.springframework.boot.loader.WarLauncher

pause
