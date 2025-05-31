package Domain.Service;

import Domain.Model.Entity.Drone;
import Domain.Utility.DroneKey;

import java.util.concurrent.ConcurrentHashMap;

public class ServidorService {

    private static ConcurrentHashMap<DroneKey, Drone> droneMap = new ConcurrentHashMap<>();
    private CentralService CentralService = new CentralService();

    public void createBank() {
        Drone drone = CentralService.getDrone();
        DroneKey key = new DroneKey(drone.getId(), drone.getGroupId());
        droneMap.put(key, drone);
    }

    public Drone dadosDrone(int id, int groupId){
        return droneMap.get(new DroneKey(id, groupId));
    }

}
