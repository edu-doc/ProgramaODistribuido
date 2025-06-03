package LoadBalance;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class UnicastThread implements Runnable {
    private final Socket socket;
    private final CopyOnWriteArrayList<ServerInfo> servidores;

    public UnicastThread(Socket socket, CopyOnWriteArrayList<ServerInfo> servidores) {
        this.socket = socket;
        this.servidores = servidores;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            if (servidores.isEmpty()) {
                out.println("Nenhum servidor disponível no momento.");
                return;
            }

            // Encontra o servidor com menos conexões
            ServerInfo melhorServidor = servidores
                    .stream()
                    .min((a, b) -> Integer.compare(a.getConexoesAtivas(), b.getConexoesAtivas()))
                    .orElse(null);

            if (melhorServidor != null) {
                out.println(melhorServidor.getPorta());
                System.out.println("[Unicast] Cliente redirecionado para porta: " + melhorServidor.getPorta());
            } else {
                out.println("Erro ao encontrar servidor.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
