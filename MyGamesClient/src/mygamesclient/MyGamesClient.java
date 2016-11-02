package mygamesclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This is the thread which manages the connection to the server and relays
 * all messages.
 * 
 * @author      Zack Ziegler <kcazyz@gmail.com>
 * @version     0.8                  
 * @since       11/24/2012
 */
public class MyGamesClient extends Thread {
    private MGCMainWindow mainWindow = null;
    private Socket clientSocket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private boolean listening = true;
    
    /**
     * Used to send messages to the server in a standard way.
     */
    private static class Message {
        public String sender;
        public String content;
        
        /**
         * Interprets the plainText from the server into the sender and the
         * content.
         * 
         * @param plainText text being received from the server
         */
        public Message(String plainText) {
            sender = plainText.substring(plainText.indexOf("###sentfrom=")+12, plainText.indexOf("###message="));
            content = plainText.substring(plainText.indexOf("###message=")+11, plainText.length());
        }
        
        /**
         * Returns a string value of the sender and content.
         * 
         * @return String value of the message
         */
        @Override
        public String toString() {
            return "Sender: " + sender + " Message: " + content;
        }
    }
    
    /**
     * Constructor takes the window for which the client runs.
     * 
     * @param mainWindow the GUI window that the client runs out of
     */
    public MyGamesClient(MGCMainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }
    
    /**
     * Connects to a server.
     * 
     * @param IPAddress IP Address of the server
     * @param port port used to connect
     * @param name name of the client (can't be a duplicate)
     * @return 1 if successful, 0 if not
     */
    public int connect(String IPAddress, int port, String name) {
        try {
            this.clientSocket = new Socket(IPAddress, port);
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            sendMessage("server", "###new_connection###name=" + name + "###");
            return 1;
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host");
            return 0;
        } catch (IOException e) {
            System.err.println("Couldn't connect to host");
            return 0;
        }
    }
    
    /**
     * Disconnects client from server.
     * 
     * @return 1 if successful
     */
    public int disconnect() {
        sendMessage("server", "###client_disconnecting###");
        listening = false;
        return 1;
    }
    
    /**
     * Sends a message to the server
     * 
     * @param destination Final destination, either server or another client
     * @param message text of the message
     * @return 1 if successful
     */
    public int sendMessage(String destination, String message) {
        out.println("###sendto=" + destination + "###message=" + message);
        return 1;
    }
    
