package org.example.server;

import org.example.server.Server;

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
            // inicijalizuj ulazni stream
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // inicijalizuj izlazni stream
            out = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream()), true);
            // pokreni thread
            start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void sendMessage(String msg){
        // todo provera ruznih reci i dodati timestamp i to i dodati u istoriju
        for(String username: Server.activeUsers.keySet()){
            if(username!=this.username){
                Server.activeUsers.get(username).println(msg);
            }
        }
    }

    public void run() {
        String response = "";
        try {
            // procitaj zahtev
            username = in.readLine();

            // todo dodati usera u listu a ako vec postoji vratiti user exists i proveriti da li ima ruzne reci username

            String formatDateTime = LocalDateTime.now().format(Server.formatter);

            // todo ovo poslati svima
            System.out.println("["+formatDateTime +"]: User " + username + " has joined");

            out.println("Successfully connected");

            Server.activeUsers.put(username,out);

            sendMessage("["+formatDateTime +"] "+ username + " has joined the chat");

            // todo poslati celu istoriju poruka korisniku
            // out.println(history.toString());

            while (true) {
                String request = in.readLine();

                formatDateTime = LocalDateTime.now().format(Server.formatter);


                if(request.equals("/disconnect")){
                    // send svima da je diskonektovan
                    // todo izbaciti ga iz liste
                    System.out.println("["+formatDateTime +"]: User " + username + " has disconnected");
                    out.println("");
                    break;
                }

                System.out.println("["+formatDateTime +"] - " + username + ": " + request);
                request = "["+formatDateTime +"] - " + username + ": " + request;

                // dodaj u listu
                sendMessage(request);

                // posalji odgovor
//                out.println(request);


            }

            // zatvori konekciju
            in.close();
            out.close();
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
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

}

