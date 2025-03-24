package com.progettoswe.model;
import java.util.ArrayList;

public class Catalogo {
    private final ArrayList<Volume> volumi;

    public Catalogo() {
        this.volumi = new ArrayList<Volume>();
    }

    public void aggiungiVolume(Volume volume) {
        volumi.add(volume);

    }

    public void rimuoviVolume(Volume volume) {
        volumi.remove(volume);
    }

    public ArrayList<Volume> getVolumi() {
        return volumi;
    }


}