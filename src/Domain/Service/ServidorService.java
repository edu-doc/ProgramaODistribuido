package Domain.Service;

import Domain.Model.Entity.Drone;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServidorService {
    private final List<Drone> drones;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ServidorService() {
        this.drones = new ArrayList<>();
    }

    public void addDrone(Drone drone) {
        drones.add(drone);
    }

    public List<Drone> getDronePosicao(String posicao) {
        return drones.stream()
                .filter(drone -> drone.getPosicao().equals(posicao))
                .collect(Collectors.toList());
    }

    public List<Drone> getDroneData(String data) {
        try {
            LocalDate dataBusca = LocalDate.parse(data, formatter);
            return drones.stream()
                    .filter(drone -> drone.getDataCriacao().equals(dataBusca))
                    .collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            System.err.println("Erro ao processar data: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Drone> getDronePosicaoData(String posicao, String data) {
        try {
            LocalDate dataBusca = LocalDate.parse(data, formatter);
            System.out.println("Buscando drones na posição " + posicao + " e data " + dataBusca);
            
            List<Drone> resultado = drones.stream()
                    .filter(drone -> {
                        boolean posicaoMatch = drone.getPosicao().equals(posicao);
                        boolean dataMatch = drone.getDataCriacao().equals(dataBusca);
                        System.out.println("Drone: " + drone + " - Posição match: " + posicaoMatch + ", Data match: " + dataMatch);
                        return posicaoMatch && dataMatch;
                    })
                    .collect(Collectors.toList());
            
            System.out.println("Total de drones encontrados: " + resultado.size());
            return resultado;
        } catch (DateTimeParseException e) {
            System.err.println("Erro ao processar data: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Drone> getAllDrones() {
        return new ArrayList<>(drones);
    }
} 