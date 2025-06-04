package Domain.Servidor;

import Domain.Model.Entity.Drone;
import Domain.Service.ServidorService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ImplServidorBanco extends UnicastRemoteObject implements BancoRemoto {
    private final ServidorService servidorService;
    private final ConcurrentHashMap<Integer, Drone> drones;

    public ImplServidorBanco() throws RemoteException {
        super();
        this.servidorService = new ServidorService();
        this.drones = new ConcurrentHashMap<>();
    }

    @Override
    public void adicionarDrone(Drone drone) throws RemoteException {
        drones.put(drone.getId(), drone);
        servidorService.addDrone(drone);
    }

    @Override
    public List<Drone> listarTodosDrones() throws RemoteException {
        return List.copyOf(drones.values());
    }

    @Override
    public List<Drone> getDronePosicao(String posicao) throws RemoteException {
        return servidorService.getDronePosicao(posicao);
    }

    @Override
    public List<Drone> getDroneData(String data) throws RemoteException {
        return servidorService.getDroneData(data);
    }

    @Override
    public List<Drone> getDronePosicaoData(String posicao, String data) throws RemoteException {
        return servidorService.getDronePosicaoData(posicao, data);
    }
}
