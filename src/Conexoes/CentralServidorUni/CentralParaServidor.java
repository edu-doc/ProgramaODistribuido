package Conexoes.CentralServidorUni;

import Domain.Model.Entity.Drone;

import java.io.*;
import java.net.Socket;

public class CentralParaServidor {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 22234;
    private static final int TIMEOUT = 5000;

    public void conexaoCentralServidor(Drone drone) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in))
        ) {
            socket.setSoTimeout(TIMEOUT);

            System.out.println("Conectando com Load Balancer na porta " + SERVER_PORT);

            String porta = in.readLine();

            System.out.println("Load Balancer retornou a porta: " + porta);

            int novaPorta = Integer.parseInt(porta);

            try (
                    Socket novoSocket = new Socket(SERVER_ADDRESS, novaPorta);
                    ObjectOutputStream objOut = new ObjectOutputStream(novoSocket.getOutputStream());
                    ObjectInputStream objIn = new ObjectInputStream(novoSocket.getInputStream());
            ) {
                System.out.println("Redirecionado para o servidor na porta " + novaPorta);

                objOut.writeUTF("Central");
                objOut.writeObject(drone);
                objOut.flush();

            } catch (IOException e) {
                System.out.println("Erro ao conectar na nova porta: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.out.println("Erro na conexão inicial: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
