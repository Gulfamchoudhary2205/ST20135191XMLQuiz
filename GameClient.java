package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
    private String serverAddress;
    private int port;
    private PrintWriter out;

    private static boolean keepRunning = true;

    public static void main(String[] args) throws IOException {
        //TODO args
        String host = "127.0.0.1";
        int port = 2000;
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }
        GameClient client = new GameClient(host, port);
        client.run();

    }

    public GameClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    private void run() throws IOException {
        Thread thread = new Thread(() -> {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromUser;

            while (keepRunning) {
                try {
                    fromUser = stdIn.readLine();
                    if (fromUser != null) {
                        out.println(fromUser);
                    }
                } catch (Exception ignored) {

                }
            }
        });

        thread.start();

        Socket socket = new Socket(serverAddress, port); //TODO refused
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        while (keepRunning) {
            String line = in.readLine();
            if (line == null) {
                keepRunning = false;
            } else {
                System.out.println(line);
            }
        }
    }
}
