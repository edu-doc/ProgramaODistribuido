package Domain.Servidor;

import Domain.Model.Entity.Drone;
import Domain.Service.ServidorService;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ImplServidorCliente implements Runnable {
    private final Socket socketCliente;
    private ServidorService servidorService = new ServidorService();


    public static AtomicInteger conexoesAtivas = null;
    private final String serverId;

    public ImplServidorCliente(String serverId, AtomicInteger conexoesAtivas ,Socket socket) {
        this.socketCliente = socket;
        this.serverId = serverId;
        this.conexoesAtivas = conexoesAtivas;
    }

    private void enviarAtualizacao() {
        String mensagem = serverId + ":" + conexoesAtivas.decrementAndGet();
        try (MulticastSocket emisorSocket = new MulticastSocket()){
            byte[] bufferEnvio = mensagem.getBytes();
            DatagramPacket pacoteEnvio = new DatagramPacket(bufferEnvio, bufferEnvio.length, InetAddress.getByName("224.0.0.10"), 55560);
            emisorSocket.send(pacoteEnvio);
            System.out.println("SIMULADOR [" + serverId + "]: Enviou atualização -> " + mensagem);
        } catch (IOException e) {
            System.err.println("SIMULADOR [" + serverId + "]: Erro ao enviar atualização multicast: " + e.getMessage());
        }
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
            enviarAtualizacao();
        }
    }
}