import java.io.*;
import java.net.Socket;
import java.util.Scanner;
/**
 *
 * @author manel
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try{
            boolean fin = false;
            Scanner entradaDades = new Scanner(System.in);
            //podeu provar de conectar la IP 132.163.4.101 que correspon al National Institute of Standards
            //and Technology, en Boulder Colorado, i ofereix la mesura d'un rellotge atòmic de Cesi
            System.out.println("Introdueix la IP del host");
            //String IP_Address = entradaDades.next();
            String IP_Address = "127.0.0.1";
            //el port al que ens conectem és el 13
            System.out.println("introdueix el port");
            //int port = entradaDades.nextInt();
            int port = 8189;
            //obrim un nou socket
            Socket socket = new Socket(IP_Address, port);
                try {
                    InputStream entrada = socket.getInputStream();
                    OutputStream salida = socket.getOutputStream();
                    Scanner in = new Scanner(entrada);
                    Scanner input = new  Scanner(System.in);
                    PrintWriter out = new PrintWriter(salida,true);

                    while (!fin) {
                        if(entrada.available() > 0) {
                            String linia = in.nextLine();
                            System.out.println(linia);
                        }

                        if(System.in.available() > 0) {
                            String dadesAEnviar = input.nextLine();
                            if(dadesAEnviar.trim().equals("BYE"))
                                fin = true;
                            out.println(dadesAEnviar);
                        }
                    }
                    //compte amb fer servir el Rellotge atomic aqui perque no podem enviar dades, només ens envia les
                    // dades i donarà una excepció per evitar-ho comenteu les següents 3 linies

                } finally {
                    socket.close();
                }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
