package com.progettoswe.business_logic;

import com.progettoswe.ORM.BookDAO;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Volume;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;


public class BookService {

    private static void aggiornaCatalogoBibliotecario(Catalogo catalogo, ListView<String> listaCatalogo) {
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
            String isbn = volume.getEdizione().getIsbn();
            int codice_edizione = volume.getEdizione().getId_edizione();

            // Aggiungi la stringa con il titolo, autore e stato
            listaCatalogo.getItems().add(isbn + " - " + titolo + " - " + codice_edizione + " - " + numeroEdizione + " edizione - " + editore + " - " + autore + " - " + statoDisponibile);
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
        catalogo = BookDAO.caricaCatalogo();
        aggiornaCatalogo(catalogo, listaCatalogo);
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