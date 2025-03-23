package com.progettoswe.model;

public class Edizione {

    private int id_edizione;
    private Opera opera;
    private String editore;
    private int annoPubblicazione;
    private int isbn;

    //costruttore con id_edizione (usato quando leggiamo i dati dal database)
    public Edizione(int isbn, int annoPubblicazione, String editore, Opera opera, int id_edizione) {
        this.isbn = isbn;
        this.annoPubblicazione = annoPubblicazione;
        this.editore = editore;
        this.opera = opera;
        this.id_edizione = id_edizione;
    }

    //costruttore senza id_edizione (usato quando creiamo un nuovo oggetto)
    public Edizione(Opera opera, String editore, int annoPubblicazione, int isbn) {
        this.opera = opera;
        this.editore = editore;
        this.annoPubblicazione = annoPubblicazione;
        this.isbn = isbn;
    }

    public int getId_edizione() {
        return id_edizione;
    }

    public String getEditore() {
        return editore;
    }

    public Opera getOpera() {
        return opera;
    }

    public int getIsbn() {
        return isbn;
    }

    public int getAnnoPubblicazione() {
        return annoPubblicazione;
    }
}
