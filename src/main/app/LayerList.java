package main.app;

import main.shape.Layer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
public class LayerList extends JList {
    private main.shape.Graphics g;
    private Layer currentLayer;
    private List<Integer> visibleList;
    private JPopupMenu createMenu;
    private JPopupMenu deleteMenu;
    private JMenuItem createItem; // create菜单项
    private JMenuItem deleteItem; // delete菜单项
    private List<Layer> layerList;
    public void setGraphicsLayer(){
        g.setLayerId(currentLayer.getId());
        g.repaint();
    }
    public void addLayer(ActionEvent e){
        String name = JOptionPane.showInputDialog(null, "input the layer name");
        if (name.equals("")) return;
        currentLayer = new Layer(name);
        layerList.add(currentLayer);
        visibleList.add(currentLayer.getId());
        updateView();
    }

    public LayerList(main.shape.Graphics g) {
        this.g = g;
        layerList = new ArrayList<>();
        visibleList = new ArrayList<>();
        createMenu = new JPopupMenu();
        deleteMenu = new JPopupMenu();
        createItem = new JMenuItem("create");
        createItem.setBorderPainted(false);
        createItem.addActionListener(this::addLayer);
        deleteItem = new JMenuItem("delete");
        deleteItem.setBorderPainted(false);
        deleteItem.addActionListener(this::deleteLayer);

        createMenu.add(createItem);
        deleteMenu.add(deleteItem);
        currentLayer = new Layer("默认图层");
        layerList.add(currentLayer);
        visibleList.add(currentLayer.getId());
        setCellRenderer(new LayerListCellRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addMouseListener(new LayerClick());
        updateView();
    }

    @Override
    public int locationToIndex(Point location) {
        int index = super.locationToIndex(location);
        if (index != -1 && !getCellBounds(index, index).contains(location)) {
            return -1;
        } else {
            return index;
        }
    }
    /**
     * 更新视图
     */
    public void updateView(){
        DefaultListModel<Layer> defaultListModel  = new DefaultListModel<>();

        for(Layer s : layerList){
            defaultListModel.addElement(s);
        }
        setModel(defaultListModel);
    }

    public void deleteLayer(ActionEvent e){
        Layer item = (Layer) getSelectedValue();
        layerList.remove(item);
        visibleList.remove(item);
        if (currentLayer.equals(item)){
            currentLayer = layerList.get(layerList.size()-1);
        }
        System.out.println("delete item :" + item);
        updateView();
    }


    /**
     * 文件点击事件类
     */
    class LayerClick extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JList<?> list = (JList<?>) e.getSource();
            if (list.locationToIndex(e.getPoint()) == -1 && !e.isShiftDown()){
                list.clearSelection();
            }
        }
        @Override
        public void mousePressed(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON3){ // 如果是右键
                if(getSelectedValuesList().isEmpty()){ // 在空白区域点击
                    createMenu.show(e.getComponent(), e.getX(), e.getY());
                }else{ // 选中文件点击
                    deleteMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }else if(e.getButton() == MouseEvent.BUTTON1 && !getSelectedValuesList().isEmpty()) { // 左键单击选中给定的文件
                Layer layer = (Layer) getSelectedValue();
                if(e.getClickCount() == 1){
                    currentLayer = layer;
                    setGraphicsLayer();
                } else if (e.getClickCount() == 2) { // 双击进行显示或者不显示切换
                    for (Integer i : visibleList) System.out.print(i + " ");
                    System.out.println();
                    if (!visibleList.contains(layer.getId())){
                        visibleList.add(layer.getId());
                        System.out.println("add " + layer.getId());
                        layer.setVisible(true);
                    }else{
                        System.out.println("remove  "+ layer.getId());
                        visibleList.remove(Integer.valueOf(layer.getId()));
                        layer.setVisible(false);
                    }
                }
            }
            g.setVisibleLayerList(visibleList);
            g.repaint();
            repaint();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Layer");
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        final JScrollPane jsp = new JScrollPane(new LayerList(null));
        jsp.setBorder(new EmptyBorder(0, 0, 0, 0));
        frame.add(jsp, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
