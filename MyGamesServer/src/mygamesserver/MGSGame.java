package mygamesserver;

public abstract class MGSGame {
    protected String[] players;
    public String type;
    
    public MGSGame(String type, String[] players) {
        this.type = type;
        this.players = players;
    }
    
    public String[] getPlayers() {
        return players;
    }
    
}
