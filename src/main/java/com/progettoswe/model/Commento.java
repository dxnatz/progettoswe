package com.progettoswe.model;

public class Commento {

    private String id_commento;
    private Prestito prestito;
    private String testo;

    //costruttore con id_commento (usato quando leggiamo i dati dal database)
    public Commento(String id_commento, Prestito prestito, String testo) {
        this.id_commento = id_commento;
        this.prestito = prestito;
        this.testo = testo;
    }

    //costruttore senza id_commento (usato quando creiamo un nuovo oggetto)
    public Commento(Prestito prestito, String testo) {
        this.prestito = prestito;
        this.testo = testo;
    }

    public String getId_commento() {
        return id_commento;
    }

    public Prestito getPrestito() {
        return prestito;
    }

    public String getTesto() {
        return testo;
    }
}