package com.progettoswe.model;
import java.time.LocalDate;

public class Prestito {
    private int id_prestito;
    private Volume volume;
    private Utente utente;
    private Libro libro; //TODO: da cancellare
    private LocalDate dataInizio;
    private Boolean restituito;

    //costruttore con id_prestito, dataInizio e restituito (usato quando leggiamo i dati dal database)
    public Prestito(int id_prestito, Volume volume, Utente utente, Libro libro, LocalDate dataInizio, Boolean restituito) {
        this.id_prestito = id_prestito;
        this.volume = volume;
        this.utente = utente;
        this.libro = libro;
        this.dataInizio = dataInizio;
        this.restituito = restituito;
    }

    //costruttore senza id_prestito, dataInizio e restituito (usato quando creiamo un nuovo oggetto)
    public Prestito(Volume volume, Utente utente, Libro libro) {
        this.volume = volume;
        this.utente = utente;
        this.libro = libro;
    }
}