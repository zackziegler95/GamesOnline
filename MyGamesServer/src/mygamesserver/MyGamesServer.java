package mygamesserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

public class MyGamesServer extends Thread {
    private ServerSocket serverSocket = null;
    private FindClients clientListener = null;
    private MGSMainWindow gui = null;
    
    private HashMap<String, MGSConnectionThread> clients = null;
    private int num_connected = 0;
    private int max_connections = 10;
    
    private HashMap<Integer, MGSLobby> lobbies = null;
    private int lobbyNum = 1;
    //private int max_lobbies = 100;
    
    private HashMap<Integer, MGSGame> games = null;
    
    private boolean listening = false;
    
    /**
     * Starts a server
     * @param gui The associate GUI
     */
    public MyGamesServer(MGSMainWindow gui) {
        this.gui = gui;
    }
    
    /**
     * Add a client to the list of connected clients
     * @param client The thread of the client
     * @return 0 if there are no errors
     */
    public int addClient(MGSConnectionThread client) {
        if (clients.containsKey(client.name)) {
            System.err.println("Duplicate name");
            return 2;
        } else if (num_connected >= max_connections) {
            System.err.println("Too many connections!");
            return 1;
        } else {
            clients.put(client.name, client);
            num_connected += 1;
            sendClientList();
            System.out.println("New client: "+client.name+" has connected.");
            return 0;
        }
    }
    
    /**
     * Remove a client from tracking
     * @param client THe thread of the client
     */
    public void removeClient(MGSConnectionThread client) {
        if (clients.containsKey(client.name)) {
            clients.remove(client.name);
            num_connected -= 1;
            System.out.println("Client: "+client.name+" has disconnected.");
            sendClientList();
        } else {
            System.err.println("Error removing client, it does not exist");
        }
    }
    
    /**
     * Tell everyone about all the clients that exist
     */
    private void sendClientList() {
        String[] clientIPList = new String[clients.size()];
        MGSConnectionThread[] clientsList = clients.values().toArray(new MGSConnectionThread[0]);
        String names = "###name_list=";
        
        for(int n = 0; n < clientsList.length; n++) {
            clientIPList[n] = String.valueOf(clientsList[n].getSocket().getRemoteSocketAddress()) + " " + clientsList[n].name;
            names += clientsList[n].name + ",";
        }
        names = names.substring(0, names.length()-1) + "###";
        gui.writeClientList(clientIPList);
        
        for(int n = 0; n < clientsList.length; n++) {
            if (clientsList[n] != null) {
                clientsList[n].sendMessage("server", names);
            }
        }
    }
    
    public void addLobby(String host, String game) {
        MGSLobby newLobby = new MGSLobby(host, game);
        lobbies.put(lobbyNum, newLobby);
        sendMessage(host, "server", "###newLobbyRecieved###id="+lobbyNum+"###host="+host+"###game="+game+"###"); 
        joinLobby(host, lobbyNum);
        sendLobbyInfo(lobbyNum);
        
        lobbyNum += 1;
        sendLobbyList();
    }
    
    public void removeLobby(int id) {
        MGSLobby lobby = lobbies.get(id);
        String[] players = lobby.getPlayers();
        
        for (String player : players) {
            clients.get(player).sendMessage("server", "###lobby_host_disconnected###id="+id+"###");
        }
        
        lobbies.remove(id);
        sendLobbyList();
    }
    
    public void sendLobbyList() {
        String names = "###lobby_list=";
        Integer[] lobbyKeys = lobbies.keySet().toArray(new Integer[0]);
        String[] lobbyStrings = new String[lobbies.size()];
        
        for (int i = 0; i < lobbies.size(); i++) {
            lobbyStrings[i] = lobbies.get(lobbyKeys[i]).toString();
        }
        
        for(int n = 0; n < lobbyStrings.length; n++) {
            names += lobbyKeys[n]+"-"+lobbyStrings[n]+",";
        }
        names = names.substring(0, names.length()-1) + "###";
        
        MGSConnectionThread[] clientsList = clients.values().toArray(new MGSConnectionThread[0]);
        
        for(int n = 0; n < clientsList.length; n++) {
            if (clientsList[n] != null) {
                clientsList[n].sendMessage("server", names);
            }
        }
    }
    
