package com.progettoswe.model;
import java.util.ArrayList;

public class Catalogo {
    private ArrayList<Libro> libri;

    public Catalogo() {
        this.libri = new ArrayList<Libro>();
    }

    public void aggiungiLibro(Libro libro) {
        libri.add(libro); //it is possible to add multiple copies of the same book
    }

    public void rimuoviLibro(Libro libro) {
        libri.remove(libro);
    }

    public ArrayList<Libro> getLibri() {
        return libri;
    }

    //ricerca libro o per titolo o per autore
    public ArrayList<Libro> ricercaLibro(String ricerca) {
        ArrayList<Libro> risultati = new ArrayList<Libro>();
        for (Libro libro : libri) {
            if (libro.toString().toLowerCase().contains(ricerca.toLowerCase())) {
                risultati.add(libro);
            }
        }
        return risultati;
    }
}