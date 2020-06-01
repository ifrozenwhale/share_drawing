/*
 * Created by JFormDesigner on Mon Mar 30 18:04:41 CST 2020
 */

package main.app;

import main.client.ReadProcess;
import main.config.ConfigType;
import main.config.StyleConfig;
import main.shape.ActionStatus;
import main.shape.Graphics;
import main.shape.Layer;
import main.shape.ShapeType;
import main.util.Save;
import main.util.ShapeFileFilter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
//        JFrame frame = new JFrame("Layer");
//                frame.setSize(500, 400);
//                frame.setLocationRelativeTo(null);
//final JScrollPane jsp = new JScrollPane(new LayerList());
//        jsp.setBorder(new EmptyBorder(0, 0, 0, 0));
//        frame.add(jsp, BorderLayout.CENTER);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
/**
 * @author susec
 */
public class App extends JFrame {
    /**
     * 入口Main 函数
     * @param args 参数
     */
    public static void main(String[] args) {
//         App app = new App();
//         app.setVisible(true);
    }
    private static Font font = new Font("Calibri", Font.BOLD, 14); // 设置全局字体
    private static Dimension buttonDimension = new Dimension(180, 40); // 设置
    private ReadProcess readThread; // 数据读入线程
    private Socket server; // 服务器
    private JMenuBar menuBar; // 菜单栏
    private JMenu fileMenu; // 文件菜单
    private JMenu exportMenu; // 导出菜单
    private JMenuItem imageMenuitem; // 导出图片菜单项
    private JMenuItem shapeMenuitem; // 保存图形菜单项
    private JMenuItem openMenuitem; // 打开文件菜单项
    private JMenu settingMenu; // 设置菜单

    private JMenuItem styleMenuitem; // 样式设置菜单项
    private JMenuItem backgroundMenuitem; // 背景设置菜单项
    private JMenu helpMenu; // 帮助菜单
    private JMenuItem moreMenuitem; // 更多菜单
    private Graphics graphics; // 画板
    private JToolBar toolBar; // 工具栏
    private JButton freelineButton; // 任意线条
    private JButton dotButton; // 圆点
    private JButton ovalButton; // 画圆
    private JButton lineButton; // 直线
    private JButton rectangleButton; // 矩形
    private JButton triangleButton; // 三角形

    private JButton undoButton; // 撤回
    private JButton redoButton; // 重做
    private JButton selectButton; // 选择
    private JButton reshapeButton; // 变形
    private JButton moveButton; // 移动
    private JButton styleButton; // 样式设置
    private JButton fillButton; // 填充颜色
    private JButton clearButton; // 清空
    private JButton removeButton;  // 移除

    private LayerList layerList;
    private JScrollPane jsp;

    /**
     * 构造函数
     * @param server 服务器
     */
    public App(Socket server) {
        this.server = server;
        // 初始化基础组件
        initComponents();
        // 读取保存的设置 初始化样式
        List<String> info = Save.readTxt("src/resource/setting.txt");
        graphics.setColor(new Color(Integer.parseInt(info.get(0)))); // 设置默认颜色
        graphics.setStroke(Float.parseFloat(info.get(1))); // 设置默认画笔
        graphics.setBackgroundColor(new Color(Integer.parseInt(info.get(2))));

        // 当连入服务器时, 初始化数据读入线程, 从服务器读取图形数据
        if(server != null){
            // read thread start
            readThread = new ReadProcess(server, graphics);
            new Thread(readThread).start();
        }else{
            // 请求是否新建画布
            int i = JOptionPane.showConfirmDialog(null, "whether to create a new board?");
            if(i == JOptionPane.NO_OPTION){
                initSavedShapes(); // 加载保存的图形
            }
        }
    }

