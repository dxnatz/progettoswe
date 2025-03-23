package com.progettoswe.model;

import java.time.LocalDate;

public class Opera {
    private int id_opera;
    private String titolo;
    private String autore;
    private String genere;
    private LocalDate annoPubblicazioneOrinale;
    private String Descrizione;

    //costruttore con id_opera (usato quando leggiamo i dati dal database)
    public Opera(int id_opera, String titolo, String autore, String genere, LocalDate annoPubblicazioneOrinale, String descrizione) {
        this.id_opera = id_opera;
        this.titolo = titolo;
        this.autore = autore;
        this.genere = genere;
        this.annoPubblicazioneOrinale = annoPubblicazioneOrinale;
        Descrizione = descrizione;
    }

    //costruttore senza id_opera (usato quando creiamo un nuovo oggetto)
    public Opera(String titolo, String autore, String descrizione, LocalDate annoPubblicazioneOrinale, String genere) {
        this.titolo = titolo;
        this.autore = autore;
        Descrizione = descrizione;
        this.annoPubblicazioneOrinale = annoPubblicazioneOrinale;
        this.genere = genere;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getAutore() {
        return autore;
    }

    public String getGenere() {
        return genere;
    }

    public LocalDate getAnnoPubblicazioneOrinale() {
        return annoPubblicazioneOrinale;
    }

    public String getDescrizione() {
        return Descrizione;
    }
}
