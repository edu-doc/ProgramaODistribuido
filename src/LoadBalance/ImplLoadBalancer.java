package LoadBalance;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

public class ImplLoadBalancer implements Runnable {
    private final List<ServerInfo> servidores;
    private final Thread ThreadBalanceamento;

    // Campos conforme seu construtor
    private MulticastSocket multiSocket;
    private InetAddress multicastIP;
    private InetSocketAddress grupo;
    private NetworkInterface interfaceRede;
    private final int porta;
    private boolean rodando = true;


    public ImplLoadBalancer(String multicastAddress, int porta) throws IOException {
        this.servidores = new CopyOnWriteArrayList<>(); // ArrayList Thread Safe
        this.porta = porta; // Armazena a porta de escuta

        try {
            multiSocket = new MulticastSocket(this.porta);
            multicastIP = InetAddress.getByName(multicastAddress);
            grupo = new InetSocketAddress(multicastIP, this.porta);


            interfaceRede = NetworkInterface.getByName("ethernet_32776");
            if (interfaceRede == null) {
                throw new IOException("A interface de rede 'Ethernet 2' não foi encontrada.");
            }

            System.out.println("Balanceador de Carga: Juntando-se ao grupo " + this.multicastIP.getHostAddress() +
                    " na porta " + this.porta + " interface: " + interfaceRede.getDisplayName() + ".");

            multiSocket.joinGroup(grupo, interfaceRede);

            multiSocket.setSoTimeout(1000);

            this.ThreadBalanceamento = new Thread(this);
            this.ThreadBalanceamento.setName("Thread-Balanceamento");
            this.ThreadBalanceamento.setDaemon(true);
            this.ThreadBalanceamento.start();

        } catch (IOException e) {
            System.err.println("CRÍTICO: Falha ao iniciar a Thread de Balanceamento do Balanceador de Carga: " + e.getMessage());

            if (this.multiSocket != null && !this.multiSocket.isClosed()) {
                this.multiSocket.close();
            }
            throw new IOException("Não foi possível iniciar a Thread de Balanceamento do Balanceador de Carga.", e);
        }
    }

    @Override
    public void run() {

        System.out.println("Balanceador de Carga: Thread do listener interno iniciada. Escutando em " +
                this.multicastIP.getHostAddress() + ":" + this.multiSocket.getLocalPort());

        byte[] buffer = new byte[256];

        while (this.rodando) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                this.multiSocket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength()).trim();

                String[] parts = message.split(":", 2);
                if (parts.length == 2) {
                    String serverId = parts[0];
                    try {
                        int quantidadeConexoes = Integer.parseInt(parts[1]);
                        this.atualizarContagemServidor(serverId, quantidadeConexoes); // Chama método da própria classe - Correto
                    } catch (NumberFormatException e) {
                        System.err.println("Thread de Balanceamento: Contagem de conexões inválida na mensagem: '" + message + "'. Erro: " + e.getMessage());
                    }
                } else {
                    System.err.println("Thread de Balanceamento: Formato de mensagem multicast inválido: '" + message + "'. Esperado 'ID:Contagem'.");
                }
            } catch (SocketTimeoutException e) {
                System.err.println("Thread de Balanceamento: Erro de Timeout: " + e.getMessage());
            } catch (IOException e) {
                if (this.rodando) {
                    System.err.println("Thread de Balanceamento: Erro de E/S ao receber multicast: " + e.getMessage());
                }
            }
        }

        // Limpeza do socket
        try {
            if (this.multiSocket != null && !this.multiSocket.isClosed()) {
                this.multiSocket.leaveGroup(this.grupo, this.interfaceRede);
                this.multiSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Thread de Balanceamento: Erro durante a limpeza do socket: " + e.getMessage());
        }
        System.out.println("Thread de Balanceamento: Thread do Balanceamento encerrada.");
    }

    public void adicionarServidor(ServerInfo servidor) {

        if (servidores.stream().anyMatch(s -> s.getId().equals(servidor.getId()))) {
            System.out.println("Servidor com ID '" + servidor.getId() + "' já existe. Não foi adicionado.");
        } else {
            this.servidores.add(servidor);
            System.out.println("Servidor adicionado: " + servidor.getId() + " (" + servidor.getHost() + ":" + servidor.getPorta() + ")");
        }
    }

    public ServerInfo getProximoServidor() {

        if (servidores.isEmpty()) {
            throw new NoSuchElementException("Nenhum servidor disponível no balanceador de carga.");
        }

        ServerInfo servidorEscolhido = null;
        int minConexoes = Integer.MAX_VALUE; // Maior valor possivel dos inteiros, usado em comparação

        for (ServerInfo servidorCandidato : servidores) {
            if (servidorCandidato.getConexoesAtivas() < minConexoes) {
                minConexoes = servidorCandidato.getConexoesAtivas();
                servidorEscolhido = servidorCandidato;
            }
        }

        if (servidorEscolhido == null && !servidores.isEmpty()) {
            return servidores.get(0);
        }
        return servidorEscolhido;
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

    public void imprimirStatusServidores() {
        System.out.println("\n--- Status dos Servidores no Balanceador ---");
        if (servidores.isEmpty()) {
            System.out.println("Nenhum servidor registrado.");
            return;
        }
        for (ServerInfo servidor : servidores) {
            System.out.println("ID: " + servidor.getId() +
                    " | Host: " + servidor.getHost() + ":" + servidor.getPorta() +
                    " | Conexões: " + servidor.getConexoesAtivas());
        }
        System.out.println("---------------------------------------------\n");
    }

    public void shutdown() {
        System.out.println("Balanceador de Carga: Encerrando Thread de Balanceamento...");
        this.rodando = false;

        if (ThreadBalanceamento != null) {
            try {
                ThreadBalanceamento.join(2000);
                if (ThreadBalanceamento.isAlive()) {
                    System.err.println("Balanceador de Carga: Thread de Balanceamento não encerrou a tempo. Interrompendo...");
                    ThreadBalanceamento.interrupt();
                }
            } catch (InterruptedException e) {
                System.err.println("Balanceador de Carga: Interrompido ao esperar pelo Thread de Balanceamento.");
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Balanceador de Carga: Encerramento solicitado.");
    }
}