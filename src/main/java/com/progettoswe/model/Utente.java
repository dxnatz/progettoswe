package com.progettoswe.model;
import java.time.LocalDate;
import java.util.ArrayList;

public class Utente {
    private String nome;
    private String cognome;
    private String codiceFiscale;
    private String email;
    private String password;
    private String cellulare;
    private LocalDate dataNascita;
    private String indirizzo;
    private ArrayList<Prestito> prestiti;

    public Utente(String nome, String cognome, String codiceFiscale, String email, String password, String cellulare, LocalDate dataNascita, String indirizzo) {
        this.nome = nome;
        this.cognome = cognome;
        this.codiceFiscale = codiceFiscale;
        this.email = email;
        this.password = password;
        this.cellulare = cellulare;
        this.dataNascita = dataNascita;
        this.indirizzo = indirizzo;
        this.prestiti = new ArrayList<Prestito>();
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
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

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public ArrayList<Prestito> getPrestiti() {
        return prestiti;
    }

}