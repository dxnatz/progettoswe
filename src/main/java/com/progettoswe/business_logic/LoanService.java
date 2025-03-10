package com.progettoswe.business_logic;

import java.time.LocalDate;
import java.util.ArrayList;

import com.progettoswe.ORM.LoanDAO;
import com.progettoswe.model.Prestito;

import javafx.scene.control.ListView;

public class LoanService {
    
    public static void stampaPrestiti(ArrayList<Prestito> prestiti, ListView<String> listaPrestiti) {
        prestiti = LoanDAO.caricaPrestiti();
        for (int i = 0; i < prestiti.size(); i++) {
            String titolo = prestiti.get(i).getLibro().getTitolo();
            LocalDate dataFine = prestiti.get(i).getDataInizioPrenotazione().plusDays(30);
            listaPrestiti.getItems().add(titolo + " | Da restituire entro il: " + dataFine);
        }
    }
    
}
