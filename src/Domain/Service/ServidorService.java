package Domain.Service;

import Domain.Model.Entity.Drone;

import java.util.concurrent.ConcurrentHashMap;

public class ServidorService {

    private static ConcurrentHashMap<String, Drone> droneMap = new ConcurrentHashMap<>();
    private CentralService CentralService = new CentralService();

    public void createBank() {
        Drone drone = CentralService.getDrone();
        droneMap.put(drone.getPosicao(), drone);
    }

    public Drone dadosDrone(String posicao) {
        return droneMap.get(posicao);
    }

}
