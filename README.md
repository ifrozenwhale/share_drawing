# 共享网络画板

项目地址：https://github.com/ifrozenwhale/share_drawing

## 特点

- 解耦合。分别实现数据模型、控制器和视图，拓展性强，可维护性高。
- 命令与事务封装。数据的一次更新为一条命令，用户的一次操作为一个事务。
- undo/redo 实现。由 controller 维护操作栈，由 数据模型维护数据镜像，恢复、重做以事务为可见单位。
- 增量同步更新。标记对象状态，只在必要的时候传输数据。
- 中心服务器保留图形数据，客户端之间相对独立。

![主窗口](https://frozenwhale.oss-cn-beijing.aliyuncs.com/img/image1.png)

## 实现的功能

- 任意曲线（Free Line）、圆点（Dot）、直线（Line）、矩形（Rectangle）、三角形（Triangle）
- 套索工具（Select）
  - 矩形区域选择框（支持一个或多个图形）
  - 变形（Reshape）
  - 移动（Move）
- 样式设置与修改（Style）
  - 线条颜色（Line Color）
  - 线条粗细（Stroke）
  - 颜色填充（Fill）
- 进阶操作
  - 抹除（Remove）：可同时抹除一个或者多个图形
  - 清空（Clear）
  - 撤销（Undo）
  - 重做（Redo）
- 图层：
  - 切换图层和图层显示
  - 图层创建和删除
  - 图层操作
- 画板设置
  - 画板背景色（Background Color）
  - 默认线条颜色（Default Color）
  - 默认线条粗细（Default Stroke）
- 保存与导出
  - 保存当前用户设置
  - 保存图形为数据文件
  - 利用套索工具，导出自定义区域为图片
  - 可加载已保存的图形文件
- 同步绘图
  - 共享画板视图
  - 共享图形编辑权限

## 启动

1. 客户端为 client 包下的 AppClient1 和 AppClient2
2. 服务端为 server 包下的 Server（默认配置了本地启动，端口号）
3. 其他类的 main 函数为测试函数

## 使用说明

1. free line 为任意线条，dot 为实心圆点，line 为直线，triangle 为等腰三角形，rectangle 为矩形。
2. select 为套索工具，绘制矩形。可选中整个图形，可选中图形的顶点，可选中图形的边，可选中1个或者多个图形。
3. move 为移动操作。select 选中图形后，以鼠标按下位置为起始点，拖动鼠标进行移动。
4. reshape 为变形操作。需通过 select 选中图形的某个顶点，然后点击 reshape 进入变形模式，拖动顶点完成变形。
5. style 为样式设置，可以设置颜色和线条粗细。如若未选中图形，则只设置画笔样式；选中图形，则对选中图形进行样式的修改。
6. fill 为填充操作。点击 fill 后，选择颜色，再点击图形内部（点中多个，选中最后绘制的图形），完成填充。
7. clear 为清空画板。
8. remove 为移除图形，需要先 select 选择1个或多个图形，再点击 remove 进行移除。
9. undo 和 redo 支持上述所有操作，进行回退和重做。
10. File 菜单栏支持导出图片，支持保存为 .shape 数据文件，启动程序时可以打开 .shape 文件。
11. Settings 菜单栏 style 支持默认样式的修改，即启动应用后的画笔颜色和粗细；
12. Settings 中 background 项为设置画板的背景颜色，此设置将会被保存，下次启动时默认颜色即设置的颜色。
13. 登录时选择加入服务器，则会同步服务器上的图形数据，进行同步、共享绘图。
14. 登录时不加入服务器，选择新建画布，则生成空画板；否则选择 .shape 文件初始化画板图形。
15. 不加入服务器时，可以利用图层分层绘制图形并做一系列的修改。

## 类的关系

Shape 作为数据的抽象基类，实现图形数据的公共方法，Rectangle 等子类完成各自的数据处理操作。

Graphics 类继承自 java.swingx.JPanel，作为画板，拥有 Shape 数据成员，调用 Shape 及其子类的方法，完成逻辑操作，同时显示图形。

App类继承自 java.swingx.JFrame，作为主界面类，拥有一系列组件，包括 Graphics 显示图形。

ErrorView 类继承自 JDialog，以对话框的形式提示。

StyleConfig 类继承自 JDialog，以对话框的形式供选择。

## 部分实现细节

- **消除曲线锯齿：** 采样鼠标运动点，生成路径，二次曲线连接。位防止点过于密集导致的抖动，设置最小步长间隔。
- **套索工具（Lasso）**：由各图形子类负责选中判断。主要分析包含和边界相交情况。
- **Undo/redo：** 由Graphics负责记录操作，使用一个事务列表和事务索引。shape子类中新增加一个容器，用于记录历史对象；增加一个变量来记录索引。
- **导出**：复用矩形选择框，选中导出区域，利用 Robot 截屏和图片 IO 流保存。 
- **保存和打开**：实现序列化和反序列化。对打开的文件可以实现除undo、redo外任意的修改操作。
- **同步机制**：使用 serverSocket 实现。采用增量更新，标记对象类别为`新增、删除、修改`，对需要更新的对象进行传输。为了判断对象是否是同一个对象，因为修改操作导致的属性不同，为Shape类增加了属性 uuid，作为唯一标识符。更新图形池时，具体的，判断图形的 active 值，如若为 false 表示已删除，将其从 shapeList 绘图池中移除；否则判断是否有属性不同的对象，若不同，则进一步判断是否具有相同的唯一标识符uuid，是这说明二者为同一个对象，应先删除原对象，再重新添加新对象表示更新；否则直接进行添加。
- **整体逻辑**
  - 顶层：对于App类，负责主界面的按钮等事件监听，在监听到事件发生时，设置全局的行为状态，包括鼠标指针更新，Graphics图形标识等。其只负责向下传达命令，不涉及复杂逻辑操作。
  - 中间控制器：对于Graphics，监听鼠标事件，完成绘图。一次绘图或者调整，涉及到鼠标的按下（Press），拖动（Drag），以及松开（Release）三个过程，对应于逻辑实现的三个部分，开始处理（startAction），处理过程（acting），处理完成（endAction）。使用有限状态机，设置多个枚举类，ActionType、ActionStatus、ShapeType，根据当前的状态和输入，判断下一步的状态。
  - 数据：负责各自绘制以及调整操作。
