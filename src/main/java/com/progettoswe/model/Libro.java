package com.progettoswe.model;

public class Libro {

    //TODO: classe da cancellare

    public static final int ISBN_MAX_LENGTH = 13;
    public static final int TITOLO_MAX_LENGTH = 100;
    public static final int AUTORE_MAX_LENGTH = 50;
    public static final int EDITORE_MAX_LENGTH = 20;
    public static final int GENERE_MAX_LENGTH = 25;
    public static final int MINIMUM_COPIE = 0;

    private String isbn;
    private String titolo;
    private String autore;
    private String editore;
    private int annoPubblicazione;
    private String genere;
    private int copie;

    public Libro(String isbn, String titolo, String autore, String editore, int annoPubblicazione, String genere, int copie) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.autore = autore;
        this.genere = genere;
        this.editore = editore;
        this.annoPubblicazione = annoPubblicazione;
        this.copie = copie;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getAutore() {
        return autore;
    }

    public String getGenere() {
        return genere;
    }

    public String getEditore() {
        return editore;
    }

    public int getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public int getCopie() {
        return copie;
    }

    public void setCopie(int copie) {
        this.copie = copie;
    }

    @Override
    public String toString() {
        return titolo + " " + autore + " " + editore + " " + annoPubblicazione + "\n";
    }
}