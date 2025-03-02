package com.progettoswe;
import java.util.Date;
import java.util.Calendar;

public class Prestito {
    private int id;
    private Utente utente;
    private Libro libro;
    private Date dataPrenotazione;
    private Date scadenzaPrestito;
    private int numeroRinnovi;
    private boolean ritirato;
    private boolean restituito;
    private Date scadenzaRitiro;
    private Date dataRitiro;

    public Prestito(int id, Utente utente, Libro libro, Date dataPrenotazione) {
        this.id = id;
        this.utente = utente;
        this.libro = libro;
        this.dataPrenotazione = dataPrenotazione;
        this.scadenzaPrestito = null;
        this.numeroRinnovi = 0;
        this.ritirato = false;
        this.restituito = false;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataPrenotazione);
        calendar.add(Calendar.DAY_OF_YEAR, 7); //user has 7 days to pick up the book from the library
        this.scadenzaRitiro = calendar.getTime();

        this.dataRitiro = null;
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

    public Date getDataPrenotazione() {
        return dataPrenotazione;
    }

    public Date getScadenzaPrestito() {
        return scadenzaPrestito;
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

    public Date getScadenzaRitiro() {
        return scadenzaRitiro;
    }

    public Date getDataRitiro() {
        return dataRitiro;
    }

    public void ritirato(){
        if(!ritirato){
            dataRitiro = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dataRitiro);
            calendar.add(Calendar.DAY_OF_YEAR, 30); //user has 30 days to return the book to the library
            scadenzaPrestito = calendar.getTime();

            ritirato = true;
            libro.setDisponibile(false);
        }
    }

    public void restituito(){
        if(ritirato && !restituito){
            restituito = true;
            libro.setDisponibile(true);
        }
    }

    //handle with exceptions
    public void rinnovaPrestito(){
        if(ritirato && !restituito && numeroRinnovi < 3){
            numeroRinnovi++;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(scadenzaPrestito);
            calendar.add(Calendar.DAY_OF_YEAR, 30); // add 30 days to the current scadenzaPrestito
            scadenzaPrestito = calendar.getTime();

        }
    }
}