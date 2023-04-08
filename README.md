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


**更新日志：**

2023-03-24 增加java代码样式、ctrl+alt+c快捷优化  
2023-03-24 增加java代码样式  
2023-03-38 优化插件UI  
2023-04-02 支持接口流处理  
2023-04-02 增加代码优化替换功能  

**后续功能：**  
自定义token配置、代理配置 开发中  
代码注释增加、代码补全（输入需求完成代码）


**声明：**
此项目以学习为目的进行创作，禁止培训机构、私人号主、公司组织等以各类收费形式进行销售。如果你有合作诉求，请与作者联系ljk1026@163.com

