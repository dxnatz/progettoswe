package com.progettoswe.model;
import java.time.LocalDate;

public class Prestito {
    private int id;
    private Utente utente;
    private Libro libro;
    private LocalDate dataInizioPrestito;
    private int numeroRinnovi;
    private boolean ritirato;
    private LocalDate dataInizioPrenotazione;

    public Prestito(int id, Utente utente, Libro libro) {
        this.id = id;
        this.utente = utente;
        this.libro = libro;
        this.dataInizioPrenotazione = LocalDate.now();
        this.numeroRinnovi = 0;
        this.ritirato = false;
    }

    public Prestito(int id, Utente utente, Libro libro, LocalDate dataInizioPrenotazione) {
        this.id = id;
        this.utente = utente;
        this.libro = libro;
        this.dataInizioPrenotazione = dataInizioPrenotazione;
        this.numeroRinnovi = 0;
        this.ritirato = false;
        }

    public Prestito(int id, Utente utente, Libro libro, LocalDate dataInizioPrenotazione, int numRinnovi) {
        this.id = id;
        this.utente = utente;
        this.libro = libro;
        this.dataInizioPrenotazione = dataInizioPrenotazione;
        this.numeroRinnovi = numRinnovi;
        this.ritirato = false;
    }

    public int getId() {
        return id;
    }

    public Utente getUtente() {
        return utente;
    }

    public Libro getLibro() {
        return libro;
    }

    public LocalDate getDataInizioPrestito() {
        return dataInizioPrestito;
    }

    public LocalDate getDataInizioPrenotazione() {
        return dataInizioPrenotazione;
    }

    public int getNumeroRinnovi() {
        return numeroRinnovi;
    }

    public boolean isRitirato() {
        return ritirato;
    }

    public void ritirato(){
        if(!ritirato){
            ritirato = true;
            libro.setCopie(libro.getCopie() - 1);
            dataInizioPrestito = LocalDate.now();
        }
    }

    public void restituito(){
        if(ritirato){
            libro.setCopie(libro.getCopie() + 1);
        }
    }

    //handle with exceptions
    public void rinnovaPrestito(){
        if(ritirato  && numeroRinnovi < 3){
            numeroRinnovi++;
        }
    }
}