package Conexoes.CentralServidorUni;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CentralParaServidor {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 22234;
    private static final int TIMEOUT = 5000;

    public void conexaoCentralServidor() {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in))
        ) {
            socket.setSoTimeout(TIMEOUT);

            String porta = in.readLine();

            System.out.println("Conectado ao servidor. " +
                    "Mensagem de boas-vindas: " + porta);

            int novaPorta = Integer.parseInt(porta);

            try (
                    Socket novoSocket = new Socket(SERVER_ADDRESS, novaPorta);
                    BufferedReader novoIn = new BufferedReader(new InputStreamReader(novoSocket.getInputStream()));
                    PrintWriter novoOut = new PrintWriter(novoSocket.getOutputStream(), true)
            ) {
                System.out.println("Conectado ao servidor na porta " + novaPorta);
                System.out.println("Mensagem do servidor: " + novoIn.readLine()); // Lê a saudação do novo servidor

                out.println("Central");

                String entrada;
                while ((entrada = console.readLine()) != null) {
                    novoOut.println(entrada);
                    System.out.println("Resposta do servidor: " + novoIn.readLine());
                }

            } catch (IOException e) {
                System.out.println("Erro ao conectar na nova porta: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.out.println("Erro na conexão inicial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CentralParaServidor().conexaoCentralServidor();
    }
}
