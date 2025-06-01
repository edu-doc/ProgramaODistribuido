package LoadBalance;

import java.io.IOException;
import java.util.NoSuchElementException;

public class MainLoadBalancer {

    public static void main(String[] args) throws InterruptedException, IOException {

        ImplLoadBalancer loadBalancer = new ImplLoadBalancer("224.0.0.10",55560);

        ServerInfo srvInfo1 = new ServerInfo("S1", "10.0.0.1", 8080);
        ServerInfo srvInfo2 = new ServerInfo("S2", "10.0.0.2", 8081);

        loadBalancer.adicionarServidor(srvInfo1);
        loadBalancer.adicionarServidor(srvInfo2);

        System.out.println("Balanceador de Carga iniciado. Aguardando atualizações dos servidores...");
        loadBalancer.imprimirStatusServidores();

        Thread.sleep(2000); // Permite tempo para atualizações iniciais
        loadBalancer.imprimirStatusServidores();

        System.out.println("\n>>> Iniciando simulação de requisições de clientes <<<\n");
        for (int i = 0; i < 15; i++) {
            System.out.println("Requisição de cliente (" + (i + 1) + ") chegou.");
            ServerInfo servidorEscolhido = null;
            try {
                servidorEscolhido = loadBalancer.getProximoServidor();
                System.out.println("Balanceador direcionou requisição para: " + servidorEscolhido.getId() +
                        " (conexões reportadas atualmente: " + servidorEscolhido.getConexoesAtivas() + ")");

            } catch (NoSuchElementException e) {
                System.err.println("Erro: Nenhum servidor disponível: " + e.getMessage());
                break;
            }

            Thread.sleep(1000);

            loadBalancer.imprimirStatusServidores();
        }

        System.out.println("\n>>> Simulação de Requisições de Clientes Finalizada <<<\n");
        loadBalancer.imprimirStatusServidores();

        System.out.println("\nEncerrando Balanceador de Carga...");
        loadBalancer.shutdown();

        System.out.println("Simulação MainLoadBalancer concluída.");
    }
}