package org.example.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public static final int TCP_PORT = 9000;
    public static final String hostname = "localhost";
    public static volatile boolean running = true;
    public static void main(String[] args) {

        System.out.print("Enter username: ");

        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            // otvori socket prema drugom racunaru
            socket = new Socket(hostname, TCP_PORT);

            // inicijalizuj ulazni stream
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            // inicijalizuj izlazni stream
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            out.println(username);

            String res = in.readLine();

            if (res.equals("existing user")){
                throw new IllegalArgumentException("Username already logged in..");
            }
            if (res.equals("bad word")){
                throw new IllegalArgumentException("Username contains bad word.");
            }
            System.out.println(res);

            // todo dohvati istoriju poruka
            // in.readline();




            Thread outputWriterThread = new Thread(new ClientWriter(scanner,out));
            Thread inputReaderThread = new Thread(new ClientInputReader(in));

            outputWriterThread.start();
            inputReaderThread.start();

            outputWriterThread.join();
            inputReaderThread.join();

            // zatvori konekciju
            in.close();
            out.close();
            socket.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
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
