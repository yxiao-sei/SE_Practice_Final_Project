# 系统Auth Server服务
AuthServer基于Spring Boot OAuth2 + Spring Security + JWT，将支持以下3种认证方式：
1. 企业微信扫码认证
2. OAuth2统一跳转认证
3. 系统内用户名密码认证

用户认证成功之后将获得`JWT`凭证，可用于访问本系统的其他服务组件。