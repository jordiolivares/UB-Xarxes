import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Jordi Olivares Provencio on 5/10/16.
 */
public class Server {
    private static final HashMap<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Servidor iniciat");
        try (ServerSocket serverSocket = new ServerSocket(8981)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Usuari connectat");
                new ServerManager(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ServerManager extends Thread {
        private Socket socket;
        private String username;

        ServerManager(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                while (true) {
                    out.println("Escolleix l'usuari: ");
                    username = in.readLine();
                    synchronized (clients) {
                        if (!clients.containsKey(username)) {
                            clients.put(username, out);
                            out.println("Benvingut al xat :)");
                            break;
                        } else if (username == null) {
                            return;
                        } else {
                            out.println("Usuari ja es al xat :(, intenta un altre");
                        }
                    }
                }
                String input;
                while ((input = in.readLine()) != null) {
                    synchronized (clients) {
                        for (String user : clients.keySet()) {
                            if (user.equals(username))
                                continue;
                            clients.get(user).println(username + ": " + input);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (username != null)
                    synchronized (clients) {
                        clients.remove(username);
                    }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
