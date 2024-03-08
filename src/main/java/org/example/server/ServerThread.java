package org.example.server;

import org.example.server.Server;
import org.example.server.mesenger.UserMessage;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class ServerThread extends Thread{

    private Socket socket;
    private String username;
    private BufferedReader in;
    private PrintWriter out;

    public ServerThread(Socket socket){
        this.socket = socket;

        try {
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream()), true);

            // pokreni thread
            start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void sendMessage(String time, String msg){
        String split[] = msg.split(" ");

        String censoredMsg = "";
        for(String s : split){
            if(Server.badWords.contains(s)){
                String middlePart = s.substring(1, s.length() - 1).replaceAll(".", "*");
                String censoredWord = s.charAt(0) + middlePart + s.charAt(s.length() - 1);
                censoredMsg += censoredWord + " ";
            }
            else{
                censoredMsg += s + " ";
            }
        }
        String formattedMsg = "["+time +"] - " + username + ": " + censoredMsg;

//        addToHistory(formattedMsg);
//        send(formattedMsg);

        // ako crkne ovo zakomentarisati
        Server.messagesToBeSent.add(new UserMessage(username, formattedMsg));
    }

    // prebacio sam u serverMsg thread koji sluzi za slanje poruka svima zbog redosleda
    private void addToHistory(String msg){
        if(Server.history.size() == 100){
            for(int i =0; i < Server.history.size();i++){
                Server.history.set(i,Server.history.get(i+1));
            }
            Server.history.remove(Server.history.size() - 1);
        }
        Server.history.add(msg);
    }

    private void send(String msg){
        System.out.println(msg);
        for(String username: Server.activeUsers.keySet()){
            if(username!=this.username){
                Server.activeUsers.get(username).println(msg);
            }
        }
    }

    public void run() {
        try {
            username = in.readLine();

            // username je ruzna rec
            if(Server.badWords.contains(username)){
                out.println("bad word");
                in.close();
                out.close();
                socket.close();
                return;
            }
            // korisnik vec postoji (vraca null ako je u tom trenutku ubacen u mapu)
            if(Server.activeUsers.putIfAbsent(username,out) != null){
                out.println("existing user");
                in.close();
                out.close();
                socket.close();
                return;
            }
            out.println("Successfully connected");


            String formatDateTime = LocalDateTime.now().format(Server.formatter);
            send("["+formatDateTime +"] "+ username + " has joined the chat");

            // osigurano jer je CopyOnWriteArrayList
            String history = "";
            for(String s : Server.history){
                history += s + "\n";
            }
            if(history != ""){
                out.println(history);
            }

            while (true) {
                String request = in.readLine();

                formatDateTime = LocalDateTime.now().format(Server.formatter);

                if(request.equals("/disconnect")){
                    Server.activeUsers.remove(username);
                    send("["+formatDateTime +"] " + username + " has disconnected");
                    out.println("");
                    break;
                }

                sendMessage(formatDateTime,request);
            }

            // zatvori konekciju
            in.close();
            out.close();
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            close(socket,in,out);
        }
    }

    private void close(Socket socket, BufferedReader in, PrintWriter out){
        Server.activeUsers.remove(username);
        if (in != null) {
            try {
                in.close();
            } catch (Exception e) {

            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {

            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {

            }
        }
    }

}

