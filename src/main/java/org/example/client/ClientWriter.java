package org.example.client;

import java.io.PrintWriter;
import java.util.Scanner;

public class ClientWriter implements Runnable{

    private PrintWriter out;
    private Scanner scanner;

    public ClientWriter(Scanner scanner, PrintWriter out) {
        this.out = out;
        this.scanner = scanner;
    }

    @Override
    public void run() {
        while (Client.running){
            String msg = scanner.nextLine();
            out.println(msg);

            if(msg.equals("/disconnect")){
                Client.running = false;
            }
        }
    }
}
