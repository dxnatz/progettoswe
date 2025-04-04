package com.progettoswe.model;
import java.util.ArrayList;

public class Catalogo {
    private ArrayList<Volume> volumi;

    public Catalogo() {
        this.volumi = new ArrayList<Volume>();
    }

    public void aggiungiVolume(Volume volume) {
        volumi.add(volume); //it is possible to add multiple copies of the same book
    }

    public void rimuoviLibro(Volume volume) {
        volumi.remove(volume);
    }

    public ArrayList<Volume> getVolumi() {
        return volumi;
    }


}