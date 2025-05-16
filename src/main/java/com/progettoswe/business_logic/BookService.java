package com.progettoswe.business_logic;

import com.progettoswe.ORM.BookDAO;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Volume;
import com.progettoswe.utilities.AlertUtil;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;


public class BookService {

    private static void aggiornaCatalogoBibliotecario(Catalogo catalogo, ListView<String> listaCatalogo) {
        listaCatalogo.getItems().clear();
        listaCatalogo.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Non disponibile")) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });

        for (Volume volume : catalogo.getVolumi()) {
            String statoDisponibile = BookDAO.operaDisponibile(volume.getEdizione().getIsbn())
                    ? "Disponibile" : "Non disponibile";

            String editore = volume.getEdizione().getEditore();
            int numeroEdizione = volume.getEdizione().getNumero();
            String titolo = volume.getEdizione().getOpera().getTitolo();
            String autore = volume.getEdizione().getOpera().getAutore();
            int codiceOpera = volume.getEdizione().getOpera().getId_opera();
            int codice_edizione = volume.getEdizione().getId_edizione();

            listaCatalogo.getItems().add(codiceOpera + " - " + titolo + " - " + codice_edizione +
                    " - " + numeroEdizione + " edizione - " + editore + " - " + autore + " - " + statoDisponibile);
        }
    }
    private static void aggiornaCatalogo(Catalogo catalogo, ListView<String> listaCatalogo) {
        listaCatalogo.getItems().clear();

        for (Volume volume : catalogo.getVolumi()) {
            String statoDisponibile = "Non disponibile"; // Default "Non disponibile"
            boolean disponibile = BookDAO.operaDisponibile(volume.getEdizione().getIsbn());
            if (disponibile) {
                statoDisponibile = "Disponibile"; // Se c'è almeno una copia disponibile
            }

            String editore = volume.getEdizione().getEditore();
            int numeroEdizione = volume.getEdizione().getNumero();
            String titolo = volume.getEdizione().getOpera().getTitolo();
            String autore = volume.getEdizione().getOpera().getAutore();

            // Aggiungi la stringa con il titolo, autore e stato
            listaCatalogo.getItems().add(titolo + " - " + numeroEdizione + " edizione - " + editore + " - " + autore + " - " + statoDisponibile);
        }
    }

    public static void stampaCatalogo(Catalogo catalogo, ListView<String> listaCatalogo) {
        try {
            listaCatalogo.getItems().clear();  // Pulisce la lista prima del caricamento
            catalogo = BookDAO.caricaCatalogo();
            if (catalogo == null || catalogo.getVolumi().isEmpty()) {
                AlertUtil.showErrorAlert(
                        "Errore di Caricamento",
                        "Catalogo vuoto",
                        "Non è stato possibile recuperare i dati del catalogo. Il database potrebbe essere vuoto."
                );
                return;
            }
            aggiornaCatalogo(catalogo, listaCatalogo);
        } catch (Exception e) {
            AlertUtil.showErrorAlert(
                    "Errore di Sistema",
                    "Impossibile caricare il catalogo",
                    "Si è verificato un errore durante il caricamento dei dati: " + e.getMessage()
            );
            listaCatalogo.getItems().add("Errore nel caricamento del catalogo");
        }
    }

    public static void stampaCatalogoBibliotecario(Catalogo catalogo, ListView<String> listaCatalogo) {
        catalogo = BookDAO.caricaCatalogo();
        aggiornaCatalogoBibliotecario(catalogo, listaCatalogo);
    }

    public static void searchBooks(Catalogo catalogo, ListView<String> listaCatalogo, TextField ricerca) {
        String searchText = ricerca.getText();
        listaCatalogo.getItems().clear();
        catalogo = BookDAO.ricercaLibro(searchText);
        aggiornaCatalogo(catalogo, listaCatalogo);
    }

    public static void searchBooksBibliotecario(Catalogo catalogo, ListView<String> listaCatalogo, TextField ricerca) {
        String searchText = ricerca.getText();
        listaCatalogo.getItems().clear();
        catalogo = BookDAO.ricercaLibro(searchText);
        aggiornaCatalogoBibliotecario(catalogo, listaCatalogo);
    }

}