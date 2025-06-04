package Domain.Model.Entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Drone implements Serializable {

    private static int count = 0;
    private int id;
    private double pressao;
    private double radiacao;
    private double temperatura;
    private double umidade;
    private double latitude;
    private double longitude;
    private String posicao;
    private LocalDate dataCriacao;


    public Drone(double pressao, double radiacao, double temperatura, double umidade, double latitude, double longitude, String posicao) {
        setId(getNextId());
        setPressao(pressao);
        setRadiacao(radiacao);
        setTemperatura(temperatura);
        setUmidade(umidade);
        setLatitude(latitude);
        setLongitude(longitude);
        setPosicao(posicao);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        setDataCriacao(LocalDate.parse(formatter.format(LocalDate.now()), formatter));

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getNextId() {
        return ++count;
    }

    public String getPosicao() {
        return posicao;
    }

    public void setPosicao(String posicao) {
        this.posicao = posicao;
    }

    public double getPressao() {
        return pressao;
    }

    public void setPressao(double pressao) {
        this.pressao = pressao;
    }

    public double getRadiacao() {
        return radiacao;
    }

    public void setRadiacao(double radiacao) {
        this.radiacao = radiacao;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public double getUmidade() {
        return umidade;
    }

    public void setUmidade(double umidade) {
        this.umidade = umidade;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public String toString() {
        return "Drone{" +
                "pressao=" + pressao +
                ", radiacao=" + radiacao +
                ", temperatura=" + temperatura +
                ", umidade=" + umidade +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", posicao='" + posicao + '\'' +
                ", dataCriacao='" + dataCriacao + '\'' +
                '}';
    }
}