    /**
     * 加载保存的图形
     * 图形被保存为.shape文件, 加载时选择数据文件, 进行初始化
     * 文件选择利用JFileChooser
     */
    private void initSavedShapes() {
        String filename = null;

        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(new ShapeFileFilter());
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int choice = jfc.showDialog(new JLabel(), "选择");
        if(choice == JFileChooser.CANCEL_OPTION){
            return; //不选择
        }
        // 得到数据文件路径
        filename = jfc.getSelectedFile().getAbsolutePath();
        // 进行初始化
        graphics.initSavedShapes(filename);

    }

    private void setGraphicsToDraw(ShapeType shapeType){
        // 设置当前画笔类型为 shapeType
        graphics.setShapeType(shapeType);
        // 设置当前操作状态为 画图
        graphics.setActionStatus(ActionStatus.DRAWING);
        // 设置鼠标指针样式为默认
        graphics.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        // 设置当前图形为空(未开始画)
        graphics.setCurrentShape(null);
    }
    /**
     * 事件响应 画圆点
     * @param e 事件
     */
    private void dotButtonActionPerformed(ActionEvent e) {
        setGraphicsToDraw(ShapeType.DOT);
    }

    /**
     * 事件响应 画圆
     * @param e 事件
     */
    private void ovalButtonActionPerformed(ActionEvent e){setGraphicsToDraw(ShapeType.OVAL);}
    /**
     * 事件响应 画直线
     * @param e 事件
     */
    private void lineButtonActionPerformed(ActionEvent e) {
        setGraphicsToDraw(ShapeType.LINE);
    }
    /**
     * 事件响应 画矩形
     * @param e 事件
     */
    private void rectangleButtonActionPerformed(ActionEvent e) {
        setGraphicsToDraw(ShapeType.RECTANGLE);
    }

    /**
     * 事件响应 画三角形
     * @param e 事件
     */
    private void triangleButtonActionPerformed(ActionEvent e) {
        setGraphicsToDraw(ShapeType.TRIANGLE);
    }

    /**
     * 事件响应 redo操作
     * @param e 事件
     */
    private void redoButtonActionPerformed(ActionEvent e) {
        graphics.redo();
    }

    /**
     * 事件响应 undo操作
     * @param e 事件
     */
    private void undoButtonActionPerformed(ActionEvent e) {
        graphics.undo();
    }

    /**
     * 事件响应 设置样式
     * 如果没有图形没选择, 设置画笔样式
     * 否则改变图形的样式
     * @param e 事件
     */
    private void styleButtonActionPerformed(ActionEvent e) {
        if(graphics.getCurrentShape() == null){ // 如果当前没有图形被选中, 则设置画笔样式
            graphics.setStyle();
        }else{ // 否则改变被选中图形的样式
            graphics.changeStyle();
        }
    }

    /**
     * 事件响应 导出图片
     * 导出图片前, 需要利用select工具选中导出区域
     * 导出过程使用系统截图实现
     * 导出格式为jpg, 文件保存使用JFileChooser自定义路径
     * @param e 事件
     */
    private void imageMenuitemActionPerformed(ActionEvent e) {
        // 没有选定导出区域, 进行提示
        if(graphics.getSelectedShapes().isEmpty()){
            ErrorView errorView = new ErrorView("select area to export");
            errorView.setVisible(true);
            return;
        }

        // 设置导出区域
        java.awt.Point pa = graphics.getAbsoluteA(); // 得到矩形左上角
        java.awt.Point pb = graphics.getAbsoluteB(); // 得到矩形右下角
        Rectangle rec = new Rectangle((int)pa.getX(),
                (int)pa.getY(),
                (int)(pb.getX() - pa.getX()),
                (int)(pb.getY() - pa.getY())); // 构造矩形
        graphics.setActionStatus(ActionStatus.SELECTING); // 设置画板状态
        // 文件选择器
        JFileChooser chooser = new JFileChooser();
        //后缀名过滤器
        chooser.setFileFilter(new FileNameExtensionFilter("image(*.jpg)", "jpg"));

        //下面的方法将阻塞
        int option = chooser.showSaveDialog(null);
        if( option == JFileChooser.CANCEL_OPTION){
            return;
        }
        if(option == JFileChooser.APPROVE_OPTION){	//假如用户选择了保存
            File file = chooser.getSelectedFile();
            String fname = chooser.getName(file);	//从文件名输入框中获取文件名
            // 假如用户填写的文件名不带我们制定的后缀名，那么我们给它添上后缀
            if(!fname.contains(".jpg")){
                file = new File(chooser.getCurrentDirectory(),fname+".jpg");
            }

            // 导出图片
            Save.savePic(rec, "jpg", file.getAbsolutePath());
        }

    }

