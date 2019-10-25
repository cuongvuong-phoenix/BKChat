package app.socket;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Scanner;

public class MessageSender implements Runnable {

    private PrintWriter writer;
    private PeerHandler peer;

    public MessageSender(PeerHandler peerHandler, PrintWriter writer) {
        this.peer = peerHandler;
        this.writer = writer;
    }

    @Override
    public void run() {
        try {
            Scanner scanIn = new Scanner(System.in);
            while (true) {
                System.out.println("Me:");
                String request = scanIn.nextLine();
                writer.write(request);
                writer.write("\n");
                writer.flush();
            }
        } catch (Exception e) {

        }

    }

    public void send(String content) {
        writer.write("Message " + content);
        writer.write("\n");
        writer.flush();
    }
}
