# GamesOnline

GamesOnline is a client/server combination that allows users anywhere in the world to play common games with each other. Right now only Checkers is implemented, but the infrastructure exists for many more. There are two parts MyGamesClient and MyGamesServer, following the traditional client-server model. There need only be one server running, to which each client connects.

## MyGamesServer

MyGamesServer provides a server that listens for incoming client connections and handles all communication between clients. It includes a simple GUI that shows the port and the connected clients. Each client is assigned to a connection thread, where messages received are sent to the main MyGamesServer object for handling.

## MyGamesClient

To connect to the server, give the IP address of the server and the port the server is listening on. You must also include a name. In the main window you will be able to chat to all other connected users, as well as see the open lobbies. You can also create a new lobby. Once you join a lobby you can indicate that you are ready, and when all users are ready the host starts the game.



To run either program, execute the jar file MyGamesClient/dist/MyGamesClient.jar or MyGamesServer/dist/MyGamesServer.jar as usual:
java -jar <jar file name>

You will need Java JRE 1.8, which can be found here: http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html. 

If you want to accept connections outside of your local router's network you may need to forward the server's port to the correct device.
