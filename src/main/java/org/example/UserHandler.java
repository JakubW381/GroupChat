package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class UserHandler implements Runnable{

    public static ArrayList<UserHandler> userHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public UserHandler(Socket socket){
        try{
            this.socket = socket;
             bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = bufferedReader.readLine();
            userHandlers.add(this);
            broadcastMessage("SERVER: "+username+" has entered the chat");
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    @Override
    public void run() {
        String userMessage;
        while(socket.isConnected()){
            try{
                userMessage = bufferedReader.readLine();
                broadcastMessage(userMessage);
            }catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String msgToSend){
        for (UserHandler userHandler: userHandlers){
            try{
                if (!userHandler.username.equals(username)){
                    userHandler.bufferedWriter.write(msgToSend);
                    userHandler.bufferedWriter.newLine();
                    userHandler.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
            }
        }
    }

    public  void userDisconnect(){
        userHandlers.remove(this);
        broadcastMessage("SERVER: "+username+" has left the chat");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        userDisconnect();
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


}
