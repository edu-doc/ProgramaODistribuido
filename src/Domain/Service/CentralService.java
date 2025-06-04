package Domain.Service;

import API.DTO.DroneDTO;
import Domain.Servidor.CentralParaServidor;
import Domain.Model.Entity.Drone;

public class CentralService {

    private Drone drone;

    public void createDrone(DroneDTO droneDTO) {
        this.drone =  new Drone(droneDTO.pressao(), droneDTO.radiacao(), droneDTO.temperatura(), droneDTO.umidade(), droneDTO.latitude(), droneDTO.longitude(), droneDTO.posicao());
        CentralParaServidor centralParaServidor = new CentralParaServidor();
        centralParaServidor.conexaoCentralServidor(drone);
    }


}
