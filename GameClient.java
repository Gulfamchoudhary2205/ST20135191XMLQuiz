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
    //flag to keep the client running
    private static boolean keepRunning = true;

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Host and port are required arguments");
            System.out.println("e.g. java -jar GameClient.jar 127.0.0.1 2000");
            System.exit(1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        GameClient client = new GameClient(host, port);
        client.run();

    }

    /**
     * Constructor that sets host and port to connect to
     * @param serverAddress address of the server
     * @param port port to connect
     */
    public GameClient(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
    }

    /**
     * Method that holds the logic of the GameClient
     * @throws IOException
     */
    private void run() throws IOException {
        //Thread that reads the user's input from the console and sends it trough the socket
        new Thread(() -> {
            //Reader of the system input
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            //text that user typed in
            String fromUser;
            while (keepRunning) {
                try {
                    //read the line of the input
                    fromUser = stdIn.readLine();
                    //if it's not null
                    if (fromUser != null) {
                        //send it to the server
                        out.println(fromUser);
                    }
                } catch (Exception ignored) {

                }
            }
        }).start();

        //create socket on the given host/port
        Socket socket = new Socket(serverAddress, port); //TODO refused
        //start the input reader
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //initialize the socket output
        out = new PrintWriter(socket.getOutputStream(), true);

        while (keepRunning) {
            //text received from the server
            String line = in.readLine();
            //null line means that the socket is closed so the app should stop
            if (line == null) {
                keepRunning = false;
            } else {
                System.out.println(line);
            }
        }
    }
}
