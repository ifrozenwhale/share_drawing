package main.shape;
import main.util.CloneUtil;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * 矩形类
 */
public class Rectangle extends Shape{
    private Point a; // 矩形的左上角
    private Point b; // 矩形的右下角
    private Point oa; // 初始的 矩形左上角
    private Point ob; // 初始的 矩形右下角

    private List<Rectangle> rectangles; // 历史对象数据，用于undo以及redo
    private int index; // 历史操作索引
    public Rectangle(Point a, Point b, int id) {
        super(id);
        this.a = a;
        this.b = b;
        this.oa = CloneUtil.clone(a);
        this.ob = CloneUtil.clone(b);
        currentPoint = CloneUtil.clone(b);
        rectangles = new ArrayList<>();
        index = -1;

    }


    @Override
    public void draw(Graphics2D graphics2D) {
        graphics2D.setColor(color);
        graphics2D.setStroke(new BasicStroke(stroke));
        if(toBeFilled) { // 如果是填充模式, 则绘制填充色
            graphics2D.fillRect(a.getX(), a.getY(), b.getX() - a.getX(), b.getY() - a.getY());
        } // 否则绘制线条
        else graphics2D.drawRect(a.getX(), a.getY(), b.getX() - a.getX(), b.getY() - a.getY());
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
        rectangles.add(index, CloneUtil.clone(this));
    }


    /**
     * 重调整
     */
    @Override
    public void reReshape() {
        index++;
        Rectangle rectangle = rectangles.get(index);
        reshape(rectangle);
    }

    /**
     * 撤回调整，逆向操作
     */
    @Override
    public void unReshape()  {
        index--;
        Rectangle rectangle = rectangles.get(index);
        reshape(rectangle);
    }

    /**
     * 调整
     * @param other 将目标期望封装成对象
     */
    @Override
    public void reshape(Shape other) {
        Rectangle rectangle = (Rectangle) other;
        a = CloneUtil.clone(rectangle.a);
        b = CloneUtil.clone(rectangle.b);
        this.stroke = other.stroke;
        color = CloneUtil.clone(rectangle.color);
        this.toBeFilled = other.toBeFilled;
    }


    @Override
    public String toString() {
        return "Rectangle{" +
                "a=" + a +
                ", b=" + b +
                '}';
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

    @Override
    public void updatePosition(Point point) {
        b.setX(point.getX());
        b.setY(point.getY());
    }

    @Override
    public void updateLocation() {
        int dx = getDx();
        int dy = getDy();
        a.setX(oa.getX() + dx);
        b.setX(ob.getX()+ dx);
        a.setY(oa.getY() + dy);
        b.setY(ob.getY() + dy);
    }

    @Override
    public Point getPointInLasso(Rectangle lasso) {
        if(a.greaterThan(lasso.getA()) && a.lessThan(lasso.getB())){
            return a;
        }else if(b.greaterThan(lasso.getA()) && b.lessThan(lasso.getB())){
            return b;
        }
        return null;
    }

    @Override
    public boolean isSelected(Point point) {
        return point.lessThan(b) && point.greaterThan(a);
    }

    @Override
    public boolean isSelected(Rectangle lasso) {
        // x[], y[] 对应于从左上角开始, 逆时针一圈, 回到左上角
        int[] x = {a.getX(), a.getX(), b.getX(), b.getX(), a.getX()};
        int[] y = {a.getY(), b.getY(), b.getY(), a.getY(), a.getY()};
        // xx[]. yy[] 对应于矩形套索的左上角, 逆时针一圈, 回到左上角
        int[] xx = {lasso.getA().getX(), lasso.getA().getX(), lasso.getB().getX(), lasso.getB().getX(), lasso.getA().getX()};
        int[] yy = {lasso.getA().getY(), lasso.getB().getY(), lasso.getB().getY(), lasso.getA().getY(), lasso.getA().getY()};
        boolean selected = false;
        // 判断矩形是否在套索区域内部
        if(b.lessThan(lasso.getB()) && a.greaterThan(lasso.getA())){
            return true;
        }
        // 判断矩形和套索边界是否有交点
        // 逆时针检查, 遍历所有可能组成的二元直线
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                selected = Line2D.linesIntersect(x[i], y[i], x[i+1], y[i+1], xx[j], yy[j], xx[j+1], yy[j+1]);
                if(selected) return true;
            }
        }
        return false;
    }

    public void storeOrigin(){
        oa.setX(a.getX());
        oa.setY(a.getY());
        ob.setX(b.getX());
        ob.setY(b.getY());
    }

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
        if(obj.getClass() == Rectangle.class) {
            Rectangle rectangle = (Rectangle) obj;
            return rectangle.a.equals(a) && rectangle.b.equals(b) && rectangle.color.equals(color) && rectangle.stroke == stroke && rectangle.uuid.equals(uuid);
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
