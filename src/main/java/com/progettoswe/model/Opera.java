package com.progettoswe.model;

public class Opera {
    private int id_opera;
    private String titolo;
    private String autore;
    private String genere;
    private int annoPubblicazioneOriginale;
    private String Descrizione;

    //costruttore con id_opera (usato quando leggiamo i dati dal database)
    public Opera(int id_opera, String titolo, String autore, String genere, int annoPubblicazioneOriginale, String descrizione) {
        this.id_opera = id_opera;
        this.titolo = titolo;
        this.autore = autore;
        this.genere = genere;
        this.annoPubblicazioneOriginale = annoPubblicazioneOriginale;
        Descrizione = descrizione;
    }

    //costruttore senza id_opera (usato quando creiamo un nuovo oggetto)
    public Opera(String titolo, String autore, String descrizione, int annoPubblicazioneOriginale, String genere) {
        this.titolo = titolo;
        this.autore = autore;
        Descrizione = descrizione;
        this.annoPubblicazioneOriginale = annoPubblicazioneOriginale;
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

    public int getAnnoPubblicazioneOriginale() {
        return annoPubblicazioneOriginale;
    }

    public String getDescrizione() {
        return Descrizione;
    }

    public int getId_opera() {
        return id_opera;
    }
}