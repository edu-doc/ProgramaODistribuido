package Domain.Service;

import Domain.Model.Entity.Drone;

import java.util.concurrent.ConcurrentHashMap;

public class ServidorService {

    private static ConcurrentHashMap<String, Drone> droneMap = new ConcurrentHashMap<>();
    private CentralService CentralService = new CentralService();


}
