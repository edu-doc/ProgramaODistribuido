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
        NetworkInterface interfaceRede = NetworkInterface.getByName("wireless_32768");

        ms.joinGroup(grupo, interfaceRede);

        System.out.println("Receptor contínuo ouvindo em " + multicastIP + ":" + porta);

        byte[] buffer = new byte[1024];
        DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);

        while (!mensagem.equals("sair")) {
            ms.receive(pacote);
            mensagem = new String(pacote.getData(), 0, pacote.getLength());
            formatarMensagem(mensagem);

        }

        ms.leaveGroup(grupo, interfaceRede);
        ms.close();
    }

    static CentralService centralService = new CentralService();
    static DroneDTO droneDTO;
    static List<Double> numeros;

    public static void formatarMensagem(String mensagem) {
        String separador = detectarSeparador(mensagem);

        if (separador == null) {
            System.out.println("Separador desconhecido.");
        }

        String[] partes = mensagem.split(java.util.regex.Pattern.quote(separador));
        numeros = new ArrayList<>();
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
                droneDTO = new DroneDTO(numeros.get(0), numeros.get(1), numeros.get(2), numeros.get(3), numeros.get(4), numeros.get(5), "Norte");
                centralService.createDrone(droneDTO);
                numeros.clear();
                System.out.println("Mensagem do drone do Norte");
                break;
            case ";":
                droneDTO = new DroneDTO(numeros.get(0), numeros.get(1), numeros.get(2), numeros.get(3), numeros.get(4), numeros.get(5), "Sul");
                centralService.createDrone(droneDTO);
                numeros.clear();
                System.out.println("Mensagem do drone do Sul");
                break;
            case "#":
                droneDTO = new DroneDTO(numeros.get(0), numeros.get(1), numeros.get(2), numeros.get(3), numeros.get(4), numeros.get(5), "Oeste");
                centralService.createDrone(droneDTO);
                numeros.clear();
                System.out.println("Mensagem do drone do Oeste");
                break;
            case ",":
                droneDTO = new DroneDTO(numeros.get(0), numeros.get(1), numeros.get(2), numeros.get(3), numeros.get(4), numeros.get(5), "Leste");
                centralService.createDrone(droneDTO);
                numeros.clear();
                System.out.println("Mensagem do drone do Leste");
                break;

            default:
                System.out.println("Separador desconhecido: " + separador);
                break;
        }

    }

    private static String detectarSeparador(String mensagem) {
        if (mensagem.contains("-")) return "-";
        if (mensagem.contains(";")) return ";";
        if (mensagem.contains("#")) return "#";
        if (mensagem.contains(",")) return ",";
        return null;
    }

}
