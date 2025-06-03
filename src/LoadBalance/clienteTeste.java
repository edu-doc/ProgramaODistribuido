package LoadBalance;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class clienteTeste {
    public static void main(String[] args) {
        String servidor = "localhost"; // ou IP do servidor
        int porta = 22234; // porta do servidor unicast

        try (Socket socket = new Socket(servidor, porta);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Lê e exibe a resposta do servidor
            String resposta = in.readLine();
            if (resposta != null) {
                System.out.println("Servidor respondeu: " + resposta);
            } else {
                System.out.println("Nenhuma resposta recebida do servidor.");
            }

        } catch (Exception e) {
            System.out.println("Erro ao conectar com o servidor:");
            e.printStackTrace();
        }
    }
}
