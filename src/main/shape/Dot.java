package main.shape;

import main.util.CloneUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 点 dot
 */
public class Dot extends Shape {
    private Point a; // 点坐标
    private Point oa; // 原始点坐标 original point
    private int index = -1; // 操作历史索引
    private List<Dot> dots; // 历史镜像
    public Dot(Point a, int id) {
        super(id);
        this.a = a;
        this.oa = CloneUtil.clone(a);
        currentPoint = CloneUtil.clone(a);
        dots = new ArrayList<>(); // 初始化一系列点
    }

    /**
     * 初始化
     * @param a 点
     * @param r 半径
     * @param color 颜色
     */
    public Dot(Point a, float r, Color color){
        super(r, color);
        this.a = a;
        this.oa = CloneUtil.clone(a);

        dots = new ArrayList<>();
    }

    public Point getA() {
        return a;
    }
    public void setA(Point a) {
        this.a = a;
    }


    @Override
    public void draw(Graphics2D graphics2D) {
        graphics2D.setColor(color);
        graphics2D.setStroke(new BasicStroke(stroke));
        graphics2D.drawOval(a.getX(), a.getY(), (int) stroke, (int) stroke);
    }


    /**
     * 调整
     * @param other 将目标期望封装成对象
     */
    @Override
    public void reshape(Shape other) {
        Dot dot = (Dot) other;
        this.a = CloneUtil.clone(dot.getA());
        this.stroke = other.stroke;
        this.color = CloneUtil.clone(other.color);
    }


    /**
     * 抹除绘图
     */
    @Override
    public void unDraw() {
        index--;
    }
    /**
     * 记录操作数据
     */
    @Override
    public void log() {
        index++;
        dots.add(index, CloneUtil.clone(this));
    }

    /**
     * 撤回调整，逆向操作
     */
    @Override
    public void unReshape()  {
        index--;
        Dot dot = dots.get(index);
        reshape(dot);
    }


    /**
     * 重调整
     */
    @Override
    public void reReshape() {
        index++;
        Dot dot = dots.get(index);
        reshape(dot);
    }

    @Override
    public void updatePosition(Point point) {
        // do nothing
    }

    @Override
    public void updateLocation() {
        int dx = getDx();
        int dy = getDy();
        a.setX(oa.getX() + dx);
        a.setY(oa.getY() + dy);
    }

    @Override
    public Point getPointInLasso(Rectangle lasso) {
        return null;
    }

    @Override
    public boolean isSelected(Point point) {
        return false;
    }


    @Override
    public boolean isSelected(Rectangle lasso) {
        // 判断是否包含
        return a.greaterThan(lasso.getA()) && a.lessThan(lasso.getB());
    }
    @Override
    public void storeOrigin() {
        oa.setX(a.getX());
        oa.setY(a.getY());
    }

    @Override
    public void updatePoint(Point point) {
        // do nothing
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj) {
            return true;
        }
        if(obj.getClass() == Dot.class) {
            Dot dot = (Dot) obj;
            return dot.a.equals(a) && dot.color.equals(color) && dot.stroke == stroke && dot.uuid.equals(uuid);
        } else {
            return false;
        }
    }

    /**
     * 重写 Hashcode方法
     * 使用Effective Java中的推荐方式
     * @return hashcode
     */
    @Override
    public int hashCode() {
        int res = a != null ? a.hashCode() : 0;
        res = 31 * res + (color != null ? color.hashCode() : 0);
        res = 31 * res + Float.floatToIntBits(stroke);
        return res;
    }

}
