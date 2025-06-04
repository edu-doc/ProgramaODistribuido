package Domain.Servidor;

import Domain.Banco.Banco;
import Domain.Model.Entity.Drone;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ServidorBanco {
    private static final int PORT = 55444;
    private static final int MAX_THREADS = 10;
    public static Banco<Drone> bancoDeDados = new Banco<>();


    public static void main(String[] args) {

        // Inicia o servidor TCP com pool de threads
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor TCP iniciado na porta " + PORT);

            while (true) {
                Socket socketServidorAplicacao = serverSocket.accept();
                System.out.println("Servidor conectado: " + socketServidorAplicacao.getInetAddress());
                pool.execute(new ImplServidorBanco(bancoDeDados, socketServidorAplicacao));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}
