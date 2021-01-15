package com.example.hidoctor;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String nome;
    private String cognome;
    private String nomeMedico;
    public static User currentUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getNomeMedico() {
        return nomeMedico;
    }

    public void setNomeMedico(String nomeMedico){
        this.nomeMedico=nomeMedico;
    }

}
