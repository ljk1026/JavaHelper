**JavaHelper基于openAi接口实现的Intelij idea2019插件；**

代码优化解析：根据选择的代码提供优化方案

代码优化:优化结果直接替换编辑器代码

简单问题回答：提供与chatGPT进行java开发相关问答

**工程说明：**
MyToolWindowFactory ：插件主界面
action包：插件的action类
handler包：提供对接口返回内容处理，可以理解为将openai的结果与idea交互处理的逻辑
1.4.0-release 版本使用*gpt-3.5-turbo*模型，详见OpenAiApiUtil类

**开发环境：**
IntelliJ IDEA 2019
gradle
jdk1.8