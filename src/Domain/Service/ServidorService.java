package Domain.Service;

import Domain.Model.Entity.Drone;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorService {

    private static ConcurrentHashMap<Integer, Drone> droneMap = new ConcurrentHashMap<>();

    public void addDrone(Drone drone){
        droneMap.put(drone.getId(), drone);
        System.out.println("Drone adicionado: " + drone.getPosicao() + " - " + drone.getDataCriacao());
    }

    public List<Drone> getDronePosicao(String posicao) {
        return droneMap.values().stream()
                .filter(drone -> drone.getPosicao().equals(posicao))
                .toList();
    }


    public List<Drone> getDroneData(LocalDateTime data) {
        return droneMap.values().stream()
                .filter(drone -> drone.getDataCriacao().toLocalDate().equals(data.toLocalDate()))
                .toList();
    }

    public List<Drone> getDronePosicaoData(String posicao, LocalDateTime data) {
        return droneMap.values().stream()
                .filter(drone -> drone.getPosicao().equals(posicao) &&
                        drone.getDataCriacao().toLocalDate().equals(data.toLocalDate()))
                .toList();
    }



}
