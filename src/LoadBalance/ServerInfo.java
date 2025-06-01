package LoadBalance;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Objects;

public class ServerInfo {
    private final String id;
    private final String host;
    private final int porta;
    private AtomicInteger conexoesAtivas;

    public ServerInfo(String id, String host, int porta) {
        this.id = id;
        this.host = host;
        this.porta = porta;
        this.conexoesAtivas = new AtomicInteger(0); // Inicia com 0
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPorta() {
        return porta;
    }

    public int getConexoesAtivas() {
        return conexoesAtivas.get();
    }

    public void setConexoesAtivas(int count) {
        if (count >= 0) {
            this.conexoesAtivas.set(count);
        } else {
            System.err.println("Tentativa de definir contagem de conexões negativa para " + id + ": " + count + ". Definindo para 0.");
            this.conexoesAtivas.set(0);
        }
    }

    public void incrementarConexoes() {
        conexoesAtivas.incrementAndGet();
    }

    public void decrementarConexoes() { // Exemplo
        conexoesAtivas.getAndUpdate(current -> current > 0 ? current - 1 : 0);
    }


    @Override
    public String toString() {
        return "ServerInfo{" +
                "id='" + id + '\'' +
                ", host='" + host + '\'' +
                ", porta=" + porta +
                ", conexoesAtivas=" + conexoesAtivas.get() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerInfo that = (ServerInfo) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}