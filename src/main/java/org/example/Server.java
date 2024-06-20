package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {


    ServerSocket serverSocket;

    public void start(int port){
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Server port: "+ port);
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("New user has connected");
                UserHandler userHandler = new UserHandler(socket);
                new Thread(userHandler).start();
            }

        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void CloseServerSocket(){
        try{
            if (serverSocket != null){
            serverSocket.close();
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {

        Server server = new Server();
        server.start(4999);


    }
}