    /**
     * 保存图形
     * 将图形数据保存为文件, 后缀为.shape, 文件路径由JFileChooser选择
     * 保存了所有的图形数据, 但是操作数据不会被记录, 即不保存撤回以及重做的数据
     */
    private void saveShapes(){
        String pathname = null;
        JFileChooser chooser = new JFileChooser();
        //后缀名过滤器
        chooser.setFileFilter(new FileNameExtensionFilter("image(*.shape)", "shape"));
        //下面的方法将阻塞
        int option = chooser.showSaveDialog(null);
        if( option == JFileChooser.CANCEL_OPTION){
            return;
        }
        if(option == JFileChooser.APPROVE_OPTION){	//假如用户选择了保存
            File file = chooser.getSelectedFile();
            String fname = chooser.getName(file);	//从文件名输入框中获取文件名
            // 假如用户填写的文件名不带我们制定的后缀名，那么我们给它添上后缀
            if(!fname.substring(fname.length() - 4).equals(".shape")){
                pathname = chooser.getCurrentDirectory() + File.separator + fname + ".shape";
            }
            // 保存对象
            try{
                graphics.saveShapes(pathname);
            }catch (Exception exp){
                JOptionPane.showMessageDialog(null, "保存错误");
            }
        }
    }
    /**
     * 保存图形菜单响应
     * @param e 事件
     */
    private void shapeMenuitemActionPerformed(ActionEvent e) {
        saveShapes();
    }

    /**
     * 打开文件菜单响应
     * @param e 事件
     */
    private void openMenuitemActionPerformed(ActionEvent e) {
        initSavedShapes();
    }

    /**
     * 事件响应 设置背景
     * @param e 事件
     */
    private void backgroundMenuitemActionPerformed(ActionEvent e) {
        // 颜色选择器
        Color color = JColorChooser.showDialog(this, "background", graphics.getBackgroundColor());
        graphics.setBackgroundColor(color);
        // 设置保存文件路径
        String path = "src/resource/setting.txt";
        List<String> info = Save.readTxt(path);
        // 保存设置
        Save.writeTxt(path, info.get(0) + "\n" + info.get(1) + "\n" + color.getRGB());
    }

