package main.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ShapeFileFilter extends FileFilter {

        /**
         * 重写接收文件方法
         * @return true 表示显示出来
         * false 表示不显示出来
         */
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            //显示满足条件的文件
            return f.getName().endsWith(".shape");
        }

        /**
         * 这就是显示在打开框中
         */
        public String getDescription() {

            return "*.shape";
        }

}
