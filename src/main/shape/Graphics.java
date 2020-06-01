package main.shape;

import main.app.ErrorView;
import main.command.ActionType;
import main.command.Affair;
import main.command.Command;
import main.config.StyleConfig;
import main.util.Save;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Graphics extends JPanel {
    private int layerId = 0;
    private List<Integer> visibleLayerList;
    private List<Shape> shapeList; // 添加的图形列表
    private List<Affair> affairs; // 历史事务列表
    private int affairIndex; // 事务索引
    private ShapeType shapeType; // 图形类型
    private List<Shape> selectedShapes; // 选中的图形
    private Shape currentShape; // 当前图形
    private Rectangle lasso; // 套索图形
    private ActionStatus actionStatus = ActionStatus.DRAWING; // 操作状态
    private boolean dragged = false; // 是否可拖动
    private float stroke = (float)2; // 默认画笔
    private Color color = Color.BLACK; // 默认颜色
    private Color backgroundColor; // 背景色
    private java.awt.Point absoluteA; // 相对于屏幕的绝对坐标
    private java.awt.Point absoluteB; // 相对于屏幕的绝对坐标
    private Socket server; // 服务器
    private ObjectOutputStream os = null; // 对象输出流
    private List<Shape> updatedShapes; // 有更新, 待发送的图形

    public List<Integer> getVisibleLayerList() {
        return visibleLayerList;
    }

    public void setVisibleLayerList(List<Integer> visibleLayerList) {
        this.visibleLayerList = visibleLayerList;
    }

    public int getLayerId() {
        return layerId;
    }

    public void setLayerId(int layerId) {
        this.layerId = layerId;
    }

    /**
     * 更新图形列表
     * 如果图形被标记为已删除, 则将其从列表移除
     * 如果本地列表中不含有新传入的图形(属性完全一致), 进一步判断是否是同一个对象
     * 如果是同一个对象, 则将其移除, 然后添加新的对象; 否则直接添加
     * @param shapes 新传入的图形列表
     */
    public void updateShapeList(List<Shape> shapes){
        for(Shape shape: shapes){
            // 如果已经被标记为删除
            if(!shape.isActive()){
                shapeList.remove(shape);
                continue;
            }
            // 如果没有一致的图形
            if(!shapeList.contains(shape)) {
                int len = shapeList.size();
                boolean flag = false;
                for (int i = len - 1; i >= 0; --i) {
                    // 如果是同一个对象
                    if (shape.getUuid().equals(shapeList.get(i).getUuid())) {
                        System.out.println("remove: " + shape.toString());
                        shapeList.remove(i);
                        shapeList.add(i, shape);
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    shapeList.add(shape);
                }
            }
        }
        repaint();
    }

    public List<Shape> getUpdatedShapes() {
        return updatedShapes;
    }
    public void setUpdatedShapes(List<Shape> updatedShapes) {
        this.updatedShapes = updatedShapes;
    }
    public int getAffairIndex() {
        return affairIndex;
    }
    public Rectangle getLasso() {
        return lasso;
    }
    public void setLasso(Rectangle lasso) {
        this.lasso = lasso;
    }
    public List<Shape> getSelectedShapes() {
        return selectedShapes;
    }
    public void setSelectedShapes(List<Shape> selectedShapes) {
        this.selectedShapes = selectedShapes;
    }
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.setBackground(backgroundColor);
    }

    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public Shape getCurrentShape() {
        return currentShape;
    }
    public void setCurrentShape(Shape currentShape) {
        this.currentShape = currentShape;
    }
    public ShapeType getShapeType() {
        return shapeType;
    }
    public boolean isDragged() {
        return dragged;
    }
    public void setDragged(boolean dragged) {
        this.dragged = dragged;
    }
    public ActionStatus getActionStatus() {
        return actionStatus;
    }
    public void setActionStatus(ActionStatus actionStatus) {
        this.actionStatus = actionStatus;
    }

    /**
     * 创建一个简单事务
     * 简单事物只有一个基本操作
     * 抽取的通用方法
     *
     * @param actionType 基本操作类型
     * @param shape      操作对象
     * @return 事务
     */
    private Affair getSimpleAffair(ActionType actionType, Shape shape) {
        List<Command> commands = new ArrayList<Command>();
        commands.add(new Command(actionType, shape));
        return new Affair(affairIndex, commands);
    }

    /**
     * undo事务
     * 更新索引
     */
    private void undoAffair() {
        affairIndex--;
    }

    /**
     * redo事务
     * 更新索引
     */
    private void redoAffair() {
        affairIndex++;
    }

    /**
     * 增加新的事务
     *
     * @param affair 事务
     */
    public void addAffair(Affair affair) {
        affairIndex++;
        affairs.add(affairIndex, affair);
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public float getStroke() {
        return stroke;
    }

    public void setStroke(float stroke) {
        this.stroke = stroke;
    }

    /**
     * 得到最后一个实例
     *
     * @return Shape
     */
    public main.shape.Shape getLastShape() {
        return shapeList.get(shapeList.size() - 1);
    }

    /**
     * 绘制所有实例
     */
    public void draw(Graphics2D graphics2D) {
        for (main.shape.Shape shape : shapeList) {
            shape.draw(graphics2D);
        }
    }


    public java.awt.Point getAbsoluteA() {
        return absoluteA;
    }

    public void setAbsoluteA(java.awt.Point absoluteA) {
        this.absoluteA = absoluteA;
    }

    public java.awt.Point getAbsoluteB() {
        return absoluteB;
    }

    public void setAbsoluteB(java.awt.Point absoluteB) {
        this.absoluteB = absoluteB;
    }

    /**
     * 添加图形实例
     * 得到一个简单事务 Affair
     * 增加事务
     * 添加图形实例
     * 记录操作
     * @param shape 图形实例
     */
    public void add(Shape shape) {
        Affair affair = getSimpleAffair(ActionType.ADD, shape); // 得到一个简单事务
        addAffair(affair); // 增加事务
        shapeList.add(shape); // 添加图形实例
        shape.log();
    }

    /**
     * 调整图形实例属性
     * @param shape 要调整的实例
     * @param newShape 目标
     */
    public void reshape(Shape shape, Shape newShape) {
        shape.reshape(newShape); // 调整
        shape.storeOrigin(); // 设置初始位置
        shape.log(); // 记录操作
    }

    /**
     * undo 操作
     */
    public void undo() {
        // 如果没有可撤回的操作
        if (affairIndex == -1) {
            ErrorView error = new ErrorView("Nothing to undo");
            error.setVisible(true);
            return;
        } // 无可撤回的记录
        List<Command> commands = affairs.get(affairIndex).getCommands(); // 得到要撤回的事务
        undoAffair(); // 更新索引
        for (Command command : commands) { // 得到此次事务中的操作命令
            Shape shape = command.getShape(); // 得到操作的图形对象
            switch (command.getActionType()) { // 根据操作类型，逆向操作
                case RESHAPE:
                case MOVE:
                case MOTIFY:
                    shape.unReshape();
                    updatedShapes.add(shape);
                    break;
                case REMOVE:
                    shapeList.add(shape);
                    shape.setActive(true);
                    updatedShapes.add(shape);
                    break;
                case ADD:
                    shape.setActive(false);
                    shapeList.remove(shape);
                    updatedShapes.add(shape);
                    break;
                default: // 否则取消这次操作
                    redoAffair();
                    break;
            }
        }
        outToServer();
        repaint();
    }

    /**
     * redo操作
     */
    public void redo() {
        if (affairIndex == affairs.size() - 1) {
            ErrorView error = new ErrorView("Nothing to redo");
            error.setVisible(true);
            return;
        } // 无可重做的操作
        redoAffair(); // 更新索引到前一次
        System.out.println("redo: index " + affairIndex);
        List<Command> commands = affairs.get(affairIndex).getCommands(); // 得到前一次事务
        for (Command command : commands) {
            Shape shape = command.getShape();
            System.out.println(shape);
            System.out.println(command.getActionType());
            switch (command.getActionType()) { // 根据操作类型，进行重做
                case ADD:
                    shapeList.add(shape);
                    updatedShapes.add(shape);
                    shape.setActive(true);
                    break;
                case REMOVE:
                    shape.setActive(false);
                    updatedShapes.add(shape);
                    shapeList.remove(shape);
                    break;
                case RESHAPE:
                case MOVE:
                case MOTIFY:
                    shape.reReshape();
                    updatedShapes.add(shape);
                    break;
                default: // 否则取消这次操作
                    undoAffair();
                    break;
            }
        }
        outToServer();
        repaint();
    }

    /**
     * 清空所有图形
     */
    public void clear() {
        List<Command> commands = new ArrayList<Command>();
        for (int i = shapeList.size() - 1; i >= 0; i--) {
            commands.add(new Command(ActionType.REMOVE, shapeList.get(i))); // 添加一条命令
            shapeList.remove(i);
        }
        Affair affair = new Affair(affairIndex, commands); // 新建复杂事务
        addAffair(affair);
        repaint();
    }

    /**
     * 设置当前被套索选中的对象
     */
    public void setSelectedShape() {
        selectedShapes.clear(); // 清空之前的选择
        if(actionStatus == ActionStatus.SELECTING){ // 选择状态
            for (Shape shape : shapeList) {
                // 如果不属于当前可见图层
                if (!visibleLayerList.contains(shape.getLayerId())) {
                    continue;
                }
                if (shape.isSelected(lasso)) { // 首先是图形被选择
                    System.out.println(shape);
                    Point point = shape.getPointInLasso(lasso); // 得到被选择的点

                    if(point != null){ // 被选择了
                        setCurrentShape(shape);
                        shape.setCurrentPoint(point);
                    }
                    selectedShapes.add(shape); // 添加被选择的图形
                    setDragged(true); // 可以拖动
                }
            }
        }

        if(!selectedShapes.isEmpty()) currentShape = selectedShapes.get(0); // 设置当前图形为第一个被选择的, 解决图形重叠的矛盾
    }

    /**
     * 设置样式
     */
    public void setStyle(){
        StyleConfig styleConfig = new StyleConfig(this);
        styleConfig.setVisible(true);
    }

    /**
     * 改变样式
     */
    public void changeStyle(){
        setActionStatus(ActionStatus.MODIFYING); // 更新状态
        StyleConfig styleConfig = new StyleConfig(this);
        styleConfig.setVisible(true);
        Affair affair = getSimpleAffair(ActionType.MOTIFY, currentShape); // 简单事务
        addAffair(affair); // 添加
    }

    /**
     * 构造初始化
     */
    public Graphics(Socket server) {
        initComponents();
        this.server = server;
        shapeList = new LinkedList<>();
        visibleLayerList = new ArrayList<>();
        selectedShapes = new LinkedList<>();
        updatedShapes = new ArrayList<>();
        affairs = new ArrayList<>();
        affairIndex = -1;
        shapeType = ShapeType.FREE_LINE; // 默认为任意线
        setBackground(backgroundColor);
        if(server != null){
            try {
                os = new ObjectOutputStream(server.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void initSavedShapes(String pathname) {
        Object o = Save.readObjectFromFile(pathname);
        if(o == null){
            return;
        }
        shapeList = (List<Shape>) o;

    }

    /**
     * 重写画图函数, 对当前所有的图形进行绘制
     * @param g java.awt.Graphics
     */
    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        for (Shape shape : shapeList) {
            if (!visibleLayerList.contains(shape.getLayerId())) continue;
            shape.draw(graphics2D);
        }
        if (lasso != null) lasso.draw(graphics2D);
    }

    /**
     * 移除
     */
    public void remove() {
        // 如果为空
        if(selectedShapes.isEmpty()){
            ErrorView error = new ErrorView("<html>Please select the shape<br/>to be removed</html>");
            error.setSize(500, 200);
            error.setVisible(true);
            return;
        }
        List<Command> commands = new ArrayList<>(); // 命令列表
        for(int i = selectedShapes.size()-1; i >= 0; i--){
            Shape shape = selectedShapes.get(i);
            shape.setActive(false);
            updatedShapes.add(shape);
            commands.add(new Command(ActionType.REMOVE, shape));
            shapeList.remove(shape);
            selectedShapes.remove(shape);
        }
        Affair affair = new Affair(affairIndex, commands); // 新建事务
        addAffair(affair); // 添加
        outToServer();
        repaint();

    }

    /**
     * 开始画图的处理
     * @param e 起始点
     */
    public void startDraw(Point e){
        setDragged(true);
        switch (shapeType) {
            case DOT:
                currentShape = new Dot(new Point(e.getX(), e.getY()), layerId);
                break;
            case OVAL:
                currentShape = new Oval(new Point(e.getX(), e.getY()), layerId);
                break;
            case RECTANGLE:
                currentShape = new Rectangle(new Point(e.getX(), e.getY()), new Point(e.getX(), e.getY()), layerId);
                break;
            case LINE:
                currentShape = new Line(new Point(e.getX(), e.getY()), new Point(e.getX(), e.getY()), layerId);
                break;
            case TRIANGLE:
                currentShape = new Triangle(new Point(e.getX(), e.getY()), new Point(e.getX(), e.getY()), new Point(e.getX(), e.getY()), layerId);
                break;
            case FREE_LINE:
                currentShape = new FreeLine(new Point(e.getX(), e.getY()), layerId);
            default:
                break;
        }
        currentShape.setColor(color);
        currentShape.setStroke(stroke);
        selectedShapes.clear();
        add(currentShape);
    }

    /**
     * 开始选择的处理
     * @param e 起始点
     */
    public void startSelect(Point e){
        lasso = new Rectangle(new Point(e.getX(), e.getY()), new Point(e.getX(), e.getY()), layerId);
        lasso.setColor(Color.gray);
        lasso.setStroke((float) 0.5);
        setAbsoluteA(new java.awt.Point(MouseInfo.getPointerInfo().getLocation()));
    }
    /**
     * 开始调整的处理
     */
    public void startReshape(){
        setDragged(true);
        if(shapeType == ShapeType.FREE_LINE) setDragged(false);
    }
    /**
     * 开始移动的处理
     * @param e 起始点
     */
    public void startMove(Point e){
        for(Shape shape : selectedShapes){
            shape.setStartLocation(new Point(e.getX(), e.getY()));
        }
        setDragged(true);
    }

    /**
     * 事件响应 鼠标压下
     * @param e MouseEvent
     */
    private void graphicsMousePressed(MouseEvent e) {
        Point p = new Point(e.getX(), e.getY());
        switch (actionStatus) {
            case DRAWING: startDraw(p);break;
            case SELECTING: startSelect(p); break;
            case RESHAPING: startReshape(); break;
            case MOVING: startMove(p);break;
        }
        repaint();
    }

    /**
     * 结束选择
     */
    public void endSelect(){
        setSelectedShape(); // 设置被选择的图形
        setActionStatus(ActionStatus.MOVING); // 默认进入移动状态
        setDragged(false);
        setAbsoluteB(new java.awt.Point(MouseInfo.getPointerInfo().getLocation())); // 设置绝对坐标
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 设置鼠标光标样式
    }
    /**
     * 结束调整的处理
     */
    public void endReshape(){
        setDragged(false);
        reshape(currentShape, currentShape); // 调整
        Affair affair = getSimpleAffair(ActionType.RESHAPE, currentShape); // 得到一个简单事务
        addAffair(affair); // 增加事务


        setActionStatus(ActionStatus.NONE);
        System.out.println("endReshape: " + currentShape.toString() + currentShape.getColor());
        updatedShapes.add(currentShape);
        outToServer();
        // addAffair(getSimpleAffair(ActionType.RESHAPE, currentShape)); // 添加事务
    }
    /**
     * 结束移动的处理
     */
    public void endMove(){
        List<Command> commands = new ArrayList<>();
        for(Shape shape:selectedShapes){
            commands.add(new Command(ActionType.MOVE, shape));
            reshape(shape, shape);
        }
        Affair affair = new Affair(affairIndex, commands);
        addAffair(affair); // 增加事务
        setDragged(false);
        setActionStatus(ActionStatus.SELECTING);
        lasso = null;
        updatedShapes.addAll(selectedShapes);
        outToServer();
    }
    /**
     * 结束画图的处理
     */
    public void endDraw(){
        currentShape.log();
        currentShape.storeOrigin();
        lasso = null;
        updatedShapes.add(currentShape);
        outToServer();
    }

    /**
     * 事件响应 松开鼠标
     * @param e MouseEvent
     */
    private void graphicsMouseReleased(MouseEvent e) {
        switch (actionStatus) {
            case SELECTING: endSelect(); break;
            case RESHAPING: endReshape(); break;
            case MOVING: endMove(); break;
            case DRAWING: endDraw(); break;
//             case REMOVING: endRemove(); break;
        }
        repaint();
    }

    /**
     * 正在选择的处理
     * @param e 点
     */
    public void selecting(Point e){
        lasso.updatePosition(new Point(e.getX(), e.getY()));
    }
    /**
     * 正在调整的处理
     * @param e 点
     */
    public void reshaping(Point e){
        currentShape.updatePoint(new Point(e.getX(), e.getY()));
        // currentShape.updatePosition();
    }
    /**
     * 正在绘制的处理
     * @param e 点
     */
    public void drawing(Point e){
        if (dragged) {
            currentShape.updatePosition(new Point(e.getX(), e.getY()));
        }
    }
    /**
     * 正在移动的处理
     * @param e 点
     */
    public void moving(Point e){
        if(dragged){
            for(Shape shape:selectedShapes){
                shape.setCurrentLocation(new Point(e.getX(), e.getY()));
                shape.updateLocation();
            }
        }
    }
    /**
     * 事件响应 鼠标拖动
     * @param e MouseEvent
     */
    private void graphicsMouseDragged(MouseEvent e) {
        Point p = new Point(e.getX(), e.getY());
        switch (actionStatus) {
            case SELECTING: selecting(p); break;
            case RESHAPING: reshaping(p); break;
            case DRAWING: drawing(p); break;
            case MOVING: moving(p); break;
        }
        repaint();
    }

    /**
     * 颜色填充
     * @param point 单击的点
     */
    public void fillColor(Point point){
        for(Shape shape: shapeList){
            if (!visibleLayerList.contains(shape.getLayerId())) continue;
            if(shape.isSelected(point)){ // 得到被选择的图形
                setCurrentShape(shape); // 设置图像
            }
        }
        currentShape.setFillColor(color); // 填充
        Affair affair = getSimpleAffair(ActionType.MOTIFY, currentShape);
        reshape(currentShape, currentShape);
        addAffair(affair);
        updatedShapes.add(currentShape);
        outToServer();
        repaint();
    }

    /**
     * 事件响应 鼠标单击
     * @param e MouseEvent
     */
    private void graphicsMouseClicked(MouseEvent e) {
        Point point = new Point(e.getX(), e.getY());
        switch (actionStatus){
            case FILLING: fillColor(point); break; // 填充
        }
    }
    /**
     * 退出, 并向服务器发送字符串数据
     * @throws IOException io
     */
    public void exit() throws IOException {
        if (server == null) return;
        os.writeObject(new String("exit"));
    }

    /**
     * 向服务器发送数据
     */
    public void outToServer(){
        if (server == null) return;
        try {
            os.writeObject(updatedShapes); // send object to server
            os.flush();
            os.reset();
            System.out.println("out to server: " + updatedShapes.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        updatedShapes.clear(); // clear
    }


    /**
     * 初始化基础组件和添加事件监听
     */
    private void initComponents(){
        //======== this ========
        setFocusCycleRoot(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                graphicsMouseClicked(e);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                graphicsMousePressed(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                graphicsMouseReleased(e);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                graphicsMouseDragged(e);
            }
        });
        setLayout(new FlowLayout());
    }

    /**
     * 保存图形
     */
    public void saveShapes(String pathname) {
        Save.writeObjectToFile(shapeList, pathname);
    }

}
