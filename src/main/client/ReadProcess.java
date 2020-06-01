package main.client;

import main.shape.Graphics;
import main.shape.Shape;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReadProcess implements Runnable {
    private Graphics graphics; // 画板
    private Socket server; // 服务器
    private ObjectInputStream is = null; // 对象输入流
    private boolean active; // 是否有效
    public ReadProcess(Socket server) {
        this.server = server;
    }
    public ReadProcess(Socket server, Graphics graphics) {
        this.graphics = graphics;
        this.server = server;
        active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void run() {
        try {
            is = new ObjectInputStream((server.getInputStream())); // 初始化输入流
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (active){ // 只有有效的时候才运行
            try {
                Object object = is.readObject(); // 读取传来的对象
                if(object.getClass() == String.class){ // 如果是String字符串, 表示服务器退出
                    active = false; // 线程结束
                    continue;
                }
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS"); // 输出时间
                // display time
                System.out.println(df.format(new Date()));
                // get shapes from server
                List<Shape> shapes = (List<Shape>) object; // 转化为图形列表
                // update shape to client
                graphics.updateShapeList(shapes); // 更新图形池

//                System.out.println("read from server: " + shapes.toString());
//                System.out.println();

            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "已经退出！");
            }
        }
    }
}
