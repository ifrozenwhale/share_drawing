package main.shape;

import java.awt.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * 抽象基类
 */
public abstract class Shape implements Serializable {
    protected String uuid; // 唯一标识
    protected boolean active = true; // 标识是否有效
    protected static float DEFAULT_STROKE = 1; // 默认画笔粗细
    protected static Color DEFAULT_COLOR = Color.black; // 默认颜色
    protected static Color DEFAULT_FILL_COLOR = Color.LIGHT_GRAY; // 默认填充色
    protected float stroke; // 画笔粗细
    protected Color color = Color.BLACK; // 画笔颜色
    protected Point currentPoint; // 选中的点
    protected Point startLocation; // 起始点
    protected Point currentLocation; // 当前位置
    protected boolean toBeFilled = false; // 是否填充, 默认不填充
    protected int layerId = 0; //  图层
    public static String[] chars = new String[]{
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
        "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1",
        "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F",
        "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
        "U", "V","W", "X", "Y", "Z"
    }; // 字符列表

    /**
     * 生成对象的唯一标识符uuid
     * 用于判断两个对象是否是同一个对象
     * @return uuid
     */
    public static String getShortUuid() {
        StringBuilder stringBuffer = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int strInteger = Integer.parseInt(str, 16);
            stringBuffer.append(chars[strInteger % 0x3E]);
        }
        return stringBuffer.toString();
    }

    
    public String getUuid() {
        return uuid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * 得到鼠标位置偏移量x
     * @return 偏移量x
     */
    protected int getDx(){
        return currentLocation.getX() - startLocation.getX();
    }
    /**
     * 得到鼠标位置偏移量y
     * @return 偏移量y
     */
    protected int getDy(){
        return currentLocation.getY() - startLocation.getY();
    }

    /**
     * 得到当前别选中的点坐标(在选择过程中)
     * @return currentPoint
     */
    public Point getCurrentPoint() {
        return currentPoint;
    }

    /**
     * 得到当前点坐标(在绘图过程中)
     * @return currentLocation
     */
    public Point getCurrentLocation() {
        return currentLocation;
    }

    /**
     * 设置填充色, 并设置模式为填充模式
     * @param fillColor 填充色
     */
    public void setFillColor(Color fillColor) {
        toBeFilled = true;
        color = fillColor;
    }

    /**
     * 设置当前别选中的点坐标(在选择过程中)
     * @param currentLocation 当前点坐标
     */
    public void setCurrentLocation(Point currentLocation) {
        this.currentLocation = currentLocation;
    }

    /**
     * 设置当前点坐标(在绘图过程中)
     * @param currentPoint 当前点坐标
     */
    public void setCurrentPoint(Point currentPoint) {
        this.currentPoint = currentPoint;
    }


    public int getLayerId() {
        return layerId;
    }

    public void setLayerId(int layerId) {
        this.layerId = layerId;
    }

    public Shape(int layerId){
        uuid = getShortUuid();
        this.stroke = DEFAULT_STROKE;
        this.color = DEFAULT_COLOR;
        this.layerId = layerId;
    }

    public Shape(float stroke, Color color) {
        this.stroke = stroke;
        this.color = color;
    }


    /**
     * 绘图
     */
    public abstract void draw(Graphics2D graphics2D);

    /**
     * 调整形状
     * @param other 将期望的结果封装成对象
     */
    public void reshape(Shape other){
        color = other.color;
        stroke = other.stroke;
    };

    /**
     * 撤回上一步操作
     */
    public abstract void unDraw();

    /**
     * 记录历史对象数据
     */
    public abstract void log();

    /**
     * 撤回调整操作
     */
    public abstract void unReshape();

    /**
     * 重调整
     */
    public abstract void reReshape();

    /**
     * 更新位置(选中的点)
     * @param point 要更新的点
     */
    public abstract void updatePosition(Point point);

    /**
     * 更新位置(绘图的当前点)
     */
    public abstract void updateLocation();

    /**
     * 得到画笔粗细
     * @return stroke 画笔粗细
     */
    public float getStroke() {
        return stroke;
    }

    /**
     * 得到起始点
     * @return 起始点
     */
    public Point getStartLocation() {
        return startLocation;
    }

    /**
     * 设置起始点
     * @param startLocation 起始点
     */
    public void setStartLocation(Point startLocation) {
        this.startLocation = startLocation;
    }

    public void setStroke(float stroke) {
        this.stroke = stroke;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * 得到被套索选中的点
     * @param lasso 矩形套索
     * @return point 选中的点
     */
    public abstract Point getPointInLasso(Rectangle lasso);

    /**
     * 图形是否被单个点选中
     * @param point 点
     * @return 选中图形
     */
    public abstract boolean isSelected(Point point);

    /**
     * 图形是否被套索选中
     * @param lasso 矩形套索
     * @return 选中图形
     */
    public abstract boolean isSelected(Rectangle lasso);

    /**
     * 设置初始位置
     */
    public abstract void storeOrigin();

    /**
     * 更新位置
     * @param point 目标点
     */
    public abstract void updatePoint(Point point);
}