    /**
     * 更多菜单事件响应
     * @param e 事件
     */
    private void moreMenuitemActionPerformed(ActionEvent e) {
        // TODO about more menu
        JOptionPane.showMessageDialog(null, "这是重庆大学2018级弘深电子田润泽同学的Java课设项目." +
                "1. free line为任意线条，dot为实心圆点，line为直线，triangle为等腰三角形，rectangle为矩形。\n" +
                "2. select为套索工具，绘制矩形。可选中整个图形，可选中图形的顶点，可选中图形的边，可选中1个或者多个图形。\n" +
                "3. move为移动操作。select选中图形后，以鼠标按下位置为起始点，拖动鼠标进行移动。\n" +
                "4. reshape为变形操作。需通过select选中图形的某个顶点，然后点击reshape进入变形模式，拖动顶点完成变形。\n" +
                "5. style为样式设置，可以设置颜色和线条粗细。如若未选中图形，则只设置画笔样式；选中图形，则对选中图形进行样式的修改。\n" +
                "6. fill为填充操作。点击fill后，选择颜色，再点击图形内部（点中多个，选中最后绘制的图形），完成填充。\n" +
                "7. clear为清空画板。\n" +
                "8. remove为移除图形，需要先select选择1个或多个图形，再点击remove进行移除。\n" +
                "9. undo和redo支持上述所有操作，进行回退和重做。\n" +
                "10. File菜单栏支持导出图片，支持保存为.shape数据文件，启动程序时可以打开.shape文件。\n" +
                "11. Settings菜单栏style支持默认样式的修改，即启动应用后的画笔颜色和粗细；\n" +
                "12. Settings中background项为设置画板的背景颜色，此设置将会被保存，下次启动时默认颜色即设置的颜色。\n" +
                "13. 登录时选择加入服务器，则会同步服务器上的图形数据，进行同步、共享绘图。\n" +
                "14. 登录时不加入服务器，选择新建画布，则生成空画板；否则选择.shape文件初始化画板图形。\n" +
                "\n");
    }
    /**
     * 事件响应 关闭窗口
     * @param e 事件
     */
    private void thisWindowClosing(WindowEvent e) {
        // 询问是否保存图形
        int i = JOptionPane.showConfirmDialog(null, "whether to save shapes to file?");
        if(i == JOptionPane.YES_OPTION){
            saveShapes();
        }else if(i == JOptionPane.CANCEL_OPTION){
            return;
        }
        try {
            graphics.exit();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
    /**
     * 事件响应 调整尺寸
     * @param e 事件
     */
    private void reshapeButtonActionPerformed(ActionEvent e) {
        graphics.setActionStatus(ActionStatus.RESHAPING);
        graphics.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
    }

    /**
     * 事件响应 清空画布
     * @param e 事件
     */
    private void clearButtonActionPerformed(ActionEvent e) {
        graphics.clear();
    }

    /**
     * 事件响应 移动图形
     * @param e 事件
     */
    private void moveButtonActionPerformed(ActionEvent e) {
        graphics.setActionStatus(ActionStatus.MOVING);
        graphics.setCursor(new Cursor(Cursor.MOVE_CURSOR));
    }

    /**
     * 事件响应 套索选中
     * @param e 事件
     */
    private void selectButtonActionPerformed(ActionEvent e) {
        graphics.setActionStatus(ActionStatus.SELECTING);
        graphics.setCursor(new Cursor(Cursor.MOVE_CURSOR));
    }

    /**
     * 事件响应 填充
     * @param e 事件
     */
    private void fillButtonActionPerformed(ActionEvent e) {
        graphics.setActionStatus(ActionStatus.FILLING);
        Color color = JColorChooser.showDialog(this, "Fill color", graphics.getColor());
        graphics.setColor(color);
        graphics.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * 事件响应 画任意线条
     * @param e 事件
     */
    private void freelineButtonActionPerformed(ActionEvent e) {
        setGraphicsToDraw(ShapeType.FREE_LINE);
    }
    /**
     * 事件响应 移除图形
     * @param e 事件
     */
    private void removeButtonActionPerformed(ActionEvent e) {
        graphics.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        graphics.remove();
        repaint();
    }
    /**
     * 事件响应 样式设置
     * @param e 事件
     */
    private void styleMenuitemActionPerformed(ActionEvent e) {
        StyleConfig styleConfig = new StyleConfig(graphics, ConfigType.GLOBAL);
        styleConfig.setVisible(true);
    }

    /**
     * 初始化基础组件
     * 设置样式
     */
    private void initComponents() {
        menuBar = new JMenuBar();
        fileMenu = new JMenu();
        exportMenu = new JMenu();
        imageMenuitem = new JMenuItem();
        shapeMenuitem = new JMenuItem();
        settingMenu = new JMenu();
        styleMenuitem = new JMenuItem();
        backgroundMenuitem = new JMenuItem();
        helpMenu = new JMenu();
        openMenuitem = new JMenuItem();
        moreMenuitem = new JMenuItem();
        graphics = new Graphics(server);
        toolBar = new JToolBar();
        freelineButton = new JButton();
        dotButton = new JButton();
        ovalButton = new JButton();
        lineButton = new JButton();
        rectangleButton = new JButton();
        triangleButton = new JButton();
        undoButton = new JButton();
        redoButton = new JButton();
        selectButton = new JButton();
        reshapeButton = new JButton();
        moveButton = new JButton();
        styleButton = new JButton();
        fillButton = new JButton();
        clearButton = new JButton();
        removeButton = new JButton();

        layerList = new LayerList(graphics);
        jsp = new JScrollPane();
        //======== this ========
        setTitle("White Board");
        setMinimumSize(new Dimension(600, 400));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(5, 5));

        //======== menuBar ========
        {
            menuBar.setMargin(new Insets(5, 40, 5, 5));
            menuBar.setBorder(null);

            //======== fileMenu ========
            {
                fileMenu.setText("File");
                fileMenu.setIcon(UIManager.getIcon("FileChooser.upFolderIcon"));
                fileMenu.setBorderPainted(false);

                //======== exportMenu ========
                {
                    exportMenu.setText("save");
                    exportMenu.setBorderPainted(false);

                    //---- imageMenuitem ----
                    imageMenuitem.setText("image");
                    imageMenuitem.setBorderPainted(false);
                    imageMenuitem.addActionListener(this::imageMenuitemActionPerformed);
                    exportMenu.add(imageMenuitem);

                    //---- imageMenuitem ----
                    shapeMenuitem.setText("shape");
                    shapeMenuitem.setBorderPainted(false);
                    shapeMenuitem.addActionListener(this::shapeMenuitemActionPerformed);
                    exportMenu.add(shapeMenuitem);
                }
                fileMenu.add(exportMenu);
                //======== openMenuitem ========
                {
                    openMenuitem.setText("open");
                    openMenuitem.setBorderPainted(false);
                    openMenuitem.addActionListener(this::openMenuitemActionPerformed);
                    
                }
            }
            menuBar.add(fileMenu);

            //======== settingMenu ========
            {
                settingMenu.setText("Settings");
                settingMenu.setIcon(UIManager.getIcon("FileView.fileIcon"));
                settingMenu.setBorderPainted(false);

                //---- styleMenuitem ----
                styleMenuitem.setText("style");
                styleMenuitem.setBorderPainted(false);
                styleMenuitem.addActionListener(this::styleMenuitemActionPerformed);
                settingMenu.add(styleMenuitem);

                //---- backgroundMenuitem ----
                backgroundMenuitem.setText("background");
                backgroundMenuitem.setBorderPainted(false);
                backgroundMenuitem.addActionListener(this::backgroundMenuitemActionPerformed);
                settingMenu.add(backgroundMenuitem);
            }
            menuBar.add(settingMenu);

            //======== helpMenu ========
            {
                helpMenu.setText("Help");
                helpMenu.setIcon(UIManager.getIcon("TextField.darcula.search.icon"));
                helpMenu.setBorderPainted(false);

                //---- moreMenuitem ----
                moreMenuitem.setText("more");
                moreMenuitem.setBorderPainted(false);
                moreMenuitem.addActionListener(this::moreMenuitemActionPerformed);
                helpMenu.add(moreMenuitem);
            }
            menuBar.add(helpMenu);
        }
        contentPane.add(menuBar, BorderLayout.NORTH);
        contentPane.add(graphics, BorderLayout.CENTER);

        //======== toolBar ========
        {
            toolBar.setAlignmentY(10.5F);
            toolBar.setBorder(null);
            toolBar.setFloatable(false);
            toolBar.setEnabled(false);
            toolBar.setFocusCycleRoot(true);
            toolBar.setPreferredSize(new Dimension(800, 35));
            toolBar.addSeparator(new Dimension(30, 10));

            //---- freelineButton ----
            setToolButtonUI(freelineButton, "free line", new Color(152, 203, 203));
            freelineButton.addActionListener(this::freelineButtonActionPerformed);
            toolBar.add(freelineButton);

            //---- dotButton ----
            setToolButtonUI(dotButton, "dot", new Color(135, 205, 205));
            dotButton.addActionListener(this::dotButtonActionPerformed); // 添加事件监听
            toolBar.add(dotButton); // 添加组件

            //---- ovalButton ----
            setToolButtonUI(ovalButton, "oval", new Color(114, 178, 178));
            ovalButton.addActionListener(this::ovalButtonActionPerformed); // 添加事件监听
            toolBar.add(ovalButton); // 添加组件

            //---- lineButton ----
            setToolButtonUI(lineButton, "line", new Color(177, 223, 223));
            lineButton.addActionListener(this::lineButtonActionPerformed);
            toolBar.add(lineButton);

            //---- rectangleButton ----
            setToolButtonUI(rectangleButton, "rectangle", new Color(135, 205, 205));
            rectangleButton.addActionListener(this::rectangleButtonActionPerformed);
            toolBar.add(rectangleButton);

            //---- triangleButton ----
            setToolButtonUI(triangleButton, "triangle", new Color(177, 223, 223));
            triangleButton.addActionListener(this::triangleButtonActionPerformed);
            toolBar.add(triangleButton);

            // separator
            toolBar.addSeparator(new Dimension(20, 10));

            //---- undoButton ----
            setToolButtonUI(undoButton, "undo", new Color(203, 205, 205));
            undoButton.addActionListener(this::undoButtonActionPerformed);
            toolBar.add(undoButton);

            //---- redoButton ----
            setToolButtonUI(redoButton, "redo", new Color(148, 152, 154));
            redoButton.addActionListener(this::redoButtonActionPerformed);
            toolBar.add(redoButton);
            toolBar.addSeparator(new Dimension(20, 10));

            //---- selectButton ----
            setToolButtonUI(selectButton, "select", new Color(243, 157, 157));
            selectButton.addActionListener(this::selectButtonActionPerformed);
            toolBar.add(selectButton);

            //---- reshapeButton ----
            setToolButtonUI(reshapeButton, "reshape", new Color(246, 205, 205));
            reshapeButton.addActionListener(this::reshapeButtonActionPerformed);
            toolBar.add(reshapeButton);

            //---- moveButton ----
//            moveButton.setActionCommand("relocate");
            setToolButtonUI(moveButton, "move", new Color(243, 210, 210));
            moveButton.addActionListener(this::moveButtonActionPerformed);
            toolBar.add(moveButton);

            //---- styleButton ----
            setToolButtonUI(styleButton, "style", new Color(246, 205, 205));
            styleButton.addActionListener(this::styleButtonActionPerformed);
            toolBar.add(styleButton);

            //---- fillButton ----
            setToolButtonUI(fillButton, "fill", new Color(243, 210, 210));
            fillButton.addActionListener(this::fillButtonActionPerformed);
            toolBar.add(fillButton);

            // separator
            toolBar.addSeparator(new Dimension(20, 10));

            //---- clearButton ----
            setToolButtonUI(clearButton, "clear", new Color(60, 60, 60));
            clearButton.setForeground(Color.white);
            clearButton.addActionListener(this::clearButtonActionPerformed);
            toolBar.add(clearButton);

            //---- removeButton ----
            setToolButtonUI(removeButton, "remove", new Color(87, 86, 86));
            removeButton.setForeground(Color.white);
            removeButton.addActionListener(this::removeButtonActionPerformed);
            toolBar.add(removeButton);
        }
        // JSP
        jsp = new JScrollPane(layerList);
        jsp.setBorder(new EmptyBorder(0, 0, 0, 0));
        add(jsp, BorderLayout.EAST);
        contentPane.add(toolBar, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
    }

    /**
     * 设置工具栏按钮的一般样式
     * @param button 按钮
     * @param name text
     * @param color 背景色
     */
    private void setToolButtonUI(JButton button, String name,  Color color){
        button.setText(name);
        button.setFont(font);
        button.setBorderPainted(false);
        button.setMaximumSize(buttonDimension);
        button.setBackground(color);
    }



}