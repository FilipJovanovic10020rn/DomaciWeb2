package org.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {


    public static final int TCP_PORT = 9000;
    // username, njegov printWriter
    // mozda konkurent map
    public static volatile Map<String, PrintWriter> activeUsers = new ConcurrentHashMap<>();

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static List<String> history = new CopyOnWriteArrayList<>();


    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(TCP_PORT);

            System.out.println("Server running...");
            while (true) {
                Socket socket = ss.accept();

                // thread koji sluzi za komunikaciju sa konkretnim korisnikom i ima pristup mapi korisnika
                new ServerThread(socket);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
