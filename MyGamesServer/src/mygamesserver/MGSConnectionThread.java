package mygamesserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The central connection class that controls a single thread
 * @author zack
 */
public class MGSConnectionThread extends Thread {
    private MyGamesServer server = null;
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private boolean listening = true;
    private boolean ready = false;
    
    public String name = "";
    
    private static class Message {
        public String recip;
        public String content;
        
        public Message(String plainText) {
            recip = plainText.substring(plainText.indexOf("###sendto")+10, plainText.indexOf("###message="));
            content = plainText.substring(plainText.indexOf("###message")+11, plainText.length());
        }
        
        @Override
        public String toString() {
            return "Recipiant: " + recip + " Message: " + content;
        }
    }
    
    /**
     * Initialize a connection
     * @param server Reference to a server object
     * @param socket The socket for the connection
     */
    public MGSConnectionThread(MyGamesServer server, Socket socket) {
        super("MyCheckersServerThread");
        this.socket = socket;
        this.server = server;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Cannot initiate I/O for this thread.");
        }
    }
    
    public Socket getSocket() {
        return this.socket;
    }
    
    public void sendMessage(String sender, String incMessage) {
       out.println("###sentfrom=" + sender + "###message=" + incMessage);
    }
    
    public void kill() {
        listening = false;
    }
    
    /**
     * Parse any input from the client
     */
    @Override
    public void run() {
        try {
            String inputLine;
            
            while ((inputLine = in.readLine()) != null && listening) {
                Message message = new Message(inputLine);
                if (message.recip.equals("server")) {
                    if (message.content.equals("###client_disconnecting###")) {
                        server.clientDisconnected(name);
                    }
                    else if (message.content.contains("###group_message=")) {
                        String groupMessage = message.content.substring
                                (message.content.indexOf("=")+1, message.content.length()-3);
                        server.sendMessage("all", name, groupMessage);
                    }
                    else if (message.content.contains("###game_message###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.indexOf("###txt=")));
                        String messageText = message.content.substring
                                (message.content.indexOf("txt=")+4, message.content.length()-3);
                        server.sendGameMessage(id, name, messageText);
                    }
                    else if (message.content.contains("###player_ready###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.indexOf("###player=")));
                        String player = message.content.substring
                                (message.content.indexOf("player=")+7, message.content.length()-3);
                        server.setReady(id, player, true);
                    }
                    else if (message.content.contains("###new_lobby###")) {
                        String host = message.content.substring
                                (message.content.indexOf("host=")+5, message.content.indexOf("###game="));
                        String game = message.content.substring
                                (message.content.indexOf("game=")+5, message.content.length()-3);
                        server.addLobby(host, game);
                    }
                    else if (message.content.contains("###killed_lobby###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.length()-3));
                        server.removeLobby(id);
                    }
                    else if (message.content.contains("###left_lobby###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.length()-3));
                        server.setReady(id, name, false); 
                        server.leaveLobby(name, id);

                    }
                    else if (message.content.contains("###join_lobby_request###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.length()-3));
                        
                        int res = server.joinLobby(name, id);
                        if (res == 0) {
                            String[] lobbyInfo = server.getLobbyInfo(id);
                            String host = lobbyInfo[0];
                            String game = lobbyInfo[1];
                            sendMessage("server", "###joining_lobby_successful###id="+id+"###host="+host+"###game="+game+"###");
                            server.sendLobbyInfo(id);
                        } else if (res == 1) {
                            sendMessage("server", "###joining_lobby_failed###problem=full###");
                        }
                    }
                    else if (message.content.equals("###get_lobby_list###")) {
                        server.sendLobbyList();
                    }
                    else if (message.content.contains("###new_game###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("lobby_id=")+9, message.content.length()-3));
                        server.newGameFromLobby(id);
                    }
                    else if (message.content.contains("###check_game_move###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.indexOf("###", message.content.indexOf("id=")+3)));
                        String data = message.content.substring(
                                message.content.indexOf("###", message.content.indexOf("id=")+3)+3, message.content.length()-3);
                        server.checkGameMove(id, data);
                    }
                    else if (message.content.contains("###new_move###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.indexOf("###", message.content.indexOf("id=")+3)));
                        String data = message.content.substring(
                                message.content.indexOf("###", message.content.indexOf("id=")+3)+3, message.content.length()-3);
                        server.gameMove(id, data);
                    }
                    else if (message.content.contains("###new_connection###")) {
                        name = message.content.substring(message.content.indexOf("=")+1, message.content.length()-3);
                        
                        int res = server.addClient(this);
                        if (res == 0) {
                            ready = true;
                        } else if (res == 1) {
                            sendMessage("server", "###too_many_connections###");
                            break;
                        } else if (res == 2) {
                            sendMessage("server", "###name_already_taken###");
                            break;
                        }
                    }
                    else if (message.content.contains("###game_closed###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.length()-3));
                        server.gameClosedByClient(id, name);
                    }
                    else if (message.content.contains("game_window_closed_after_end###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.length()-3));
                        server.gameWindowClosedAfterEnd(id);
                    }
                    else if (message.content.contains("###i_give_up###")) {
                        int id = Integer.parseInt(message.content.substring
                                (message.content.indexOf("id=")+3, message.content.length()-3));
                        server.clientGivesUp(id, name);
                    }
                } else {
                    if (ready) {
                        server.sendMessage(message.recip, name, message.content);
                    }
                }
            }
            
            out.close();
            in.close();
            socket.close();
            if (ready) {
                server.removeClient(this);
            }
            
        } catch (IOException e) {}
    }
}
