package app.socket;

public class ServerMaster {
    public static void main(String args[]) {
        int port = 9998;
        Server server = new Server(port);
        server.start();
    }
}
