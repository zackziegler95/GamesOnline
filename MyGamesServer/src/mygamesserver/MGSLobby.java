package mygamesserver;

public class MGSLobby {
    private String[] playerList = null;
    private int[] playersReadyList = null;
    private int playersReady = 0;
    private int currentPlayers = 0;
    private int maxPlayers = 0;
    
    public String host = "";
    public String game = "";
    
    /**
     * The starting point of a game, while waiting for all players
     * to be ready
     * @param host Host name
     * @param game Game name
     */
    public MGSLobby(String host, String game) {
        this.host = host;
        this.game = game;
        
        if (game.equals("Checkers")){
            maxPlayers = 2;
        }
        
        playerList = new String[maxPlayers];
        playersReadyList = new int[maxPlayers];
        for (int i = 0; i < maxPlayers; i++) playersReadyList[i] = 0;
    }
    
    public int addPlayer(String player) {
        if (currentPlayers >= maxPlayers) {
            return 1;
        }
        for (int i = 0; i < maxPlayers; i++) {
            if (playerList[i] == null) {
                playerList[i] = player;
                break;
            }
        }
        currentPlayers++;
        return 0;
    }
    
    public void removePlayer(String player) {
        for (int i = 0; i < maxPlayers; i++) {
            if (playerList[i].equals(player)) {
                playerList[i] = null;
                break;
            }
        }
        currentPlayers--;
    }
    
    public String[] getPlayers() {
        String[] allPlayers = new String[currentPlayers];
        int i = 0;
        for (String player : playerList) {
            if (player != null) {
                allPlayers[i] = player;
                i++;
            }
        }
        return allPlayers;
    }
    
    public boolean setReady(String player, boolean yes) {
        for (int i = 0; i < playerList.length; i++) {
            if (playerList[i].equals(player)) {
                if (yes) {
                    if (playersReadyList[i] == 0) {
                        playersReadyList[i] = 1;
                        playersReady++;
                    }
                    break;
                } else {
                    if (playersReadyList[i] == 1) {
                        playersReadyList[i] = 0;
                        playersReady--;
                    }
                    break;
                }
            }
        }
        if (playersReady == maxPlayers) return true;
        return false;
    }
    
    @Override
    public String toString() {
        return host+"-"+game;
    }
}
