package main.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static final  int MAX_COUNT = 30; // 最大链接数目
    private ServerSocket serverSocket; // 套接字
    private Set<Socket> socketSet;
    public Server(){
        socketSet = new HashSet<>();
    }

    public void run() throws IOException {
        serverSocket = new ServerSocket(43322); // 默认端口号为43322
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_COUNT);
        System.out.println("Wait users to connect");
        for(int i = 0; i < MAX_COUNT; i++){
            // waite client to join
            Socket socket = serverSocket.accept();
            System.out.println("New user joined: " + socket.getInetAddress() + socket.getPort());
            executorService.execute(new ExecuteClientServer(socket, socketSet)); // process data
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }
}
