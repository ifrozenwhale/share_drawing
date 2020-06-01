package main.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.MultiResolutionImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 工具类
 */
public class Save {
    /**
     * 文件读取
     * @param p 文件路径
     * @return 文件内容 List<String>
     */
    public static List<String> readTxt(String p){
        Path path = Paths.get(p);
        List<String> lines = null;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * 文件写
     * @param p 文件路径
     * @param info 文件信息
     */
    public static void writeTxt(String p, String info){
        Path path = Paths.get(p);
        try {
            Files.write(path, info.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片保存
     * @param rectangle 保存区域
     * @param format 文件格式
     * @param path 文件路径
     */
    public static void savePic(Rectangle rectangle, String format, String path){
        MultiResolutionImage myImage = null;
        try {

            myImage = new Robot().createMultiResolutionScreenCapture(rectangle); // 截屏
            ImageIO.write((RenderedImage) myImage.getResolutionVariant(rectangle.width, rectangle.height), format, new File(path));

        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中读取对象
     * @return 存储的对象
     */
    public static Object readObjectFromFile(String pathname) {
        Object o = null;
        File file = new File(pathname);
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            o = objectInputStream.readObject();
            objectInputStream.close();
            System.out.println("read object success!");
        } catch (IOException e) {
            System.out.println("read object failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * 将对象写入文件
     * @param obj 要写入的对象
     */
    public static void writeObjectToFile(Object obj, String pathname) {
        File file = new File(pathname);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(out);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();
            objectOutputStream.close();
            System.out.println("write object success!");
        } catch (IOException e) {
            System.out.println("write object failed");
            e.printStackTrace();
        }
    }
}
