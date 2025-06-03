package Domain.Servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ImplCliente implements Runnable {
    private final Socket socketCliente;

    public ImplCliente(Socket socket) {
        this.socketCliente = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socketCliente.getInputStream()));
             PrintWriter out = new PrintWriter(
                     socketCliente.getOutputStream(), true)) {

            // Lê identificação do cliente
            String clienteId = in.readLine();
            out.println("Cliente identificado: " + clienteId);



        } catch (SocketTimeoutException e) {
            System.err.println("Timeout com cliente: " + socketCliente.getInetAddress());
        } catch (IOException e) {
            System.err.println("Erro com cliente: " + e.getMessage());
        } finally {
            try {
                socketCliente.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket: " + e.getMessage());
            }
        }
    }
}