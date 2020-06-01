package main.shape;

import main.util.CloneUtil;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
public class Oval extends Shape {
    private Point a;
    private Point b;
    private Point o;
    private Point oa;
    private Point ob;
    private List<Oval> ovalList;
    private int index;

    public Oval(Point a, int id) {
        super(id);
        this.a = a;
        this.oa = CloneUtil.clone(a);
        this.ob = CloneUtil.clone(a);
        // current point
        b = CloneUtil.clone(a);
        o = CloneUtil.clone(a);
        currentPoint = CloneUtil.clone(b);
        ovalList = new ArrayList<>();
        index = -1;
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        graphics2D.setColor(color);
        graphics2D.setStroke(new BasicStroke(stroke));
        if(toBeFilled){
            graphics2D.fillOval(a.getX(), a.getY(), b.getX() - a.getX(), b.getY() - a.getY());
        }else{
            graphics2D.drawOval(a.getX(), a.getY(), b.getX() - a.getX(), b.getY() - a.getY());
        }
    }

    @Override
    public void unDraw() {
        index--;
    }

    @Override
    public void log() {
        index++;
        ovalList.add(index, CloneUtil.clone(this));
    }

    public void reshape(Shape other){
        Oval oval = (Oval) other;
        a = CloneUtil.clone(oval.a);
        this.stroke = other.stroke;
        this.toBeFilled = other.toBeFilled;
        color = CloneUtil.clone(oval.color);
    }

    @Override
    public void unReshape() {
        index--;
        Oval oval = ovalList.get(index);
        reshape(oval);
    }

    @Override
    public void reReshape() {
        index++;
        Oval oval = ovalList.get(index);
        reshape(oval);

    }

    @Override
    public void updatePosition(Point point) {
//        r = (int) Math.sqrt(Math.pow(point.getX() - a.getX(), 2) + Math.pow(point.getY() - a.getY(), 2));
        b.setX(point.getX());
        b.setY(point.getY());
    }

    @Override
    public void updateLocation() {
        int dx = getDx(), dy = getDy();
        a.setX(oa.getX() + dx);
        a.setY(oa.getY() + dy);
        b.setX(ob.getX() + dx);
        b.setY(ob.getY() + dy);
        o.setX((a.getX() + b.getY())/ 2);
        o.setX((a.getX() + b.getX())/ 2);
        o.setY((a.getY() + b.getY())/ 2);
        o.setY((a.getY() + b.getY())/ 2);

    }

    @Override
    public Point getPointInLasso(Rectangle lasso) {
        if (b.greaterThan(lasso.getA()) && b.lessThan(lasso.getB())){
            return a;
        }else{
            return null;
        }
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


    @Override
    public void storeOrigin() {
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
            Oval oval = (Oval) obj;
            return oval.a.equals(a) && oval.b == b && oval.color.equals(color) && oval.stroke == stroke && oval.uuid.equals(uuid);
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
