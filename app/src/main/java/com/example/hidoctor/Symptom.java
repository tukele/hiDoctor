package com.example.hidoctor;

public class Symptom {
   public String nome;
   public String value;

    public Symptom(String nome, String value) {
        this.nome = nome;
        this.value = value;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
