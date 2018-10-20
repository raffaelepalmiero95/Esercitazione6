package com.example.antonio.esercitazione6;

public class DettaglioSegnalazione {
    private double Latitudine;
    private double Longitudine;
    private String Descrizione_Problema;
    private String URL;
    //20 ottobre
    private String Data_Ora;
    //

    public DettaglioSegnalazione() {

    }

    public DettaglioSegnalazione(String problema, double latitudine, double longitudine, String URL,String data_Ora) {
       this.Latitudine = latitudine;
        this.Longitudine = longitudine;
        this.Descrizione_Problema = problema;
        this.URL = URL;
        //20 ottobre
        this.Data_Ora = data_Ora;
        //
    }

//20 ottobre
    public String getData_Ora() {
        return Data_Ora;
    }

    public void setData_Ora(String data_Ora) {
        Data_Ora = data_Ora;
    }
    //

    public double getLatitudine() {
        return Latitudine;
    }

    public double getLongitudine() {
        return Longitudine;
    }

    public String getDescrizione_Problema() {
        return Descrizione_Problema;
    }

    public String getURL() {
        return URL;
    }

    public void setLatitudine(float latitudine) {
        Latitudine = latitudine;
    }

    public void setLongitudine(float longitudine) {
        Longitudine = longitudine;
    }

    public void setDescrizione_Problema(String problema) {
        Descrizione_Problema = problema;
    }

    //if perchè altrimenti senza url crasha l'app
    public void setURL(String URL) {
        if (URL != null) {
            this.URL = URL;
        }
        else {
            this.URL = " ";
        }
    }
}
