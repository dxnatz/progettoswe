package com.progettoswe.model;
import com.progettoswe.ORM.LoanDAO;

import java.time.LocalDate;

public class Prestito {
    private int id_prestito;
    private Volume volume;
    private Utente utente;
    private LocalDate dataInizio;
    private Boolean restituito;
    private int num_rinnovi;

    //costruttore con id_prestito, dataInizio e restituito (usato quando leggiamo i dati dal database)
    public Prestito(int id_prestito, Volume volume, Utente utente, LocalDate dataInizio, Boolean restituito, int num_rinnovi) {
        this.id_prestito = id_prestito;
        this.volume = volume;
        this.utente = utente;
        this.dataInizio = dataInizio;
        this.restituito = restituito;
        this.num_rinnovi = num_rinnovi;
    }

    //costruttore senza id_prestito, dataInizio e restituito (usato quando creiamo un nuovo oggetto)
    public Prestito(Volume volume, Utente utente) {
        this.volume = volume;
        this.utente = utente;
        this.num_rinnovi = 0;
    }

    public int getId_prestito() {
        return id_prestito;
    }

    public Volume getVolume() {
        return volume;
    }

    public Utente getUtente() {
        return utente;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public Boolean getRestituito() {
        return restituito;
    }

    public int getNum_rinnovi() {
        return num_rinnovi;
    }
    //toString
    @Override
    public String toString() {
        String s;
        LocalDate dataFine;

        if(LoanDAO.rinnovi(this.getVolume().getEdizione().getIsbn()) == 2){
            dataFine = this.getDataInizio().plusDays(60);
        }else if(LoanDAO.rinnovi(this.getVolume().getEdizione().getIsbn()) == 1){
            dataFine = this.getDataInizio().plusDays(45);
        }else{
            dataFine = this.getDataInizio().plusDays(30);
        }
        s = this.getVolume().getEdizione().getOpera().getTitolo() + " - " + this.getVolume().getEdizione().getNumero() + " edizione - " + this.getVolume().getEdizione().getEditore() + " - " + this.getVolume().getEdizione().getOpera().getAutore() + " - Da restituire entro il: " + dataFine + " - " + this.getId_prestito();
        return s;
    }
}