package com.example.antonio.esercitazione6;

public class DettagliAccount {
    private String Nome;
    private String Cognome;
    private String Email;
    private String Residenza;

    public DettagliAccount() {

    }

    public DettagliAccount(String nome, String cognome, String email, String residenza) {
        this.Nome = nome;
        this.Cognome = cognome;
        this.Email = email;
        this.Residenza = residenza;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getCognome() {
        return Cognome;
    }

    public void setCognome(String cognome) {
        Cognome = cognome;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getResidenza() {
        return Residenza;
    }

    public void setResidenza(String residenza) {
        Residenza = residenza;
    }
}
