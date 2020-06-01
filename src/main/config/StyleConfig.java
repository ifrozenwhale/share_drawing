/*
 * Created by JFormDesigner on Wed Apr 01 14:43:14 CST 2020
 */

package main.config;

import main.command.ActionType;
import main.command.Affair;
import main.command.Command;
import main.shape.Graphics;
import main.shape.Shape;
import main.util.Save;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author susec
 * 样式选择
 */
public class StyleConfig extends JDialog {
    // ui
    private JPanel dialogPane; // 对话框主体
    private JPanel contentPanel; // 选择区域
    private JLabel lineColorLabel; // 颜色标签
    private JButton lineColorButton; // 颜色选择器
    private JLabel strokeLabel; // 笔画标签
    private JComboBox<String> strokeStyleBox; // 笔画选择框
    private JPanel buttonBar; // 按钮区域
    private JButton okButton; // 保存
    private JButton cancelButton; // 取消

    private JLabel zoomLabel;
    private JComboBox<String> zoomBox;

    // data
    private Graphics graphics; // 画板
    private ConfigType configType = ConfigType.LOCAL; // 设置类型
    private Color tempLineColor=Color.BLACK; // 当前颜色
    public StyleConfig(Frame owner) {
        super(owner);
        initComponents();
    }

    public StyleConfig(Dialog owner) {
        super(owner);
        initComponents();
    }

    /**
     * 默认设置
     */
    private void setLastConfig() {
        Shape shape = graphics.getCurrentShape();
        if (shape == null){
            lineColorButton.setBackground(Color.BLACK);
        }
        else{
            tempLineColor = shape.getColor();
            lineColorButton.setBackground(shape.getColor());
        }

    }

    /**
     * 样式设置
     * @param graphics 画板
     */
    public StyleConfig(Graphics graphics) {
        this.graphics = graphics;
        initComponents();
        setLastConfig();
    }

    public StyleConfig(Graphics graphics, ConfigType configType) {
        this.graphics = graphics;
        this.configType = configType;
        initComponents();
        setLastConfig();
    }

    /**
     * 事件响应 线条颜色
     * @param e 事件
     */
    private void lineColorButtonActionPerformed(ActionEvent e) {
        Color color = JColorChooser.showDialog(this, "Line Color ", Color.BLACK);
        lineColorButton.setBackground(color);
        tempLineColor = color;
    }

    /**
     * 事件响应 保存设置
     * @param e 事件
     */
    private void okButtonActionPerformed(ActionEvent e) {
        if(configType == ConfigType.GLOBAL){
            String path = "src/resource/setting.txt"; // 保存设置的路径
            List<String> info  = Save.readTxt(path);
            String str = tempLineColor.getRGB() + "\n" + transToFloat(strokeStyleBox) + "\n" + info.get(2);
            Save.writeTxt(path, str);
        }
        float stroke = transToFloat(strokeStyleBox);
        graphics.setColor(tempLineColor);
        graphics.setStroke(stroke);
        List<Shape> shapes = graphics.getSelectedShapes();
        if(shapes.isEmpty()){
            dispose();
            return;
        }
        List<Command> commands = new ArrayList<>();
        for(Shape shape : shapes){
            shape.setColor(tempLineColor);
            shape.setStroke(stroke);
            graphics.reshape(shape, shape);
            commands.add(new Command(ActionType.MOTIFY, shape));
            graphics.getUpdatedShapes().add(shape);
        }
        graphics.repaint();
        Affair affair = new Affair(graphics.getAffairIndex(), commands);
        graphics.addAffair(affair); // 添加
        graphics.outToServer();

        dispose();
    }

