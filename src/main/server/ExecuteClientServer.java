package main.server;

import main.shape.Shape;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;


public class ExecuteClientServer implements Runnable{
    private Socket client; // 客户端
    private ObjectOutputStream os; // 对象输出流
    private Set<Socket> clientSet; // 客户端集合
    private List<Shape> updatedShapes; // 待更新图形列表
    private static List<ObjectOutputStream> oss = new ArrayList<>();
    private static Set<Shape> shapeSet = new HashSet<>(); // 所有的图形对象

    /**
     * 处理数据
     * @param client 客户端
     * @param clientSet 客户端集合
     * @throws IOException io异常
     */
    public ExecuteClientServer(Socket client, Set<Socket> clientSet) throws IOException {
        super();
        this.client = client;
        this.clientSet = clientSet;

        updatedShapes = new ArrayList<>(); // 初始化待更新的对象
        System.out.println("add client");
        os = new ObjectOutputStream(client.getOutputStream()); // 初始化对象输出流
        oss.add(os);
        clientSet.add(client);

        // init to update the user joined in
        os.writeObject(List.copyOf(shapeSet));
        os.flush();
        os.reset();
    }

    /**
     * 更新存储的图形列表
     * 首先判断传入的图形状态是否已经被删除, 如果标记为已删除, 则将其从列表中移除
     * 判断现有集合中是否已经存在新传入的图形, 如果不存在, 将其加入集合
     * 如果存在(属性完全一致), 表示没有变动, 不与更新
     * 图形属性是否一致通过equals方法判断, 图形是否为同一个对象由唯一表示符uid判断
     * @param shapes 新读取的图形列表
     */
    private void updateShapes(List<Shape> shapes){
//        System.out.println("receive: " + shapes.toString());
        // 判断是否被删除
        for(Shape shape:shapes){
            if(!shape.isActive()){
                shapeSet.remove(shape);
                updatedShapes.add(shape);
                continue;
            }
            // 如果集合中没有和此图形属性一致的图形(位置等不同或者不是同一个对象)
            if(!shapeSet.contains(shape)){
                Iterator<Shape> it = shapeSet.iterator();
                boolean flag = false;
                Shape tmp = null;
                while (it.hasNext()){
                    tmp = it.next();
                    if(tmp.getUuid().equals(shape.getUuid())){ // 表示这两个图形是同一个对象, 属性不同
                        it.remove(); // 将其删除, 再添加新传入的图形, 表示更新
                        shapeSet.add(shape);
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    shapeSet.add(shape);
                }
                updatedShapes.add(shape);
            }
        }
    }


    @Override
    public void run() {
        ObjectInputStream is = null;
        Object object = null;
        boolean active = true;
        try {
            is = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
            while (active) {
                object = is.readObject();
                if(object != null){
                    if(object.getClass() == String.class){ // 如果接受的对象为字符串类型, 表示收到了退出通知
                        System.out.println("client exit");
                        os.writeObject("exit");
                        oss.remove(os); // 将其移除
                        clientSet.remove(client);
                        client.close(); // 关闭客户端
                        active = false;
                        continue;
                    }
                    List<Shape> shapes = (List<Shape>) object; // 读取从客户端发送的对象数据
                    updateShapes(shapes); // 进行对象更新

                    // 向加入此服务器的每一个客户端发送已经更新的数据
                    for(ObjectOutputStream ops : oss){
                        ops.writeObject(updatedShapes);
                        ops.flush();
                        ops.reset();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                        System.out.println(df.format(new Date()));
                    }
                    updatedShapes.clear();
                }
            }
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}


