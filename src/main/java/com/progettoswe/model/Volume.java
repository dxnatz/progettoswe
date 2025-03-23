package com.progettoswe.model;

public class Volume {

    private int id_volume;
    private Edizione edizione;
    private String stato;
    private String posizione;

    //costruttore con id_volume (usato quando leggiamo i dati dal database)
    public Volume(int id_volume, Edizione edizione, String stato, String posizione) {
        this.id_volume = id_volume;
        this.edizione = edizione;
        this.stato = stato;
        this.posizione = posizione;
    }

    //costruttore senza id_volume (usato quando creiamo un nuovo oggetto)
    public Volume(Edizione edizione, String stato, String posizione) {
        this.edizione = this.edizione;
        this.stato = stato;
        this.posizione = posizione;
    }

    public int getId_volume() {
        return id_volume;
    }

    public Edizione edizione() {
        return edizione;
    }

    public String getStato() {
        return stato;
    }

    public String getPosizione() {
        return posizione;
    }
}
