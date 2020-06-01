package main.shape;

import main.util.CloneUtil;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.LinkedList;
import java.util.List;

public class Triangle extends Shape {
    private Point a;
    private Point b;
    private Point c;
    private Point oa;
    private Point ob;
    private Point oc;
    private List<Triangle> triangles;
    private int index;

    public Triangle(Point a, Point b, Point c, int layerId) {
        super(layerId);
        this.a = a;
        this.b = b;
        this.c = c;
        currentPoint = CloneUtil.clone(c);
        oa = CloneUtil.clone(a);
        ob = CloneUtil.clone(b);
        oc = CloneUtil.clone(c);
        this.triangles = new LinkedList<>();
        index = -1;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    @Override
    public String toString() {
        return "Triangle{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }

    @Override
    public void reshape(Shape other) {
        // System.out.println("Reshape before: " + this.toString());
        Triangle triangle = (Triangle) other;
        this.a = CloneUtil.clone(triangle.getA());
        this.b = CloneUtil.clone(triangle.getB());
        this.c = CloneUtil.clone(triangle.getC());
        this.toBeFilled = other.toBeFilled;
        this.stroke = other.stroke;
        this.color = new Color(other.color.getRGB());
        // System.out.println("Reshape after: " + this.toString());
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

    public Point getC() {
        return c;
    }

    public void setC(Point c) {
        this.c = c;
    }


    @Override
    public void draw(Graphics2D graphics2D) {
        graphics2D.setStroke(new BasicStroke(stroke));
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints
        .VALUE_ANTIALIAS_ON);
        graphics2D.setColor(color);
        int[] x = {a.getX(), b.getX(), c.getX()};
        int[] y = {a.getY(), b.getY(), c.getY()};
        if(toBeFilled) graphics2D.fillPolygon(x, y, 3);
        else graphics2D.drawPolygon(x, y, 3);
    }

    @Override
    public void unDraw() {
//         System.out.println("UnDraw: " + toString());
        index--;
    }

    @Override
    public void log() {
        index++;
        triangles.add(index, CloneUtil.clone(this));
    }

    @Override
    public void unReshape() {
        index--;
        Triangle triangle = triangles.get(index);
        reshape(triangle);
//         System.out.println("UnReshape: " + toString());
    }

    @Override
    public void reReshape() {
        index++;
        Triangle triangle = triangles.get(index);
        reshape(triangle);
//        System.out.println("ReReshape: " + toString());
    }

    @Override
    public void updatePosition(Point point) {
        int x = point.getX();
        int y = point.getY();
        c.setX(x);
        c.setY(y);
        b.setX(a.getX() - (x - a.getX()));
        b.setY(y);
    }


    @Override
    public void updateLocation() {
        int dx = getDx();
        int dy = getDy();
        a.setX(oa.getX() + dx);
        b.setX(ob.getX() + dx);
        a.setY(oa.getY() + dy);
        b.setY(ob.getY() + dy);
        c.setX(oc.getX() + dx);
        c.setY(oc.getY() + dy);
    }

    @Override
    public Point getPointInLasso(Rectangle lasso) {
        if(a.greaterThan(lasso.getA()) && a.lessThan(lasso.getB())){
            return a;
        }else if(b.greaterThan(lasso.getA()) && b.lessThan(lasso.getB())){
            return b;
        }else if(c.greaterThan(lasso.getA()) && b.lessThan(lasso.getB())){
            return c;
        }
        return null;
    }


    @Override
    public boolean isSelected(Point point) {
        double area_ABC = new SimpleTriangle(a, b, c).getArea();
        double area_PAB = new SimpleTriangle(point, a, b).getArea();
        double area_PAC = new SimpleTriangle(point, a, c).getArea();
        double area_PBC = new SimpleTriangle(point, b, c).getArea();
        return Math.abs(area_PAB + area_PBC + area_PAC - area_ABC) < 0.000001;
    }

    @Override
    public boolean isSelected(Rectangle lasso) {
        int[] x = {a.getX(), b.getX(), c.getX(), a.getX()};
        int[] y = {a.getY(), b.getY(), c.getY(), a.getY()};
        int[] xx = {lasso.getA().getX(), lasso.getA().getX(), lasso.getB().getX(), lasso.getB().getX(), lasso.getA().getX()};
        int[] yy = {lasso.getA().getY(), lasso.getB().getY(), lasso.getB().getY(), lasso.getA().getY(), lasso.getA().getY()};
        boolean selected = false;
        if(b.greaterThan(lasso.getA()) && c.lessThan(lasso.getB()) && a.greaterThan(lasso.getA()) && a.lessThan(lasso.getB())){
            return true;
        }
        // 是否有直线相交
        for(int i = 0; i < 3; i++){
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
        oc.setX(c.getX());
        oc.setY(c.getY());
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
        if(obj.getClass() == Triangle.class) {
            Triangle triangle = (Triangle) obj;
            return triangle.a.equals(a) && triangle.b.equals(b) && triangle.c.equals(c) && triangle.color.equals(color) && triangle.stroke == stroke && triangle.uuid.equals(uuid);
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
        res = 31 * res + (c != null ? c.hashCode() : 0);
        res = 31 * res + (color != null ? color.hashCode() : 0);
        res = 31 * res + Float.floatToIntBits(stroke);

        return res;
    }


}
