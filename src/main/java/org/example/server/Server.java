package org.example.server;

import org.example.server.mesenger.ServerMessenger;
import org.example.server.mesenger.UserMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Server {


    public static final int TCP_PORT = 9000;
    public static volatile Map<String, PrintWriter> activeUsers = new ConcurrentHashMap<>();
    public static List<String> history = new CopyOnWriteArrayList<>();
    public static List<String> badWords = new ArrayList<>();

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static BlockingQueue<UserMessage> messagesToBeSent = new LinkedBlockingQueue<>();

    public static void main(String[] args) {

        badWords.add("damn");
        badWords.add("peepee");
        badWords.add("poopoo");

        // todo probati ovako ali je ono okej
        Thread msgSender = new Thread(new ServerMessenger());
        msgSender.setDaemon(true);
        msgSender.start();

        try {
            ServerSocket ss = new ServerSocket(TCP_PORT);

            System.out.println("Server running...");
            while (true) {
                Socket socket = ss.accept();
                new ServerThread(socket);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
