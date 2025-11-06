#!/bin/bash

# 启动脚本 - 分离式部署
# 使用方法: ./start.sh [profile]
# 例如: ./start.sh dev

# 设置默认profile
PROFILE=${1:-dev}

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# 项目根目录
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# 设置JAVA_OPTS
JAVA_OPTS="-Xms512m -Xmx1024m -Dspring.profiles.active=$PROFILE"

# 设置classpath
CLASSPATH="$PROJECT_DIR/lib/*:$PROJECT_DIR/app/ecnu-authserver-*.war"

# 启动应用
echo "Starting ECNU Auth Server with profile: $PROFILE"
echo "Classpath: $CLASSPATH"
echo "Java options: $JAVA_OPTS"

java $JAVA_OPTS -cp "$CLASSPATH" org.springframework.boot.loader.WarLauncher