    /**
     * Runs the thread, listening for messages from the server and translating
     * them into functions for the GUI.
     */
    @Override
    public void run() {
        String fromServer;
        
        try {
            while((fromServer = in.readLine()) != null && listening) {
                MyGamesClient.Message message = new MyGamesClient.Message(fromServer);
                if (message.sender.equals("server")) {
                    if (message.content.equals("###sever_disconnected###")) {
                        mainWindow.notifyServerDisconnect();
                    }
                    else if (message.content.equals("###too_many_connections###")) {
                        mainWindow.disconnect("Too many connections to server");
                    }
                    else if (message.content.equals("###name_already_taken###")) {
                        mainWindow.disconnect("That name is already taken");
                    }
                    else if (message.content.contains("###name_list")) {
                        String[] names = message.content.substring(13, message.content.length()-3).split(",");
                        mainWindow.setUserList(names);
                    }
                    else if (message.content.contains("###newLobbyRecieved###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.indexOf("###host="))); 
                        String host = message.content.substring(message.content.indexOf("host=")+5, message.content.indexOf("###game="));
                        String game = message.content.substring(message.content.indexOf("game=")+5, message.content.length()-3);
                        mainWindow.openNewLobby(id, host, game);
                    }
                    else if (message.content.contains("###joining_lobby_successful###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.indexOf("###host=")));
                        String host = message.content.substring(message.content.indexOf("host=")+5, message.content.indexOf("###game="));
                        String game = message.content.substring(message.content.indexOf("game=")+5, message.content.length()-3);
                        mainWindow.openNewLobby(id, host, game);
                    }
                    else if (message.content.contains("###joining_lobby_failed###")) {
                        String reason = message.content.substring(
                                message.content.indexOf("problem=")+8, message.content.length()-3);
                        mainWindow.joiningLobbyFailed(reason);
                    }
                    else if (message.content.contains("###lobby_info###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.indexOf("###players=")));
                        String[] players = message.content.substring
                                (message.content.indexOf("players=")+8, message.content.length()-3).split(",");
                        mainWindow.updateLobby(id, players);
                    }
                    else if (message.content.contains("###lobby_list")) {
                        String[] lobbies;
                        if (message.content.length() != 16) {
                            lobbies = message.content.substring(14, message.content.length()-3).split(",");
                        } else {
                            lobbies = new String[0];
                        }
                        mainWindow.setLobbyList(lobbies);
                    }
                    else if (message.content.contains("###lobby_host_disconnected###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.length()-3));
                        mainWindow.lobbyHostDisconnected(id);
                    }
                    else if (message.content.contains("###lobby_all_ready###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.length()-3));
                        mainWindow.lobbyAllReady(id, true);
                    }
                    else if (message.content.contains("###lobby_not_all_ready###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.length()-3));
                        mainWindow.lobbyAllReady(id, false);
                    }
                    else if (message.content.contains("###created_new_game###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.indexOf("###game=")));
                        String game = message.content.substring(
                                message.content.indexOf("game=")+5, message.content.indexOf("###partner="));
                        String partner = message.content.substring(
                                message.content.indexOf("partner=")+8, message.content.indexOf("###turn="));
                        boolean turn = Boolean.valueOf(message.content.substring(
                                message.content.indexOf("turn=")+5, message.content.indexOf("###board=")));
                        String board = message.content.substring(
                                message.content.indexOf("board=")+6, message.content.length()-3);
                        mainWindow.openNewGame(id, game, partner, turn, board);
                    }
                    else if (message.content.contains("###move_checked###")) {
                        int id = Integer.parseInt(message.content.substring(
                                message.content.indexOf("id=")+3, message.content.indexOf("###res")));
                        String data = message.content.substring(message.content.indexOf("###res="));
                        mainWindow.reportServerResponse(id, data);
                    }
                    else if (message.content.contains("###inc_game_move###")) {
                        int id = Integer.parseInt(message.content.substring(
                                message.content.indexOf("id=")+3, message.content.indexOf("###new_board")));
                        String newBoard = message.content.substring(
                                message.content.indexOf("new_board=")+10, message.content.length()-3);
                        mainWindow.incGameMove(id, newBoard);
                    }
                    else if (message.content.contains("###you_win###")) {
                        int id = Integer.parseInt(message.content.substring(
                                message.content.indexOf("id=")+3, message.content.length()-3));
                        mainWindow.notifyEnd(id, true);
                    }
                    else if (message.content.contains("###you_lose###")) {
                        int id = Integer.parseInt(message.content.substring(
                                message.content.indexOf("id=")+3, message.content.length()-3));
                        mainWindow.notifyEnd(id, false);
                    }
                    else if (message.content.contains("###other_player_left###")) {
                        int id = Integer.parseInt(message.content.substring(
                                message.content.indexOf("id=")+3, message.content.length()-3));
                        mainWindow.notifyPlayerDisconnected(id);
                    }
                } else {
                    if (message.content.contains("###new_game_message###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.indexOf("###txt=")));
                        String text = message.content.substring
                                (message.content.indexOf("txt=")+4, message.content.length()-3);
                        mainWindow.recievedGameMessage(message.sender, id, text);
                    }
                    else {
                        mainWindow.recievedGroupMessage(message.sender, message.content);
                    }
                }
            }
            
            in.close();
            out.close();
            clientSocket.close();
        } catch(IOException e) {
            System.err.println("Error listning to server input");
            System.exit(1);
        }
    }
}
