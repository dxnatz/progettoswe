package com.progettoswe.model;

public class Libro {
    private int isbn;
    private String titolo;
    private String autore;
    private String genere;
    private String editore;
    private int annoPubblicazione;
    private int stato;
    private float prezzo;
    private boolean disponibile;

    public Libro(int isbn, String titolo, String autore, String genere, String editore, int annoPubblicazione, float prezzo) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.autore = autore;
        this.genere = genere;
        this.editore = editore;
        this.annoPubblicazione = annoPubblicazione;
        this.stato = 0; //a new book is in new state by default and is set to zero
        this.prezzo = prezzo;
        this.disponibile = true; //a new book is available by default
    }

    public int getIsbn() {
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

    public int getStato() {
        return stato;
    }

    public float getPrezzo() {
        return prezzo;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

    public void setDisponibile(boolean disponibile) {
        this.disponibile = disponibile;
    }

    @Override
    public String toString() {
        return titolo + " " + autore + " " + editore + " " + annoPubblicazione + "\n";
    }
}