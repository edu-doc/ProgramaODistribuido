package Conexoes.DroneCentrlMulti;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class EmissorMultiCast {

    public static void main(String[] args) {
        String mensagem = "Mensagem do emissor Python para receptores Java";
        String multicastIp = "224.0.0.1";
        int porta = 55554;

        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName(multicastIp);
            socket.setTimeToLive(1); // TTL = 1, restrito à rede local

            while (true) {
                byte[] buffer = mensagem.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, porta);
                System.out.println("Enviando: " + mensagem);
                socket.send(packet);
                Thread.sleep(3000); // envia a cada 3 segundos
            }

        } catch (IOException e) {
            System.err.println("Erro de E/S: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Interrompido: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}

