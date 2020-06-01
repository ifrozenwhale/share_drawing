package main.app;

import main.shape.Layer;
import main.util.FileIconCreate;
import main.util.IconCreate;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;

public class LayerListCellRenderer extends DefaultListCellRenderer {
    Border lineBorder = BorderFactory.createLineBorder(new Color(0, 0, 0, 0),1, true);

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        System.out.println(value);
        if(value instanceof Layer){
            Layer layer = (Layer) value;

            File imageFile = new File("src/resource/盘子.png");
            try{
                //拿到当前文件的URI再拿到URL
                ImageIcon icon = new ImageIcon(imageFile.toURI().toURL());
                //设置图标大小
                icon.setImage(icon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT));
                setIcon(icon);
                setForeground(Color.BLACK);
                setHorizontalAlignment(LEFT);
                setText(layer.getName());

                //设置背景色
                if (layer.isVisible()){
                    setBackground(Color.WHITE);
                }else{
                    setBackground(Color.LIGHT_GRAY);
                }
                //设置文本的水平和垂直位置:比如（右上）
                setVerticalTextPosition(SwingConstants.CENTER);
                setHorizontalTextPosition(SwingConstants.RIGHT);
                if (isSelected){
                    setForeground(Color.BLACK);
                }else{
                    setForeground(Color.GRAY);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
//        if(isSelected){
//            if(index == 0){
//                System.out.println("第一个被选择了，注意鼠标按下算一次，鼠标松开算一次，只有选择发生改变时才会触发，重复点击无效果");
//            }
//        }
//        if(cellHasFocus){
//            System.out.println("???");
//        }
        return this;

    }
}

