package com.progettoswe.model;

public class Commento {

    private int id_commento;
    private Utente utente;
    private Edizione edizione;
    private Prestito prestito;
    private String testo;

    //costruttore con id_commento (usato quando leggiamo i dati dal database)
    public Commento(int id_commento, Utente utente, Edizione edizione, Prestito prestito, String testo) {
        this.id_commento = id_commento;
        this.utente = utente;
        this.edizione = edizione;
        this.prestito = prestito;
        this.testo = testo;
    }

    public Commento(int id_commento, Utente utente, Edizione edizione, String testo) {
        this.id_commento = id_commento;
        this.utente = utente;
        this.edizione = edizione;
        this.prestito = null;
        this.testo = testo;
    }

    //costruttore senza id_commento (usato quando creiamo un nuovo oggetto)
    public Commento(Utente utente, Edizione edizione, Prestito prestito, String testo) {
        this.utente = utente;
        this.edizione = edizione;
        this.prestito = prestito;
        this.testo = testo;
    }

    public int getId_commento() {
        return id_commento;
    }

    public Prestito getPrestito() {
        return prestito;
    }

    public String getTesto() {
        return testo;
    }

    public Utente getUtente() {
        return utente;
    }

    public Edizione getEdizione() {
        return edizione;
    }
}