package com.progettoswe.model;
import java.time.LocalDate;

public class Prestito {
    private int id;
    private Utente utente;
    private Libro libro;
    private LocalDate dataInizioPrestito;
    private int numeroRinnovi;
    private boolean ritirato;
    private boolean restituito;
    private LocalDate dataInizioPrenotazione;

    public Prestito(int id, Utente utente, Libro libro) {
        this.id = id;
        this.utente = utente;
        this.libro = libro;
        this.dataInizioPrenotazione = LocalDate.now();
        this.numeroRinnovi = 0;
        this.ritirato = false;
        this.restituito = false;
    }

    public Prestito(int id, Utente utente, Libro libro, LocalDate dataInizioPrenotazione) {
        this.id = id;
        this.utente = utente;
        this.libro = libro;
        this.dataInizioPrenotazione = dataInizioPrenotazione;
        this.numeroRinnovi = 0;
        this.ritirato = false;
        this.restituito = false;
    }

    public Prestito(int id, Utente utente, Libro libro, LocalDate dataInizioPrenotazione, int numRinnovi, boolean restituito) {
        this.id = id;
        this.utente = utente;
        this.libro = libro;
        this.dataInizioPrenotazione = dataInizioPrenotazione;
        this.numeroRinnovi = numRinnovi;
        this.ritirato = false;
        this.restituito = restituito;
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

    public boolean isRestituito() {
        return restituito;
    }

    public void ritirato(){
        if(!ritirato && !restituito){
            ritirato = true;
            libro.setCopie(libro.getCopie() - 1);
            dataInizioPrestito = LocalDate.now();
        }
    }

    public void restituito(){
        if(ritirato && !restituito){
            restituito = true;
            libro.setCopie(libro.getCopie() + 1);
        }
    }

    //handle with exceptions
    public void rinnovaPrestito(){
        if(ritirato && !restituito && numeroRinnovi < 3){
            numeroRinnovi++;
        }
    }
}