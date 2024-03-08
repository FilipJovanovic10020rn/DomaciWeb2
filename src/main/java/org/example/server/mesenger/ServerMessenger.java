package org.example.server.mesenger;

import org.example.server.Server;

public class ServerMessenger implements Runnable{
    @Override
    public void run() {
        while (true){
            try {
                UserMessage userMessage = Server.messagesToBeSent.take();

                addToHistory(userMessage.getMessage());

                send(userMessage.getUsername(),userMessage.getMessage());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void addToHistory(String msg){
        if(Server.history.size() == 100){
            for(int i =0; i < Server.history.size();i++){
                Server.history.set(i,Server.history.get(i+1));
            }
            Server.history.remove(Server.history.size() - 1);
        }
        Server.history.add(msg);
    }

    private void send(String user,String msg){
        System.out.println(msg);
        for(String username: Server.activeUsers.keySet()){
            if(username!=user){
                Server.activeUsers.get(username).println(msg);
            }
        }
    }
}
