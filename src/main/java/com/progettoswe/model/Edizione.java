package com.progettoswe.model;

public class Edizione {

    private int id_edizione;
    private Opera opera;
    private int numero;
    private String editore;
    private int annoPubblicazione;
    private String isbn;

    //costruttore con id_edizione (usato quando leggiamo i dati dal database)
    public Edizione(String isbn, int annoPubblicazione, String editore, int numero, Opera opera, int id_edizione) {
        this.isbn = isbn;
        this.annoPubblicazione = annoPubblicazione;
        this.editore = editore;
        this.numero = numero;
        this.opera = opera;
        this.id_edizione = id_edizione;
    }

    //costruttore senza id_edizione (usato quando creiamo un nuovo oggetto)
    public Edizione(int numero, Opera opera, String editore, int annoPubblicazione, String isbn) {
        this.numero = numero;
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

    public String getIsbn() {
        return isbn;
    }

    public int getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public int getNumero() {
        return numero;
    }
}
