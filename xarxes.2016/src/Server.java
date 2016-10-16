import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Jordi Olivares and Alberto Leiva on 5/10/16.
 */
public class Server {
    private static final HashMap<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Servidor iniciat");
        // try-with-resources, tanca al final del try els recursos inicialitzats
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
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
                // Demanem l'usuari
                while (true) {
                    out.println("Escolleix l'usuari: ");
                    username = in.readLine();
                    // Utilitzem *synchronized* per tal d'evitar problemes de multithread
                    synchronized (clients) {
                        if (!clients.containsKey(username)) {
                            clients.put(username, out);
                            out.println("Benvingut al xat :)");
                            break;
                        // Cas que s'hagi desconnectat
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
