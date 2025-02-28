package com.progettoswe;
import java.util.ArrayList;

public class Utente {
    private int id;
    private String nome;
    private String cognome;
    private String email;
    private String cellulare;
    private String password;
    private ArrayList<Libro> preferiti;
    private ArrayList<Prestito> prestiti;

    public Utente(int id, String nome, String cognome, String email, String cellulare, String password) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.cellulare = cellulare;
        this.password = password;
        this.preferiti = new ArrayList<Libro>();
        this.prestiti = new ArrayList<Prestito>();
    }

    public void aggiungiPreferito(Libro libro){
        if(!preferiti.contains(libro)){
            preferiti.add(libro);
        }
    }

    public void aggiungiPrestito(Prestito prestito){
        prestiti.add(prestito);
    }

    public void rimuoviLibroDaPreferiti(Libro libro) {
        preferiti.remove(libro);
    }

    public ArrayList<Libro> getListaPreferiti() {
        return preferiti;
    }

    public ArrayList<Prestito> getPrestiti() {
        return prestiti;
    }

}