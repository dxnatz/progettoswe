package com.progettoswe.model;
import java.time.LocalDate;
import java.util.ArrayList;

public class Utente {
    private int id;
    private String nome;
    private String cognome;
    private String cf;
    private String email;
    private String cellulare;
    private String password;
    private LocalDate dataNascita;
    private String indirizzo;
    private ArrayList<Libro> preferiti;
    private ArrayList<Prestito> prestiti;

    public Utente(String nome, String cognome, String cf, String email, String cellulare, String password, LocalDate dataNascita, String indirizzo) {
        this.nome = nome;
        this.cognome = cognome;
        this.cf = cf;
        this.email = email;
        this.cellulare = cellulare;
        this.password = password;
        this.dataNascita = dataNascita;
        this.indirizzo = indirizzo;
        this.preferiti = new ArrayList<Libro>();
        this.prestiti = new ArrayList<Prestito>();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getEmail() {
        return email;
    }

    public String getCellulare() {
        return cellulare;
    }

    public String getPassword() {
        return password;
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

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public String getCodiceFiscale() {
        return cf;
    }

    public String getIndirizzo() {
        return indirizzo;
    }
}