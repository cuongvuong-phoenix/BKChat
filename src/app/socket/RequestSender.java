package app.socket;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.io.*;

public class RequestSender implements Runnable {
    private DataOutputStream os;
    private Client client;
    private Queue<String> requestQueue = new LinkedList<>();

    public RequestSender(Client client, DataOutputStream writer) {
        this.os = writer;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (this) {
                    while (!requestQueue.isEmpty()) {
                        String request = requestQueue.remove();
                        System.out.println(request);
                        os.writeUTF(request);
                        os.flush();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        synchronized (this) {
            requestQueue.add(msg);
        }
    }
}
