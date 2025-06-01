package Conexoes.DroneCentrlMulti;

import java.io.IOException;
import java.net.*;

public class ReceptorMultiCast {
    public static void main(String[] args) throws IOException {
        int porta = 55554;
        String mensagem = "";

        MulticastSocket ms = new MulticastSocket(porta);
        InetAddress multicastIP = InetAddress.getByName("224.0.0.1");
        InetSocketAddress grupo = new InetSocketAddress(multicastIP, porta);
        NetworkInterface interfaceRede = NetworkInterface.getByName("wlo1");

        ms.joinGroup(grupo, interfaceRede);

        System.out.println("Receptor contínuo ouvindo em " + multicastIP + ":" + porta);

        byte[] buffer = new byte[1024];
        DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);

        while (!mensagem.equals("sair")) {
            ms.receive(pacote);
            mensagem = new String(pacote.getData(), 0, pacote.getLength());
            System.out.println("[" + pacote.getAddress() + "] -> " + mensagem);
        }

        ms.leaveGroup(grupo, interfaceRede);
        ms.close();
    }
}