    public String[] getLobbyInfo(int id) {
        return lobbies.get(id).toString().split("-");
    }
    
    public int joinLobby(String name, int id) {
        return lobbies.get(id).addPlayer(name);
    }
    
    public void leaveLobby(String name, int id) {
        if (lobbies.containsKey(id)) {
            lobbies.get(id).removePlayer(name);
            sendLobbyInfo(id);
        }
    }
    
    public void sendLobbyInfo(int lobbyID) {
        MGSLobby lobby = (MGSLobby) lobbies.get(lobbyID);
        String[] players = lobby.getPlayers();
        
        String playerString = "";
        for (String player : players) {
            playerString += player+",";
        }
        playerString = playerString.substring(0, playerString.length()-1);
        
        for (String player : players) {
            clients.get(player).sendMessage("server", "###lobby_info###id="+lobbyID+"###players="+playerString+"###");
        }
    }
    
    /**
     * Start listening for connections
     * @param port The port
     * @return 1=success, 0=error
     */
    public int startListener(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clients = new HashMap();
            lobbies = new HashMap();
            games = new HashMap();
            clientListener = new FindClients(this, serverSocket);
            clientListener.start();
            listening = true;
            return 1;
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println("Bad port");
            return 0;
        }
    }
    
    /**
     * Stop listening for connections
     * @return 1=success, 0=error
     */
    public int stopListening() {
        try {
            if (listening) {
                for (MGSConnectionThread client : clients.values().toArray(new MGSConnectionThread[0])) {
                    client.sendMessage("server", "###server_disconnected###");
                    client.kill();
                }
                clientListener.kill();
                clientListener = null;
                serverSocket.close();
                gui.writeClientList(new String[0]);
                clients.clear();
                games.clear();
                lobbies.clear();
                num_connected = 0;
                listening = false;
            }
            return 1;
        } catch (IOException e) {
            System.err.println("Could not close server.");
            return 0;
        }
    }
    
    /**
     * Send a message from one client to another, through the server
     * @param reciever Client that receives the message
     * @param sender Client that sent the message
     * @param message The message, in string form
     */
    public void sendMessage(String reciever, String sender, String message) {
        if (clients.containsKey(reciever)) {
            clients.get(reciever).sendMessage(sender, message);
        } else if (reciever.equals("all")) {
            MGSConnectionThread[] clientsList = clients.values().toArray(new MGSConnectionThread[0]);
            for (MGSConnectionThread c : clientsList) {
                c.sendMessage(sender, message);
            }
        } else {
            System.err.println("Error sending message to " + reciever);
        }
    }
    
    /**
     * Send a message in game
     * @param id ID of the game number
     * @param sender Sender name
     * @param text Text of the message
     */
    public void sendGameMessage(int id, String sender, String text) {
        String[] players;
        if (lobbies.containsKey(id)) {
            players = lobbies.get(id).getPlayers();
        } else if (games.containsKey(id)) {
            players = games.get(id).getPlayers();
        } else {
            players = new String[0];
        }
        
        for (String player : players) {
            sendMessage(player, sender, "###new_game_message###id="+id+"###txt="+text+"###");
        }
    }
    
    /**
     * Receive a ready signal
     * @param id lobby ID
     * @param player player name
     * @param yes true=ready
     */
    public void setReady(int id, String player, boolean yes) {
        MGSLobby lobby = lobbies.get(id);
        String[] players = lobby.getPlayers();
        if (lobby.setReady(player, yes)) {
            for (String p : players) {
                sendMessage(p, "server", "###lobby_all_ready###id="+id+"###");
            }
        } else {
            for (String p : players) {
                sendMessage(p, "server", "###lobby_not_all_ready###id="+id+"###");
            }
        }
    }
    
    /**
     * Turn a waiting lobby into a game
     * @param id the id of the lobby
     */
    public void newGameFromLobby(int id) {
        MGSLobby lobby = lobbies.get(id);
        String game = lobby.game;
        
        if (game.equals("Checkers")) {
            String p1 = lobby.getPlayers()[0];
            String p2 = lobby.getPlayers()[1];
            MGSCheckers checkers = new MGSCheckers(lobby.getPlayers());
            games.put(id, checkers);
            
            sendMessage(p1, "server",
                    "###created_new_game###id="+id+"###game=Checkers###"
                    + "partner="+p2+"###turn=true###board="+checkers.getBoard()+"###");
            sendMessage(p2, "server",
                    "###created_new_game###id="+id+"###game=Checkers###"
                    + "partner="+p1+"###turn=false###board="+checkers.getRotatedBoard()+"###");
        }
        lobbies.remove(id);
        sendLobbyList();
    }
    
    /**
     * The server handles all game logic
     * @param id Lobby id
     * @param data The game move
     */
    public void checkGameMove(int id, String data) {
        MGSGame game = games.get(id);
        if (game.type.equals("Checkers")) {
            String name = data.substring(data.indexOf("name=")+5, data.indexOf("###from"));
            int[] from = {Integer.parseInt(data.charAt(data.indexOf("from=")+5)+""), Integer.parseInt(data.charAt(data.indexOf("from=")+7)+"")};
            int[] to = {Integer.parseInt(data.charAt(data.indexOf("to=")+3)+""), Integer.parseInt(data.charAt(data.indexOf("to=")+5)+"")};
            int res = ((MGSCheckers) game).checkMove(name, from, to);
            
            sendMessage(name, "server", "###move_checked###id="+id+"###res="+res+"###from="+from[0]+","+from[1]+"###to="+to[0]+","+to[1]+"###");
        } 
    }
    
    /**
     * Make the move
     * @param id Lobby ID
     * @param data The game move
     */
    public void gameMove(int id, String data) {
        MGSGame game = games.get(id);
        if (game.type.equals("Checkers")) {
            String player = data.substring(data.indexOf("player=")+7, data.indexOf("###new_board"));
            String newBoard = data.substring(data.indexOf("new_board=")+10);
            
            MGSCheckers checkers = (MGSCheckers) game;
            checkers.setBoard(checkers.stringBoardToInt(newBoard));
            
            checkers.changeTurns();
            String rotBoard = checkers.getRotatedBoard(newBoard);
            
            sendMessage(checkers.getTurn(), "server", "###inc_game_move###id="+id+"###new_board="+rotBoard+"###");
            
            if (checkers.checkEnd(player)) {
                sendMessage(checkers.getTurn(), "server", "###you_win###id="+id+"###");
                sendMessage(player, "server", "###you_lose###id="+id+"###");
            }
            
            if (checkers.checkEnd(checkers.getTurn())) {
                sendMessage(player, "server", "###you_win###id="+id+"###");
                sendMessage(checkers.getTurn(), "server", "###you_lose###id="+id+"###");
            }
        } 
    }
    
    /**
     * Handle the game closing
     * @param id lobby ID
     * @param player the leaving player's name
     */
    public void gameClosedByClient(int id, String player) {
        MGSGame game = games.get(id);
        if (game.type.equals("Checkers")) {
            MGSCheckers checkers = (MGSCheckers) game;
            String otherPlayer;
            if (checkers.players[0].equals(player)) {
                otherPlayer = checkers.players[1];
            } else {
                otherPlayer = checkers.players[0];
            }
            sendMessage(otherPlayer, "server", "###other_player_left###id="+id+"###");
        }
        games.remove(id);
    }
    
    public void gameWindowClosedAfterEnd(int id) {
        if (games.containsKey(id)) {
            games.remove(id);
        }
    }
    
    public void clientGivesUp(int id, String player) {
        MGSGame game = games.get(id);
        if (game.type.equals("Checkers")) {
            MGSCheckers checkers = (MGSCheckers) game;
            String otherPlayer;
            if (checkers.players[0].equals(player)) {
                otherPlayer = checkers.players[1];
            } else {
                otherPlayer = checkers.players[0];
            }
            sendMessage(otherPlayer, "server", "###other_player_left###id="+id+"###");
            sendMessage(player, "server", "###you_lose###id="+id+"###");
        }
    }
    
    public void clientDisconnected(String client) {
        MGSConnectionThread c = clients.get(client);
        removeClient(c);
    }
}       