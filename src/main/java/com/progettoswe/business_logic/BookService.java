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
            boolean disponibile = BookDAO.operaDisponibile(volume.edizione().getIsbn());
            if (disponibile) {
                statoDisponibile = "Disponibile"; // Se c'è almeno una copia disponibile
            }

            String editore = volume.edizione().getEditore();
            int numeroEdizione = volume.edizione().getNumero();
            String titolo = volume.edizione().getOpera().getTitolo();
            String autore = volume.edizione().getOpera().getAutore();
            String isbn = volume.edizione().getIsbn();

            // Aggiungi la stringa con il titolo, autore e stato
            listaCatalogo.getItems().add(isbn + " - " + titolo + " - " + numeroEdizione + " edizione - " + editore + " - " + autore + " - " + statoDisponibile);
        }
    }

    private static void aggiornaCatalogo(Catalogo catalogo, ListView<String> listaCatalogo) {
        listaCatalogo.getItems().clear();

        for (Volume volume : catalogo.getVolumi()) {
            String statoDisponibile = "Non disponibile"; // Default "Non disponibile"
            boolean disponibile = BookDAO.operaDisponibile(volume.edizione().getIsbn());
            if (disponibile) {
                statoDisponibile = "Disponibile"; // Se c'è almeno una copia disponibile
            }

            String editore = volume.edizione().getEditore();
            int numeroEdizione = volume.edizione().getNumero();
            String titolo = volume.edizione().getOpera().getTitolo();
            String autore = volume.edizione().getOpera().getAutore();

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