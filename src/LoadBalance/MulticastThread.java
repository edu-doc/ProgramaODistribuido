package LoadBalance;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MulticastThread implements Runnable{

    private CopyOnWriteArrayList<ServerInfo> servidores;

    private MulticastSocket multiSocket;
    private InetAddress multicastIP = InetAddress.getByName("224.0.0.10") ;
    private InetSocketAddress grupo ;
    private NetworkInterface interfaceRede = NetworkInterface.getByName("ethernet_32776");
    private final int porta = 55560;

    private boolean rodando = true;

    public MulticastThread(CopyOnWriteArrayList<ServerInfo> servidores) throws IOException {
        this.servidores = servidores; // ArrayList Thread Safe

        multiSocket = new MulticastSocket(this.porta);

        System.out.println("Receptor " +
                InetAddress.getLocalHost() +
                " executando na porta " +
                multiSocket.getLocalPort());


        grupo = new InetSocketAddress(this.multicastIP, this.porta);

        if (interfaceRede == null) {
            throw new IOException("A interface de rede não foi encontrada.");
        }

        System.out.println("Balanceador de Carga: Juntando-se ao grupo " + this.multicastIP.getHostAddress() +
                " na porta " + this.porta + " interface: " + interfaceRede.getDisplayName() + ".");

        multiSocket.joinGroup(grupo, interfaceRede);

    }

    @Override
    public void run() {
        System.out.println("Balanceador de Carga: Thread do listener interno iniciada. Escutando em " +
                this.multicastIP.getHostAddress() + ":" + this.multiSocket.getLocalPort());

        byte[] buffer = new byte[1024];

        while (this.rodando) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try {
                this.multiSocket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String message = new String(packet.getData(), 0, packet.getLength()).trim();

            String[] parts = message.split(":", 2);
            if (parts.length == 2) {
                String serverId = parts[0];
                int quantidadeConexoes = Integer.parseInt(parts[1]);
                this.atualizarContagemServidor(serverId, quantidadeConexoes);
            } else {
                System.err.println("Thread de Balanceamento: Formato de mensagem multicast inválido: '" + message + "'. Esperado 'ID:Contagem'.");
            }

        }

        try {
            this.multiSocket.leaveGroup(this.grupo, this.interfaceRede);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.multiSocket.close();

        System.out.println("Thread de Balanceamento: Thread do Balanceamento encerrada.");
    }

    protected void atualizarContagemServidor(String serverId, int novaContagem) {
        if (serverId == null) {
            System.err.println("Thread de Balanceamento: Recebida atualização com serverId nulo. Ignorando.");
            return;
        }
        for (ServerInfo server : servidores) {
            if (server.getId().equals(serverId)) {
                server.setConexoesAtivas(novaContagem);
                return;
            }
        }

    }
}
