package main.client;

import main.app.App;
import main.util.StyleConfig;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class AppClient1 {

    private Socket server; // 服务器
    public AppClient1(String serverName, int port){
        StyleConfig.initGlobalSetting();
        try {
            server = new Socket(serverName, port);
            App app = new App(server);
            app.setTitle("White board 2");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造函数
     * 选择是否加入服务器, 如果是, 输入服务器ip和端口
     * 否则不加入服务器启动, 并询问是否打开保存的文件
     */
    public AppClient1(){
        StyleConfig.initGlobalSetting();
        App app = null;
        int i = JOptionPane.showConfirmDialog(null, "whether to join a server?");
        if(i == JOptionPane.NO_OPTION){
            app = new App(null);
            app.setVisible(true);
        }else{
            try {
                String s = JOptionPane.showInputDialog(null, "请输入服务器(eg: 127.0.0.1:43322)");
                String[] config = s.split(":");
                server = new Socket(config[0], Integer.parseInt(config[1]));
                app = new App(server);
                app.setVisible(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "请确认服务器已运行");
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "请确保输入格式正确");
            }
        }
        app.setTitle("White board 1");

    }


    public static void main(String[] args) {
        // AppClient client = new AppClient("127.0.0.1", 43211);
        AppClient1 client = new AppClient1();
    }
}
