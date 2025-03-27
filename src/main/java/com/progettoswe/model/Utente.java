package com.progettoswe.model;
import java.time.LocalDate;

public class Utente {
    private int id_utente;
    private String nome;
    private String cognome;
    private String cf;
    private String email;
    private String cellulare;
    private String password;
    private LocalDate dataNascita;
    private String indirizzo;
    private LocalDate dataRegistrazione;

    //costruttore con id_utente e dataRegistrazione (usato quando leggiamo i dati dal database)
    public Utente(int id_utente, String nome, String cognome, String cf, String email, String password, String cellulare, LocalDate dataNascita, String indirizzo, LocalDate dataRegistrazione) {
        this.id_utente = id_utente;
        this.nome = nome;
        this.cognome = cognome;
        this.cf = cf;
        this.email = email;
        this.cellulare = cellulare;
        this.password = password;
        this.dataNascita = dataNascita;
        this.indirizzo = indirizzo;
        this.dataRegistrazione = dataRegistrazione;
    }

    //costruttore senza id_utente e dataRegistrazione (usato quando creiamo un nuovo oggetto)
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

    public Utente(int id_utente, String nome, String cognome, String cf, String email, String cellulare, LocalDate dataNascita, String indirizzo) {
        this.id_utente = id_utente;
        this.nome = nome;
        this.cognome = cognome;
        this.cf = cf;
        this.email = email;

    }

    public int getId_utente() {
        return id_utente;
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

    public LocalDate getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}