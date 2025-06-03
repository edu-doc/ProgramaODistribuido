package Domain.Servidor;

import Domain.Model.Entity.Drone;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ImplServidorCliente implements Runnable {
    private final Socket socketCliente;

    public ImplServidorCliente(Socket socket) {
        this.socketCliente = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(socketCliente.getInputStream()));
             PrintWriter out = new PrintWriter(
                     socketCliente.getOutputStream(), true)) {

            socketCliente.setSoTimeout(5000);

            // Lê identificação do cliente
            String clienteId = in.readLine();
            System.out.println("Cliente identificado: " + clienteId);

            if (clienteId == "Central") {
                ObjectInputStream objIn = new ObjectInputStream(socketCliente.getInputStream());
                Drone recebido = (Drone) objIn.readObject();
                System.out.println("Drone recebido: " + recebido);
            }

            out.println("Bem vindo cliente");


        } catch (SocketTimeoutException e) {
            System.err.println("Timeout com cliente: " + socketCliente.getInetAddress());
        } catch (IOException e) {
            System.err.println("Erro com cliente: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Drone não encontrado: " + e.getMessage());
        } finally {
            try {
                socketCliente.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket: " + e.getMessage());
            }
        }
    }
}