    /**
     * 事件响应 取消操作
     * @param e 事件
     */
    private void cancelButtonActionPerformed(ActionEvent e) {
        // do nothing
        dispose();
    }
    /**
     * 事件响应 初始化
     */
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        lineColorLabel = new JLabel();
        lineColorButton = new JButton();
        strokeLabel = new JLabel();
        strokeStyleBox = new JComboBox<>();
        zoomLabel = new JLabel();
        zoomBox = new JComboBox<>();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridLayout(2, 2, 0, 5));

                //---- lineColorLabel ----
                lineColorLabel.setText("Line Color ");
                lineColorLabel.setFont(new Font("Corbel", lineColorLabel.getFont().getStyle() | Font.BOLD, lineColorLabel.getFont().getSize() + 3));
                lineColorLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                lineColorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                contentPanel.add(lineColorLabel);

                //---- lineColorButton ----
                lineColorButton.setFont(new Font("Corbel", lineColorButton.getFont().getStyle() | Font.BOLD, lineColorButton.getFont().getSize() + 3));
                lineColorButton.setBackground(Color.white);
                lineColorButton.setBorderPainted(false);
                lineColorButton.addActionListener(e -> lineColorButtonActionPerformed(e));
                contentPanel.add(lineColorButton);

                //---- strokeLabel ----
                strokeLabel.setText("Line Width");
                strokeLabel.setFont(new Font("Corbel", strokeLabel.getFont().getStyle() | Font.BOLD, strokeLabel.getFont().getSize() + 3));
                strokeLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                strokeLabel.setAlignmentX(0.5F);
                strokeLabel.setHorizontalAlignment(SwingConstants.CENTER);
                contentPanel.add(strokeLabel);

                //---- strokeStyleBox ----
                strokeStyleBox.setModel(new DefaultComboBoxModel<>(new String[] {
                    "0.5  px",
                    "1.0  px",
                    "1.5  px",
                    "2.0  px",
                    "2.5  px",
                    "3.0  px",
                    "3.5  px",
                    "5.0  px",
                    "7.0  px",
                    "10  px",
                    "15  px",
                    "20  px"
                }));
                strokeStyleBox.setBorder(null);
                strokeStyleBox.setBackground(Color.white);
                strokeStyleBox.setFont(new Font("Corbel", strokeStyleBox.getFont().getStyle() & ~Font.BOLD, strokeStyleBox.getFont().getSize() + 3));
                strokeStyleBox.setForeground(Color.black);
                strokeStyleBox.setSelectedIndex(1);
                contentPanel.add(strokeStyleBox);

                //---- zoomLabel ----
                zoomLabel.setText("Zoom ");
                zoomLabel.setFont(new Font("Corbel", zoomLabel.getFont().getStyle() | Font.BOLD, zoomLabel.getFont().getSize() + 3));
                zoomLabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                zoomLabel.setHorizontalAlignment(SwingConstants.CENTER);
                // contentPanel.add(zoomLabel);

                //---- zoomBox ----
                zoomBox.setModel(new DefaultComboBoxModel<>(new String[] {
                    "10%",
                    "30%",
                    "50%",
                    "70%",
                    "100%",
                    "120%",
                    "150%",
                    "200%",
                    "250%",
                    "300%"
                }));
                zoomBox.setBorder(null);
                zoomBox.setBackground(Color.white);
                zoomBox.setFont(new Font("Corbel", zoomBox.getFont().getStyle() & ~Font.BOLD, zoomBox.getFont().getSize() + 3));
                zoomBox.setForeground(Color.black);
                zoomBox.setSelectedIndex(4);
                // contentPanel.add(zoomBox);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                okButton.setFont(new Font("Corbel", okButton.getFont().getStyle() | Font.BOLD, okButton.getFont().getSize() + 3));
                okButton.setVerticalAlignment(SwingConstants.BOTTOM);
                okButton.setBorderPainted(false);
                okButton.setBackground(new Color(204, 204, 204));
                okButton.setForeground(new Color(51, 51, 51));
                okButton.addActionListener(e -> okButtonActionPerformed(e));
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("CANCEL");
                cancelButton.setFont(new Font("Corbel", cancelButton.getFont().getStyle(), cancelButton.getFont().getSize() + 3));
                cancelButton.setVerticalAlignment(SwingConstants.BOTTOM);
                cancelButton.setBorderPainted(false);
                cancelButton.setBackground(Color.white);
                cancelButton.setForeground(new Color(51, 51, 51));
                cancelButton.addActionListener(e -> cancelButtonActionPerformed(e));
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private float transToFloat(Object object) {
        String temp = ((JComboBox) object).getSelectedItem().toString();
        Pattern p = Pattern.compile("[a-zA-Z%\\s]");
        Matcher m = p.matcher(temp);
        return Float.parseFloat(m.replaceAll(""));
    }


}
