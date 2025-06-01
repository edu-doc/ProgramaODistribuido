package Domain.Service;

import API.DTO.DroneDTO;
import Domain.Model.Entity.Drone;

public class CentralService {

    private Drone drone;

    public void createDrone(DroneDTO droneDTO) {
        this.drone =  new Drone(droneDTO.pressao(), droneDTO.radiacao(), droneDTO.temperatura(), droneDTO.umidade(), droneDTO.latitude(), droneDTO.longitude(), droneDTO.posicao());
        System.out.println(drone);
    }

    public Drone getDrone() {
        return drone;
    }

}
