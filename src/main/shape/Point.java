package main.shape;

import java.io.Serializable;

/**
 * 封装的Point类
 */
public class Point implements Serializable {
    private int x; // x坐标
    private int y; // y坐标


    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 小于
     * @param point 要比较的另一个点
     * @return true if this.x < point.x and this.y < point.y, else false
     */
    public boolean lessThan(Point point){
        return x < point.x && y < point.y;

    }
    /**
     * 大于
     * @param point 要比较的另一个点
     * @return true if this.x > point.x and this.y > point.y, else false
     */
    public boolean greaterThan(Point point){
        return x > point.x && y > point.y;
    }

    /**
     * 减法
     * @param p 另一个点
     * @return 新的点
     */
    public Point minus(Point p){
        return new Point(x - p.x, y - p.y);
    }

    /**
     * 相量叉乘
     * @param p 另一个点
     * @return 叉乘的结果
     */
    public int crossProduct(Point p){
        return x * p.y - y * p.x;
    }

    @Override
    public String toString() {
        return "(" + x + ","+ y + ")";
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj.getClass() == Point.class) {
            Point point = (Point) obj;
            return point.x == x && point.y == y;
        }else{
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
        int res = 19;
        res = 31 * res + x;
        res = 31 * res + y;
        return res;
    }
}
