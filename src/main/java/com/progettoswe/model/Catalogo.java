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


}