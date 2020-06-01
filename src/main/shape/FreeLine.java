package main.shape;

import main.util.CloneUtil;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

/**
 * 任意线条
 */
public class FreeLine extends Shape {
    private List<Point> points; // 线条的一系列点坐标
    private List<Point> opoints; // 原始的点坐标
    private List<FreeLine> lines; // 历史对象数据，用于undo以及redo
    private int index = -1; // 历史操作索引

    /**
     * 构造函数
     * 传入起点位置
     * @param a Point a
     */
    public FreeLine(Point a, int id){
        super(id);
        points = new ArrayList<>();
        opoints = new ArrayList<>();
        points.add(a);
        for(Point point : points){
            opoints.add(CloneUtil.clone(point));
        }
        currentPoint = CloneUtil.clone(points.get(points.size()-1));
        lines = new ArrayList<>();
    }

    public List<Point> getPoints() {
        return points;
    }

    /**
     * 相当于深拷贝
     * @param newPoints 被复制的点列表
     */
    public void setPoints(List<Point> newPoints) {
        int len = points.size();
        for(int i = 0; i < len; i++) {
            points.get(i).setX(newPoints.get(i).getX());
            points.get(i).setY(newPoints.get(i).getY());
        }

    }

    public List<Point> getOpoints() {
        return opoints;
    }

    public void setOpoints(List<Point> opoints) {
        this.opoints = new ArrayList<>();
        for(int i = 0; i < opoints.size(); i++) {
            this.opoints.add(CloneUtil.clone(opoints.get(i)));
        }
    }

    public List<FreeLine> getLines() {
        return lines;
    }

    public void setLines(List<FreeLine> lines) {
        this.lines = lines;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    @Override
    public void draw(Graphics2D graphics2D) {
        // 抗锯齿
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 设置画笔
        graphics2D.setColor(color);
        graphics2D.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int len = points.size();
        // 将相邻的两个点进行连线从而绘制曲线
        GeneralPath path = new GeneralPath(); // 生成路径
        path.moveTo(points.get(0).getX(), points.get(0).getY()); // 路径起点
        int count = 0;
        for(int i = 0; i < len-3; i++){
            Point sp = points.get(i+1);
            Point mp = points.get(i+2);
            // 防止点过于密集导致的抖动, 两次坐标采集距离过近时, count++, 当count > MAX_COUNT时, 不绘制
            if(Math.abs(sp.getX() - mp.getX()) < 2 && Math.abs(sp.getY() - mp.getY()) < 2 && count < 5){
                count++;
                continue;
            }
            count = 0;
            path.quadTo(sp.getX(), sp.getY(),mp.getX(), mp.getY()); // 采用二次曲线连接
        }
        graphics2D.draw(path);
    }

    /**
     * 调整
     * @param other 将目标期望封装成对象
     */
    @Override
    public void reshape(Shape other) {
        FreeLine line = (FreeLine) other;
        this.setPoints(line.points);
        this.stroke = other.stroke;
        color = CloneUtil.clone(line.color);
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
        lines.add(index, CloneUtil.clone(this));
    }

    /**
     * 撤回调整，逆向操作
     */
    @Override
    public void unReshape()  {
        index--;
        FreeLine line = lines.get(index);
        reshape(line);
    }


    /**
     * 重调整
     */
    @Override
    public void reReshape() {
        index++;
        FreeLine line = lines.get(index);
        reshape(line);
    }
    @Override
    public void updatePosition(Point point) {
        // can't resize
        Point p = new Point(point.getX(), point.getY());
        points.add(p);
    }

    @Override
    public void updateLocation() {
        int dx = getDx();
        int dy = getDy();

        for(int i = 0; i < points.size(); i++){
            points.get(i).setX(opoints.get(i).getX() + dx);
            points.get(i).setY(opoints.get(i).getY() + dy);
        }

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
        // 如果dot在套索区域内部, 则被选中
        for(Point point : points){
            if(point.greaterThan(lasso.getA()) && point.lessThan(lasso.getB())){
                return true;
            }
        }
        return false;
    }
    @Override
    public void storeOrigin() {
        setOpoints(points);
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
        if(obj.getClass() == FreeLine.class) {
            FreeLine line = (FreeLine) obj;
            for(int i = 0; i < points.size(); ++i){
                if(!points.get(i).equals(line.points.get(i))){
                    return false;
                }
            }
            return color.equals(line.color) && stroke == line.stroke && uuid.equals(line.uuid);
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
        int res = points != null ? points.hashCode() : 0;
        res = 31 * res + (color != null ? color.hashCode() : 0);
        res = 31 * res + Float.floatToIntBits(stroke);
        return res;
    }


}
