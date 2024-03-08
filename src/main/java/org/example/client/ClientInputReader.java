package org.example.client;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientInputReader implements Runnable{

    private BufferedReader in;

    public ClientInputReader(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        while (Client.running){
            try {
                String response = in.readLine();
                if(response.equals("")){
                    break;
                }
                System.out.println(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
