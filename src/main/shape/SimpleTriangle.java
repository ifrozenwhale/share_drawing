package main.shape;

import main.util.CloneUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 简单三角形
 */
public class SimpleTriangle {
    Point a; // 三角形上方的点
    Point b; // 三角形左下角
    Point c; // 三角形右下角

    private static void actionPerformed(ActionEvent e) {
    }

    public void test(){
        JButton button = new JButton();
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // do something
            }
        });

        button.addActionListener(e -> {buttonPerformed(e);});

        button.addActionListener(this::buttonPerformed);
    }

    private void buttonPerformed(ActionEvent actionEvent) {
        // do something
    }

    public SimpleTriangle(Point a, Point b, Point c) {
        this.a = CloneUtil.clone(a);
        this.b = CloneUtil.clone(b);
        this.c = CloneUtil.clone(c);

    }

    /**
     * 计算面积
     * @return 面积
     */
    public double getArea(){
        Point AB = a.minus(b);
        Point BC = c.minus(b);
        return Math.abs(AB.crossProduct(BC) / 2.0);
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
}
