package Domain.Servidor;

import Domain.Model.Entity.Drone;
import Domain.Service.ServidorService;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ImplServidorCliente implements Runnable {
    private final Socket socketCliente;
    private ServidorService servidorService = new ServidorService();

    public ImplServidorCliente(Socket socket) {
        this.socketCliente = socket;
    }

    @Override
    public void run() {
        try {
            // Cria o output primeiro
            ObjectOutputStream objOut = new ObjectOutputStream(socketCliente.getOutputStream());
            objOut.flush();  // força a escrita do cabeçalho

            // Depois cria o input
            ObjectInputStream objIn = new ObjectInputStream(socketCliente.getInputStream());

            socketCliente.setSoTimeout(5000);

            // Lê a identificação (supondo que o cliente envie via writeUTF)
            String clienteId = objIn.readUTF();
            System.out.println("Cliente identificado: " + clienteId);

            if (clienteId.equals("Central")) {
                Drone recebido = (Drone) objIn.readObject();
                servidorService.addDrone(recebido);
            } else if (clienteId.equals("Cliente")) {
                var drones = servidorService.getDronePosicao("Norte");
                objOut.writeObject(drones);
                objOut.flush();
            }

            objIn.close();
            objOut.close();

        } catch (SocketTimeoutException e) {
            System.err.println("Timeout com cliente: " + socketCliente.getInetAddress());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro com cliente:");
            e.printStackTrace();
        } finally {
            try {
                socketCliente.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket: " + e.getMessage());
            }
        }
    }
}