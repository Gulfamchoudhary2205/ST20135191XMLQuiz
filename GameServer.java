package server;

import server.data.Parser;
import server.data.Question;
import server.data.QuestionBank;
import server.data.XmlParser;
import server.exception.QuizException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * GameServer class with complete quiz logic
 */
public class GameServer {
    //10 seconds to set the timeout on socket in ms
    private static final int TIMEOUT = 10 * 1000;
    //list of the players
    private static List<PlayerHandler> players = new ArrayList<>();
    //synchronization point for the game start
    private static CountDownLatch gameStartCDL;
    //synchronization point for the game end
    private static CountDownLatch gameOverCDL;
    //question bank to be used in the game
    private QuestionBank questionBank;
    //port to open for socket
    private int port;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Port and path to the questionaure file are required");
            System.out.println("e.g. java -jar GameServer.jar 2000 questionaire.xml");
            System.exit(1);
        }
        GameServer gameServer = new GameServer(Integer.parseInt(args[0]), args[1]);
        gameServer.start();
    }

    /**
     * Constructors that sets the port and calls the setup with path to the xml file
     *
     * @param port to open for the socket
     * @param path to the xml file
     */
    public GameServer(int port, String path) {
        this.port = port;
        setup(path);
    }

    /**
     * Sets the questionBank by calling the xml parser with the file path given
     *
     * @param path of the xml file
     */
    private void setup(String path) {
        //instatiate the parser
        Parser parser = new XmlParser();
        try {
            //set the questionBank
            questionBank = parser.parseQuestionaire(path);
        } catch (QuizException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Starts the server and handles the game rules
     */
    private void start() {
        //create the socket on the given port
        try (ServerSocket listener = new ServerSocket(port)) {
            //load the number of the players
            int playersNum = questionBank.getPlayers();
            System.out.println("The game server is running on port " + port + " for " + playersNum + " players");
            //define the gameStartCDL to wait for the given number of the players to join
            gameStartCDL = new CountDownLatch(playersNum);
            //for the number of players
            for (int i = 0; i < playersNum; i++) {
                //create the same number of Player threads
                new PlayerHandler(listener.accept()).start();
            }
            //define the gameOverCDL to wait for the given number of the players to finish the game
            gameOverCDL = new CountDownLatch(playersNum);
            //start waiting for game over
            gameOverCDL.await();

            //scores message to be sent after the end of the game
            StringBuilder scores = new StringBuilder();
            //for each player add the player's info
            for (PlayerHandler p : players) {
                scores.append(p.name).append(" has scored ").append(p.score).append(" in ").append(p.answeringTime).append("ms\n");
            }
            //sort the players
            Collections.sort(players);
            //for each player send the messages to his output and close the stream
            for (PlayerHandler p : players) {
                p.out.println("Game over! Scores:");
                p.out.println(scores);
                p.out.println("The winner is " + players.get(0));
                p.socket.close();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * PlayerHandler class that holds the player's details. Extends the Tread to support thread-per-player.
     * Implements the Comparable to support the sorting of the players by the score.
     */
    private class PlayerHandler extends Thread implements Comparable<PlayerHandler> {
        //host the player comes from
        private String host;
        //socket the player is connected to
        private Socket socket;
        //player's output to write
        private PrintWriter out;
        //name of the player
        private String name = "";
        //last name of the player
        private String lastName = "";
        //age of the player
        private int age = 0;
        //score of the player
        private int score = 0;
        //total answering time of the player
        private long answeringTime = 0;

        /**
         * Constructor that sets the socket
         *
         * @param socket endpoint to the player
         */
        PlayerHandler(Socket socket) {
            this.socket = socket;
            this.host = socket.getInetAddress().getHostName();
        }

        @Override
        public void run() {
            try {
                //add the player
                players.add(this);
                //open the input stream of the socket
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //open the output stream of the socket
                out = new PrintWriter(socket.getOutputStream(), true);

                //ask the player for the name and the age and set them
                out.println("Welcome to the game!");
                //keep asking until the name is set correctly
                while (name.isEmpty()) {
                    out.println("Please enter your name: ");
                    String tmp = in.readLine();
                    //if the entered string contains only letters
                    if (tmp.chars().allMatch(Character::isLetter)) {
                        //set it as a name
                        name = tmp;
                    } else {
                        //otherwise return the message to the player
                        out.println("Invalid name!");
                    }
                }
                //keep asking until the last name is set correctly
                while (lastName.isEmpty()) {
                    out.println("Please enter your last name: ");
                    String tmp = in.readLine();
                    //if the entered string contains only letters
                    if (tmp.chars().allMatch(Character::isLetter)) {
                        //set it as a last name
                        lastName = tmp;
                    } else {
                        //otherwise return the message to the player
                        out.println("Invalid last name!");
                    }
                }
                while (age < 1) {
                    out.println("Please enter your age: ");
                    try {
                        age = Integer.parseInt(in.readLine());
                    } catch (NumberFormatException nfe) {
                        out.println("Incorrect input!");
                    }
                }
                out.println("Welcome " + name + " " + lastName + "! The game will start once all the players are registered...");

                System.out.println(this + " has joined");
                //countDown for this player
                gameStartCDL.countDown();
                //wait for the remaining players to join
                gameStartCDL.await();

                //set the timeout for the answer
                socket.setSoTimeout(TIMEOUT);

                //for each question in the bank
                for (Question question : questionBank.getQuestions()) {
                    //send the formatted question
                    out.println(question.getFormattedQuestion());
                    //load the time when the question is sent
                    long startTime = System.currentTimeMillis();
                    try {
                        //given answer
                        int selected = Integer.parseInt(in.readLine());
                        //tell the player if the answer is correct or not and increase the score if given answer is correct
                        if (question.getAnswerId() == selected) {
                            score++;
                            out.println("Correct! Your score is " + score);
                        } else {
                            out.println("Wrong! Your score is " + score);
                        }
                    } catch (NumberFormatException nfe) {
                        out.println("The answer is wrong!");
                    } catch (SocketTimeoutException ste) {
                        out.println("Time is up!");
                    } finally {
                        //increase the answeringTime
                        answeringTime += System.currentTimeMillis() - startTime;
                    }
                }
                //send out the final score
                out.println("Your final score is " + score + " achieved in " + answeringTime + "ms");

                //decrement the count for gameOverCDL
                gameOverCDL.countDown();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return name + " " + lastName + " (" + age + ") from " + host;
        }

        @Override
        public int compareTo(PlayerHandler other) {
            //if two players have the same score
            if (this.score == other.score) {
                //the faster one is better
                return Long.compare(this.answeringTime, other.answeringTime);
            }
            //bigger score is better
            return Integer.compare(other.score, this.score);
        }
    }
}
