package Conexoes.ClienteServidorUni;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClienteParaServidor {
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

            System.out.println("Conectando ao Load Balancer na porta " + SERVER_PORT);

            String porta = in.readLine();

            System.out.println("Load Balancer retornou a porta: " + porta);

            int novaPorta = Integer.parseInt(porta);

            try (
                    Socket novoSocket = new Socket(SERVER_ADDRESS, novaPorta);
                    ObjectOutputStream objOut = new ObjectOutputStream(novoSocket.getOutputStream());
                    ObjectInputStream objIn = new ObjectInputStream(novoSocket.getInputStream());
            ) {
                novoSocket.setSoTimeout(TIMEOUT);
                System.out.println("Redirecionado para o servidor na porta " + novaPorta);

                objOut.writeUTF("Cliente");
                objOut.flush();

                Object resposta = objIn.readObject();
                if (resposta instanceof List<?> lista) {
                    System.out.println("Lista de drones recebida:");
                    for (Object o : lista) {
                        System.out.println(o); // Drone precisa ter toString() bem definido
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro ao conectar na nova porta: " + e.getMessage());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            System.out.println("Erro na conexão inicial: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ClienteParaServidor().conexaoCentralServidor();
    }
}

