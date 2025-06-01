package Conexoes.DroneCentrlMulti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class EmissorMultiCastLeste {

    public static void main(String[] args) {
        String apiUrl = "http://localhost:8080/api/sensores/dados/Leste"; // URL da API
        String multicastIp = "224.0.0.1";
        int porta = 55554;

        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName(multicastIp);
            socket.setTimeToLive(1); // Restringe à rede local

            while (true) {
                String mensagem = buscarDadosDaAPI(apiUrl);
                byte[] buffer = mensagem.getBytes();

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, porta);
                System.out.println("Enviando: " + mensagem);
                socket.send(packet);

                Thread.sleep(5000); // Espera 3 segundos antes do próximo envio
            }

        } catch (IOException e) {
            System.err.println("Erro de E/S: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Interrompido: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private static String buscarDadosDaAPI(String urlString) {
        StringBuilder resposta = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");

            if (conexao.getResponseCode() == 200) {
                try (BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()))) {
                    String linha;
                    while ((linha = leitor.readLine()) != null) {
                        resposta.append(linha);
                    }
                }
            } else {
                resposta.append("{\"erro\":\"Falha ao obter dados da API\"}");
            }

            conexao.disconnect();
        } catch (IOException e) {
            resposta.append("{\"erro\":\"").append(e.getMessage()).append("\"}");
        }

        return resposta.toString();
    }
}
