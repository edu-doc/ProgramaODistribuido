package Domain.Model.Entity;

public class Drone {

    private static int count = 0;
    private static int groupCount = 0;
    private static String posicoes;
    private int id;
    private int groupId;
    private String posicao;
    private int pressao;
    private int radiacao;
    private int temperatura;
    private int umidade;

    public Drone(int pressao, int radiacao, int temperatura, int umidade) {
        setId(getCount());
        setGroupId(getGroupCount());
        setPosicao(getPosicoes());
        setPressao(pressao);
        setRadiacao(radiacao);
        setTemperatura(temperatura);
        setUmidade(umidade);
    }

    public int getCount() {
        count++;
        if (count > 4) {
            count = 1;
        }
        return count;
    }

    public int getGroupCount() {
        if (getId() == 1) {
            groupCount++;
        }
        return groupCount;
    }

    public String getPosicoes() {

        if (getId() == 1) {
            posicoes = "Norte";
        } else if (getId() == 2) {
            posicoes = "Sul";
        } else if (getId() == 3) {
            posicoes = "Leste";
        } else if (getId() == 4) {
            posicoes = "Oeste";
        } else {
            posicoes = "Indefinida";
        }

        return posicoes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getPosicao() {
        return posicao;
    }

    public void setPosicao(String posicao) {
        this.posicao = posicao;
    }

    public int getPressao() {
        return pressao;
    }

    public void setPressao(int pressao) {
        this.pressao = pressao;
    }

    public int getRadiacao() {
        return radiacao;
    }

    public void setRadiacao(int radiacao) {
        this.radiacao = radiacao;
    }

    public int getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public int getUmidade() {
        return umidade;
    }

    public void setUmidade(int umidade) {
        this.umidade = umidade;
    }

    @Override
    public String toString() {

        if (getId() == 1) {
            return getPressao() + "-" + getRadiacao() + "-" + getTemperatura() + "-" + getUmidade();
        } else if (getId() == 2) {
            return getPressao() + ";" + getRadiacao() + ";" + getTemperatura() + ";" + getUmidade();
        } else if (getId() == 3) {
            return getPressao() + "," + getRadiacao() + "," + getTemperatura() + "," + getUmidade();
        } else if (getId() == 4) {
            return getPressao() + "#" + getRadiacao() + "#" + getTemperatura() + "#" + getUmidade();
        } else {
            return "Indefinido";
        }

    }

}
