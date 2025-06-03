package Domain.Servidor;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

class ImplServidor implements Runnable {
    private final String serverId;

    private final int porta;
    private DatagramSocket emisorSocket; // Socket para enviar
    private String grupo;

    private static int conexoesAtivas = 0;
    private boolean rodando = true;


    public ImplServidor(String serverId, String grupo, int porta) throws IOException {
        this.serverId = serverId;
        this.porta = porta;
        this.grupo = grupo;
        this.emisorSocket = new DatagramSocket();
        enviarAtualizacao(); // Envia estado inicial (0 conexões)
    }

    public void simularNovaConexao() {
        conexoesAtivas++;
        enviarAtualizacao();
    }

    public void simularConexaoFechada() {
        if (conexoesAtivas > 0) {
            conexoesAtivas--;
        }
        enviarAtualizacao();
    }

    private void enviarAtualizacao() {
        String mensagem = serverId + ":" + conexoesAtivas;
        try {
            byte[] bufferEnvio = mensagem.getBytes();
            DatagramPacket pacoteEnvio = new DatagramPacket(bufferEnvio, bufferEnvio.length, InetAddress.getByName(grupo), porta);
            emisorSocket.send(pacoteEnvio);
            System.out.println("SIMULADOR [" + serverId + "]: Enviou atualização -> " + mensagem);
        } catch (IOException e) {
            System.err.println("SIMULADOR [" + serverId + "]: Erro ao enviar atualização multicast: " + e.getMessage());
        }
    }

    public void close() {
        if (emisorSocket != null) {
            emisorSocket.close();
        }
    }

    @Override
    public void run() {
        while (rodando) {
            try {
                // Apenas para simular algo, envia a cada 5 segundos:
                simularNovaConexao();
                simularNovaConexao();
                simularNovaConexao();
                Thread.sleep(5000);
                simularConexaoFechada();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Servidor interrompido.");
                rodando = false;
            }
        }
        close();
    }
}