package main.util;

import javax.swing.*;
import java.awt.*;

public class IconCreate {
    public static ImageIcon getIcon(int w, int h, String url){
        ImageIcon iconImg = new ImageIcon(url);
        //改变图片的大小
        Image temp = iconImg.getImage().getScaledInstance(w, h, iconImg.getImage().SCALE_DEFAULT);
        return new ImageIcon(temp);
    }
}
