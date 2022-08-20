基于Mirai的一个QQ机器人，没什么功能。


其中 application.yml 文件内容如下：
 
 ```yml
spring:
  # 数据库配置
  datasource:
    username: username
    password: password
    url: xxxxx
    driver-class-name: com.mysql.cj.jdbc.Driver

  redis:
    host: localhost
    port: 6379
    password:
    database: 0

mybatis:
  mapper-locations: classpath:mapper/*.xml

# 机器人账号密码
Account: 123456789
password: 123456789

# 对象存储地址，用的 minio
# accessKey和secretKey在minion控制台可以拿到
app:
  minio:
    # api请求地址
    endpoint: url
    # accessKey
    accessKey: accessKey
    # secretKey
    secretKey: secretKey
    # 储存桶
    bucket: images

# 这是折腾的 Learn cloud 的api，废弃了
blog:
  ip: 
  link: 
  mail: 
  mailMd5: 
  nick: 
  ua: 
  url: 
  id: 
  key: 

# 项目中的账号出现的配置，只是不想暴露别人的账号
# 可以直接在项目中写QQ号
account:
  master: 
  friend1: 
  friend2: 
```
