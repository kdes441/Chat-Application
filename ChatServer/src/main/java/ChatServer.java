import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ChatServer extends WebSocketServer
{
    //Port number for chat server
    private static final int PORT_NUMBER = 5665;

    //Number of alive connections
    private static int aliveConnections;

    //Subscribers
    private static HashMap<String, WebSocket> clients = new HashMap();

    /**
     * Default constructor for chat server
     * @throws java.net.UnknownHostException
     */
    public ChatServer() throws java.net.UnknownHostException
    {
        super(new InetSocketAddress(PORT_NUMBER));
        System.out.println("ChatServer started on port: 5665");
    }

    /**
     * Start of the chat server
     */
    public void onStart() {
        System.out.println("Chat Server \n=====================\n");
    }

    /**
     * Gets errors
     * @param webSocket
     * @param ex
     */
    public void onError(WebSocket webSocket, Exception ex)
    {
        System.out.println("Error with:   " + webSocket + "  @ " + ex);
    }
    /**
     * Handles a new connection created between the user and server
     * @param webSocket
     * @param handshake
     */
    public void onOpen(WebSocket webSocket, ClientHandshake handshake)
    {
        System.out.println("New Handshake created: " + handshake.getResourceDescriptor());
    }

    /**
     * Handles when a connection to the socket server closes
     * @param webSocket
     * @param arg1
     * @param name
     * @param arg3
     */
    public void onClose(WebSocket webSocket, int arg1, String name, boolean arg3)
    {
        try
        {
            webSocket.close();
            aliveConnections -= 1;
            System.out.println("Number of alive connection: " + aliveConnections);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Handles when a message is send from a subscriber
     * @param webSocket
     * @param incomingMessage
     */
    public void onMessage(WebSocket webSocket, String incomingMessage)
    {
        JSONParser parser = new JSONParser();

        try
        {
            Object jsonObj = parser.parse(incomingMessage);
            JSONObject message = (JSONObject)jsonObj;
            String messageType = (String)message.get("MessageType");

            String str1;
            switch ((str1 = messageType).hashCode()) {case -1618876223:  if (str1.equals("broadcast")) {} break; case 871329084:  if (str1.equals("initiation")) break; break; case 1086463900:  if (!str1.equals("regular")) {
                return;

            if (clients.containsKey((String)message.get("From"))) {
                    clients.remove((String)message.get("From"));
                }
                clients.put((String)message.get("From"), webSocket);
                aliveConnections += 1;
                System.out.printf("New connection @ %s \n", new Object[] { webSocket.getRemoteSocketAddress().getAddress().getHostAddress() });
                System.out.printf("Number of alive connection: %d\n\n", new Object[] { Integer.valueOf(aliveConnections) });
            }
            else
            {
                relayMessage(message);
                return;


                broadcastMessage(message);
            }
                break;
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends message to specific subscriber
     * @param message
     */
    private static void relayMessage(JSONObject message)
    {
        WebSocket recipient = (WebSocket)clients.get((String)message.get("To"));

        try
        {
            recipient.send(message.toString());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send a message to all of the subscribers
     * @param message
     */
    private static void broadcastMessage(JSONObject message)
    {
        //TODO Add Code
    }

    /**
     * Entry point for the program
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            ChatServer server = new ChatServer();
            server.start();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
