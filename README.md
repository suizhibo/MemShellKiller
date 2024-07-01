# 简介

本工具采用Java Agent技术实现内存马的查杀，目前支持Servelet/Filter/Listener/Agent内存马，以及实现了ClassFileTransformer接口的类transform查杀。
当前仅实现了Tomcat中间件的Genie。
# 目录结构
```text
org.xxxx
|   Run.java  # agent.jar注入
|
+---agent  # 核心模块的加载和卸载
|       Agent.java
|       Config.java
|       JarFileHelper.java
|       Module.java
|       ModuleLoader.java
|
+---core
|   |   CoreBoot.java # 核心模块的启动器
|   |
|   +---genie # 注入小精灵
|   |   |   GenieBase.java
|   |   |
|   |   \---tomcat 
|   |           ApplicationFilterChainGenie.java
|   |
|   +---killer # 内存马删除模块
|   |       AgentKiller.java
|   |       KillerBase.java
|   |       NormalKiller.java
|   |       TransformKiller.java
|   |
|   \---trasnformer # 功能模块
|           ClassDumpTransformer.java
|           GenieTransformer.java
|           KillerTransformer.java
|           ProtectedTransformer.java
|           ScanTransformer.java
|           TransformerBase.java
|
+---javassist # 打包自己的javassist，防止和应用产生依赖冲突
|   |....
|
+---request # 反射获取httpservlet的request中的方法
|       AbstractRequest.java
|       HttpServletRequest.java
|
+---response 反射获取httpservlet的response中的方法
|       HttpServletResponse.java
|
\---utils # 工具模块
        Cache.java
        CheckStruct.java
        JavassistUtil.java
        Reflections.java
        Utils.java
```

# 使用方法
## 安装
```text
 java -Xbootclasspath/a:"C:\Program Files\Java\jdk1.8.0_251\tools.jar" -jar .\MemShellKiller.jar D:\工作\专项工具\MemShellKiller\out\artifacts\MemShellKiller_jar\MemShellKiller.jar install D:\\tomcat_server.properties
```
![image](https://github.com/suizhibo/MemShellKiller/assets/28916595/40c03ce0-06ac-4a37-9ee2-be637a510c93)

## 使用
访问：http://localhost:8080/TomactMemshellTest_war_exploded/?action=scan
![image](https://github.com/suizhibo/MemShellKiller/assets/28916595/496a39ee-3509-4493-9ef2-c2d3d80602df)

Dump MyFilter
![image](https://github.com/suizhibo/MemShellKiller/assets/28916595/cb596666-de6e-417c-a334-adc0dc22fea6)

Kill MyFilter 然后Dump，对比doFilter方法的内容 
![image](https://github.com/suizhibo/MemShellKiller/assets/28916595/a20c5268-b88d-4d7a-8470-5466b8da6c24)

## 卸载

```text
java -Xbootclasspath/a:"C:\Program Files\Java\jdk1.8.0_251\tools.jar" -jar .\MemShellKiller.jar D:\工作\专项工具\MemShellKiller\out\artifacts\MemShellKiller_jar\MemShellKiller.jar release D:\\tomcat_server.properties
```
![image](https://github.com/suizhibo/MemShellKiller/assets/28916595/9ce09cb4-31cf-4224-904e-74d2c571523c)
![image](https://github.com/suizhibo/MemShellKiller/assets/28916595/79fe258a-240d-461f-8621-0dbc771d5516)
![image](https://github.com/suizhibo/MemShellKiller/assets/28916595/e7857442-a88a-48cc-9c07-d649446682c4)


# 免责声明
1. 娱乐用途优先：本工具设计的初衷是为了教育、研究和娱乐目的。它可以帮助您了解网络安全的基础知识，提升您的技能，以及在合法范围内进行测试。

2. 请勿非法使用：请记住，未经授权对他人的系统、网络或设备进行渗透测试是违法的。我们强烈建议并恳求您不要使用本工具进行任何非法活动。否则，后果自负（并且可能会有法律追究哦）。

3. 合法授权：请确保您仅在获得明确授权的情况下使用本工具进行渗透测试。这包括但不限于：您自己的设备和网络，或明确授权您进行测试的第三方。

4. 知识与责任并重：网络安全是一项崇高的事业，保护网络安全是每一个网络安全爱好者的责任。使用本工具进行合法的测试和研究，帮助提升整体网络安全水平，这才是我们共同的目标。

5. 技术支持有限：本工具提供“按现状”提供，我们不对其适用性或可能造成的任何损失负责。使用本工具之前，请确保您已充分了解其功能和潜在影响。

6. 玩得开心，但要有节制：我们希望您在使用本工具时能获得乐趣并学到新知识，但请务必保持理智和克制。不要因为一时兴起而越界，记住，网络冒险有时也是需要勇气的撤退。

最后提醒
当您点击下载或使用本工具时，即表示您已阅读并同意以上所有条款。请戴好您的虚拟防护帽，系紧您的安全带，准备好迎接一场合法且富有教育意义的网络冒险吧！




