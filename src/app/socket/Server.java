package app.socket;

import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {
    private ArrayList<ClientHandler> clientList = new ArrayList<>();
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public List<ClientHandler> getClientList() {
        return clientList;
    }

    public void addUser(ClientHandler current) {
        clientList.add(current);
    }

    public void removeUser(ClientHandler current) {
        clientList.remove(clientList.indexOf(current));
    }

    public void start() {
        ServerSocket server;
        Executor executor = Executors.newFixedThreadPool(30);
        try {
            server = new ServerSocket(port);
            while (true) {
                try {
                    Socket client = server.accept();
                    executor.execute(new ClientHandler(this, client));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
