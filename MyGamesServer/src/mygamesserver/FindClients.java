package mygamesserver;

import java.io.IOException;
import java.net.ServerSocket;

public class FindClients extends Thread {
    private MyGamesServer server = null;
    private ServerSocket serverSocket = null;
    private boolean keepLooking = true;
    
    /**
     * Listen for incoming connections
     * @param server Reference to the server
     * @param serverSocket The server's socket handle
     */
    public FindClients(MyGamesServer server, ServerSocket serverSocket) {
        this.server = server;
        this.serverSocket = serverSocket;
    }
    
    public void kill() {
        keepLooking = false;
    }
    
    @Override
    public void run() {
        while (keepLooking) {
            try {
                MGSConnectionThread newThread = new MGSConnectionThread(server, serverSocket.accept());
                newThread.start();
            } catch(IOException e) {}
        }
    }
}
