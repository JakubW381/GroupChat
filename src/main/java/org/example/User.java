package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class User {

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    String username;

    public User(Socket socket, String username){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }
        catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String msgToSend = scanner.nextLine();
                bufferedWriter.write(username+": "+msgToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()){
                    try{
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    }catch (IOException e){
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
    }


    public void closeEverything(Socket socket, BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        try{
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username");
        String username = scanner.nextLine();
        Socket socket1 = new Socket("localhost",4999);
        User user1 = new User(socket1,username);
        user1.listenForMessage();
        user1.sendMessage();
    }
}
