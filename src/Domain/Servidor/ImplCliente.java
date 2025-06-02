package Domain.Servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ImplCliente implements Runnable {

    private final Socket socket;

    public ImplCliente(Socket socketCliente) {
        this.socket = socketCliente;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true)
        ) {
            // Mensagem opcional de boas-vindas
            out.println("Bem-vindo ao LoadBalance.");

            int porta = 54321;

            // Envia a porta e encerra
            out.println(porta);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close(); // Fecha a conexão após enviar a porta
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
