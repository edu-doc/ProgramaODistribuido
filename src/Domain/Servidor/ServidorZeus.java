package Domain.Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorZeus {

    private static final int PORT = 12345;
    private static final int MAX_THREADS = 10;

    public static void main(String[] args) {

        // Inicia o serviço de multicast
        try {
            ImplServidor implServidor = new ImplServidor("S1", "224.0.0.10", 55560);
            Thread multicastThread = new Thread(implServidor);
            multicastThread.start();
            System.out.println("Thread multicast iniciada.");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar multicast: " + e.getMessage());
        }

        // Inicia o servidor TCP com pool de threads
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor TCP iniciado na porta " + PORT);

            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Cliente conectado: " + socketCliente.getInetAddress());
                pool.execute(new ImplCliente(socketCliente));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}
