package com.example.hidoctor;

import java.io.Serializable;

public class Parameters implements Serializable {
    private String temperatura;
    private String pressione;

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getPressione() {
        return pressione;
    }

    public void setPressione(String pressione) {
        this.pressione = pressione;
    }
}
