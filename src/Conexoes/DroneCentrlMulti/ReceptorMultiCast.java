package Conexoes.DroneCentrlMulti;

import API.DTO.DroneDTO;
import Domain.Service.CentralService;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

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
            System.out.println(formatarMensagem(mensagem));

        }

        ms.leaveGroup(grupo, interfaceRede);
        ms.close();
    }

    public static List<Double> formatarMensagem(String mensagem) {
        String separador = detectarSeparador(mensagem);

        if (separador == null) {
            System.out.println("Separador desconhecido.");
            return new ArrayList<>();
        }

        String[] partes = mensagem.split(java.util.regex.Pattern.quote(separador));
        List<Double> numeros = new ArrayList<>();

        for (String parte : partes) {
            try {
                numeros.add(Double.parseDouble(parte.trim()));
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido ignorado: " + parte);
            }
        }

        // Aqui você pode executar ações diferentes com base no separador detectado
        switch (separador) {
            case "-":
                CentralService centralService = new CentralService();
                DroneDTO droneDTO = new DroneDTO(numeros.get(0), numeros.get(1), numeros.get(2), numeros.get(3), numeros.get(4), numeros.get(5), "Norte");
                centralService.createDrone(droneDTO);
                System.out.println("Mensagem do drone do Norte");
                break;
            case ";":
                System.out.println("Mensagem do drone do Sul");
                break;
            case "#":
                System.out.println("Mensagem do drone do Oeste");
                break;
            case ",":
                System.out.println("Mensagem do drone do Leste");
                break;
        }

        return numeros;
    }

    private static String detectarSeparador(String mensagem) {
        if (mensagem.contains("-")) return "-";
        if (mensagem.contains(";")) return ";";
        if (mensagem.contains("#")) return "#";
        if (mensagem.contains(",")) return ",";
        return null;
    }

}
