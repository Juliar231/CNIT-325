package battleshipgame;


import java.io.*;
import java.net.*;

public class GameServer {
    private ServerSocket serverSocket;
    
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        
        while (true) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }
    
    public void stop() throws IOException {
        serverSocket.close();
    }
    
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (".".equals(inputLine)) {
                        out.println("good bye");
                        break;
                    }
                    out.println("Server: " + inputLine);
                }
                
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            }
        }
    }
}

