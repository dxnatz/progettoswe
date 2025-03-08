package com.progettoswe.model;
import java.time.LocalDate;

public class Utente {
    private int codice;
    private String nome;
    private String cognome;
    private String cf;
    private String email;
    private String cellulare;
    private String password;
    private LocalDate dataNascita;
    private String indirizzo;

    public Utente(int codice, String nome, String cognome, String cf, String email, String password, String cellulare, LocalDate dataNascita, String indirizzo) {
        this.codice = codice;
        this.nome = nome;
        this.cognome = cognome;
        this.cf = cf;
        this.email = email;
        this.cellulare = cellulare;
        this.password = password;
        this.dataNascita = dataNascita;
        this.indirizzo = indirizzo;
    }

    public Utente(String nome, String cognome, String cf, String email, String password, String cellulare, LocalDate dataNascita, String indirizzo) {
        this.nome = nome;
        this.cognome = cognome;
        this.cf = cf;
        this.email = email;
        this.cellulare = cellulare;
        this.password = password;
        this.dataNascita = dataNascita;
        this.indirizzo = indirizzo;
    }

    public int getCodice() {
        return codice;
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