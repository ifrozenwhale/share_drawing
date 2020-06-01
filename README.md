# 编码说明

1. 编码使用UTF-8。

# 启动说明

1. 客户端为client包下的AppClient1和AppClient2
2. 服务端为server包下的Server（默认配置了本地启动，端口号）
3. 其他类的main函数为测试函数

# 实现的功能

## 图形

- 任意曲线（Free Line）
- 圆点（Dot）
- 直线（Line）
- 矩形（Rectangle）

- 三角形（Triangle） 

## 调整

- 套索选择（Select）
- 矩形区域选择框
  - 可同时选中一个或者多个图形
- 变形（Reshape）
  - 选中图形的控制点，改变图形形状
- 移动（Move）
  - 以鼠标按下为移动中心，进行拖动
  - 可同时移动或多个图形

- 样式修改（Style）
  - 线条颜色（Line Color）
  - 线条粗细（Stroke）

- 颜色填充（Fill）

## 图层（非网络模式）

- 图层选择区域，可以切换图层和图层显示
- 图层创建和删除
- 图层操作

## 重绘、清除

- 抹除（Remove）
  - 可同时抹除一个或者多个图形

- 清空（Clear）

- 撤销（Undo）
  - 支持上述所有操作

- 重做（Redo）
  - 支持上述所有操作

## 设置

- 画板背景色（Background Color）

- 默认线条颜色（Default Color）

- 默认线条粗细（Default Stroke）

## 保存、导出、打开

- 保存设置（Global Configuration）
  - 下次启动程序恢复已保存的全局设置

- 保存图形（Save Shape）
  - 将图形数据保存为数据文件
- 导出
  - 利用套索工具，选择画板的区域，导出为JPG图片
- 打开
  - 可新建画板，或者从保存的数据文件中加载画板信息

## 同步绘图

- 共享画板视图

- 共享图形编辑权限

# 使用说明

1. free line为任意线条，dot为实心圆点，line为直线，triangle为等腰三角形，rectangle为矩形。
2. select为套索工具，绘制矩形。可选中整个图形，可选中图形的顶点，可选中图形的边，可选中1个或者多个图形。
3. move为移动操作。select选中图形后，以鼠标按下位置为起始点，拖动鼠标进行移动。
4. reshape为变形操作。需通过select选中图形的某个顶点，然后点击reshape进入变形模式，拖动顶点完成变形。
5. style为样式设置，可以设置颜色和线条粗细。如若未选中图形，则只设置画笔样式；选中图形，则对选中图形进行样式的修改。
6. fill为填充操作。点击fill后，选择颜色，再点击图形内部（点中多个，选中最后绘制的图形），完成填充。
7. clear为清空画板。
8. remove为移除图形，需要先select选择1个或多个图形，再点击remove进行移除。
9. undo和redo支持上述所有操作，进行回退和重做。
10. File菜单栏支持导出图片，支持保存为.shape数据文件，启动程序时可以打开.shape文件。
11. Settings菜单栏style支持默认样式的修改，即启动应用后的画笔颜色和粗细；
12. Settings中background项为设置画板的背景颜色，此设置将会被保存，下次启动时默认颜色即设置的颜色。
13. 登录时选择加入服务器，则会同步服务器上的图形数据，进行同步、共享绘图。
14. 登录时不加入服务器，选择新建画布，则生成空画板；否则选择.shape文件初始化画板图形。
15. 不加入服务器时，可以利用图层分层绘制图形并做一系列的修改。



## 包（Package）和类（Class）说明

### **app** 界面UI

- **class** App 主要界面类，和用户进行操作交互，完成接收用户指令和下层功能调用，并进行显示。

- **class** ErrorView 错误提示类，异常显示，错误显示
- **class** LayerList 图层列表类，显示图层选择区
- **class** LayerListCellRenderer 列表样式渲染类 

### **command** 封装操作

- **enum** ActionType 操作类型枚举
- **class** Affair 事务类，将一次整体操作封装为一次事务
- **class** Command 命令类，一次事务包括多条命令 

### **config** 设置

- **enum** ConfigType 设置类型枚举，分为全局设置和临时设置

- **class** StyleConfig 样式设计类

### **util** 工具

- **class** Util 工具类，负责文件操作、图片操作等

### **shape** 数据对象

- **enum** ActionStatus 操作状态枚举，表示所处的操作环节

- **enum** ShapeType 图形类型枚举，标识不同的图形

- **class** Point 点类，封装x坐标与y坐标，实现了点（向量）运算

- **class** Shape 抽象基类，图形类，抽象方法声明，实现了公有属性和方法

- **class** Dot 实心圆点类

- **class** FreeLine 任意线条，平滑曲线

- **class** Line 直线类

- **class** Rectangle矩形类

- **class** Triangle 三角形类，初始为等腰三角形

- **class** SimpleTriangle 简单三角形类，用于三角形数据处理

- **class** Layer 图层封装类，封装了编号和名称

- **class** Graphics 画板操作类，完成不同子类实例间的穿插处理以及和上层的接口功能。

### **client** 客户端

- **class** AppClient 客户端类启动类

- **class** ReadProcess 数据读入线程类，负责接受服务器发送的数据来进行更新。

### **server** 服务端

- **class** Server 服务器启动类，等待客户端加入

- **class** ExecuteClientServer 监听客户端处理类，接受数据并分发

## 类的关系

Shape作为数据的抽象基类，实现图形数据的公共方法，Rectangle等子类完成各自的数据处理操作。

Graphics类继承自java.swingx.JPanel，作为画板，拥有Shape数据成员，调用Shape及其子类的方法，完成逻辑操作，同时显示图形。

App类继承自java.swingx.JFrame，作为主界面类，拥有一系列组件，包括Graphics显示图形。

ErrorView类继承自JDialog，以对话框的形式提示。

StyleConfi类继承自JDialog，以对话框的形式供选择。 

# UI

- 主窗口显示。
  
  <img src="https://frozenwhale.oss-cn-beijing.aliyuncs.com/img/20200601203351.png" width="70%"/>
  
- 导出图片
  
  <img src="https://frozenwhale.oss-cn-beijing.aliyuncs.com/img/20200601203451.png" width="70%"/>
  
- 文件保存

  <img src="https://frozenwhale.oss-cn-beijing.aliyuncs.com/img/20200601203541.png" width="70%"/>
  
- 加入图层

  <img src="https://frozenwhale.oss-cn-beijing.aliyuncs.com/img/20200601203631.png" width=70%/>