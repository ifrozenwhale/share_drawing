package main.shape;

import main.util.CloneUtil;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * 直线
 */
public class Line extends Shape {
    private Point a; // 起始点
    private Point b; // 结束点
    private Point oa; // 初始的起始点
    private Point ob; // 初始的结束点
    private List<Line> lines; // 历史对象数据，用于undo以及redo
    private int index = -1; // 历史操作索引

    public Line(Point a, Point b, int id) {
        super(id);
        this.a = a;
        this.b = b;
        this.oa = CloneUtil.clone(a);
        this.ob = CloneUtil.clone(b);
        lines = new ArrayList<>();
    }

    public Point getA() {
        return a;
    }

    public void setA(Point a) {
        this.a = a;
    }

    public Point getB() {
        return b;
    }

    public void setB(Point b) {
        this.b = b;
    }

    public Line(float stroke, Color color, Point a, Point b) {
        super(stroke, color);
        this.a = a;
        this.b = b;
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        graphics2D.setColor(color);
        graphics2D.setStroke(new BasicStroke(stroke));
        graphics2D.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
    }

    /**
     * 调整
     *
     * @param other 将目标期望封装成对象
     */
    @Override
    public void reshape(Shape other) {
        Line line = (Line) other;
        this.a = CloneUtil.clone(line.getA());
        this.b = CloneUtil.clone(line.getB());
    }


    /**
     * 撤回操作
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
        lines.add(index, CloneUtil.clone(this));
    }

    /**
     * 撤回调整，逆向操作
     */
    @Override
    public void unReshape() {
        index--;
        Line line = lines.get(index);
        reshape(line);
    }


    /**
     * 重调整
     */
    @Override
    public void reReshape() {
        index++;
        Line line = lines.get(index);
        reshape(line);
    }

    @Override
    public void updatePosition(Point point) {
        b.setX(point.getX());
        b.setY(point.getY());
    }

    /**
     * 更新位置
     */
    @Override
    public void updateLocation() {
        int dx = getDx(); // x偏移量
        int dy = getDy(); // y偏移量
        a.setX(oa.getX() + dx);
        b.setX(ob.getX() + dx);
        a.setY(oa.getY() + dy);
        b.setY(ob.getY() + dy);
    }

    /**
     * @param lasso 套索工具
     * @return 套索内部的点
     */
    @Override
    public Point getPointInLasso(Rectangle lasso) {
        if(a.greaterThan(lasso.getA()) && a.lessThan(lasso.getB())){
            return a;
        }else if(b.greaterThan(lasso.getA()) && b.lessThan(lasso.getB())){
            return b;
        }
        return null;
    }

    /**
     * 此类型不会单个点选中
     * @param point 单个点
     * @return false
     */
    @Override
    public boolean isSelected(Point point) {
        return false;
    }

    /**
     * 判断是否被选中
     * 如果两个点在套索区域内部, 则被选中
     * 如果直线和套索边界有交点, 则被选中
     * @param lasso 套索
     * @return true or false
     */
    @Override
    public boolean isSelected(Rectangle lasso) {
        int[] x = {a.getX(), b.getX()};
        int[] y = {a.getY(), b.getY()};
        int[] xx = {lasso.getA().getX(), lasso.getA().getX(), lasso.getB().getX(), lasso.getB().getX(), lasso.getA().getX()};
        int[] yy = {lasso.getA().getY(), lasso.getB().getY(), lasso.getB().getY(), lasso.getA().getY(), lasso.getA().getY()};

        // 判断是否包含
        if ((a.greaterThan(lasso.getA()) && b.greaterThan(lasso.getA())) && (a.lessThan(lasso.getB()) && b.lessThan(lasso.getB()))) {
            return true;
        }

        // 是否有直线相交
        // 逆时针abcd
        boolean selected = false;
        for (int j = 0; j < 4; j++) {
            selected = Line2D.linesIntersect(x[0], y[0], x[1], y[1], xx[j], yy[j], xx[j + 1], yy[j + 1]);
            if (selected) return true;
        }
        return false;
    }

    /**
     * 储存初始位置
     */
    @Override
    public void storeOrigin() {
        oa.setX(a.getX());
        oa.setY(a.getY());
        ob.setX(b.getX());
        ob.setY(b.getY());
    }

    /**
     * 更新位置
     * @param point 当前的点
     */
    @Override
    public void updatePoint(Point point) {
        currentPoint.setX(point.getX());
        currentPoint.setY(point.getY());
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj) {
            return true;
        }
        if(obj.getClass() == Line.class) {
            Line line = (Line) obj;
            return line.a.equals(a) && line.b.equals(b) && line.color.equals(color) && line.stroke == stroke && line.uuid.equals(uuid);
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
        res = 31 * res + (b != null ? b.hashCode() : 0);
        res = 31 * res + (color != null ? color.hashCode() : 0);
        res = 31 * res + Float.floatToIntBits(stroke);
        return res;
    }
}
