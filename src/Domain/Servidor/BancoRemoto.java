package Domain.Servidor;

import Domain.Model.Entity.Drone;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BancoRemoto extends Remote {
    void adicionarDrone(Drone drone) throws RemoteException;
    List<Drone> listarTodosDrones() throws RemoteException;
    List<Drone> getDronePosicao(String posicao) throws RemoteException;
    List<Drone> getDroneData(String Data) throws RemoteException;
    List<Drone> getDronePosicaoData(String posicao, String Data) throws RemoteException;
}
