package Domain.Service;

import API.DTO.DroneDTO;
import Domain.Servidor.CentralParaServidor;
import Domain.Model.Entity.Drone;

public class CentralService {

    private Drone drone;

    public void createDrone(DroneDTO droneDTO) {
        this.drone =  new Drone(droneDTO.co2(),droneDTO.co(),droneDTO.no2(),droneDTO.so2(),droneDTO.pm2_5(),droneDTO.pm10(),droneDTO.temperatura(),
                droneDTO.umidade(),droneDTO.ruido(),droneDTO.radiacao(),droneDTO.latitude(),droneDTO.longitude(),droneDTO.posicao());
        CentralParaServidor centralParaServidor = new CentralParaServidor();
        centralParaServidor.conexaoCentralServidor(drone);
    }


}
