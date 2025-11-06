# ECNU Auth Server 部署说明

## 新的分离式打包方式

项目已修改为分离式打包，将应用程序和依赖库分开，避免每次都打包出很大的JAR文件。

## 打包命令

```bash
mvn clean package
```

## 打包结果

打包完成后，在 `target` 目录下会生成一个ZIP文件：`ecnu-authserver-0.0.1-SNAPSHOT-package.zip`

解压后的目录结构：
```
ecnu-authserver-0.0.1-SNAPSHOT-package/
├── app/                          # 应用程序目录
│   └── ecnu-authserver-0.0.1-SNAPSHOT.war
├── lib/                          # 依赖库目录
│   ├── spring-boot-*.jar
│   ├── spring-security-*.jar
│   ├── mysql-connector-*.jar
│   ├── neusoft-cas-client-4.0.0.jar
│   └── ... (其他依赖JAR)
├── config/                       # 配置文件目录
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prd.yml
│   ├── application-uat.yml
│   └── ... (其他配置文件)
└── scripts/                      # 启动脚本目录
    ├── start.sh                  # Linux/Mac启动脚本
    └── start.bat                 # Windows启动脚本
```

## 部署方式

### 1. 解压部署包
```bash
unzip ecnu-authserver-0.0.1-SNAPSHOT-package.zip
cd ecnu-authserver-0.0.1-SNAPSHOT-package
```

### 2. 启动应用

#### Linux/Mac:
```bash
# 使用dev环境启动
./scripts/start.sh dev

# 使用prd环境启动
./scripts/start.sh prd

# 使用uat环境启动
./scripts/start.sh uat
```

#### Windows:
```cmd
REM 使用dev环境启动
scripts\start.bat dev

REM 使用prd环境启动
scripts\start.bat prd

REM 使用uat环境启动
scripts\start.bat uat
```

### 3. 手动启动（可选）
如果不想使用启动脚本，也可以手动启动：

```bash
java -Xms512m -Xmx1024m -Dspring.profiles.active=dev \
     -cp "lib/*:app/ecnu-authserver-*.war" \
     org.springframework.boot.loader.WarLauncher
```

## 优势

1. **减小打包文件大小**: 应用程序JAR文件不包含依赖，文件更小
2. **快速部署**: 只需要替换应用程序JAR，依赖库可以复用
3. **灵活配置**: 可以独立修改配置文件而不重新打包
4. **易于维护**: 依赖库和应用程序分离，便于版本管理

## 注意事项

1. 确保Java环境已正确安装
2. 确保所有依赖库都在 `lib` 目录中
3. 配置文件路径相对于应用程序JAR文件
4. 如需修改依赖库，需要重新打包整个项目
