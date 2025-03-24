package com.progettoswe.business_logic;

import com.progettoswe.ORM.BookDAO;
import com.progettoswe.ORM.VolumeDAO;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Libro;
import com.progettoswe.model.Prestito;
import com.progettoswe.model.Volume;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.ArrayList;


public class BookService {

    //aggiornato
    private static void aggiornaCatalogoISBN(Catalogo catalogo, ListView<String> listaCatalogo) {
        listaCatalogo.getItems().clear();
        for (int i = 0; i < catalogo.getVolumi().size(); i++) {
            String isbn = catalogo.getVolumi().get(i).edizione().getIsbn();
            String titolo = catalogo.getVolumi().get(i).edizione().getOpera().getTitolo();
            String autore = catalogo.getVolumi().get(i).edizione().getOpera().getAutore();
            String disponibile;
            if(catalogo.getVolumi().get(i).getStato().contentEquals("disponibile")){
                disponibile = "Disponibile";
            }else{
                disponibile = "Non Disponibile";
            }
            listaCatalogo.getItems().add(isbn + " - " + titolo + " - " + autore + " - " + disponibile);
        }
    }

    //aggiornato
    private static void aggiornaCatalogo(Catalogo catalogo, ListView<String> listaCatalogo) {
        listaCatalogo.getItems().clear();
        for (int i = 0; i < catalogo.getVolumi().size(); i++) {
            String titolo = catalogo.getVolumi().get(i).edizione().getOpera().getTitolo();
            String autore = catalogo.getVolumi().get(i).edizione().getOpera().getAutore();
            String disponibile;
            if(catalogo.getVolumi().get(i).getStato().contentEquals("disponibile")){
                disponibile = "Disponibile";
            }else{
                disponibile = "Non Disponibile";
            }
            listaCatalogo.getItems().add(titolo + " - " + autore + " - " + disponibile);
        }
    }

    //aggiornato
    public static void stampaCatalogo(Catalogo catalogo, ListView<String> listaCatalogo) {
        catalogo = BookDAO.caricaCatalogo();
        aggiornaCatalogo(catalogo, listaCatalogo);
    }

    //aggiornato
    public static void stampaCatalogoISBN(Catalogo catalogo, ListView<String> listaCatalogo) {
        catalogo = BookDAO.caricaCatalogo();
        aggiornaCatalogoISBN(catalogo, listaCatalogo);
    }

    //aggiornato
    public static void searchBooks(Catalogo catalogo, ListView<String> listaCatalogo, TextField ricerca) {
        String searchText = ricerca.getText();
        listaCatalogo.getItems().clear();
        catalogo = BookDAO.ricercaLibro(searchText);
        aggiornaCatalogo(catalogo, listaCatalogo);
    }

    //aggiornato
    public static void searchBooksISBN(Catalogo catalogo, ListView<String> listaCatalogo, TextField ricerca) {
        String searchText = ricerca.getText();
        listaCatalogo.getItems().clear();
        catalogo = BookDAO.ricercaLibro(searchText);
        aggiornaCatalogoISBN(catalogo, listaCatalogo);
    }

    //TODO: da ricontrollare
    public static boolean addVolume(Volume v) throws SQLException {
        if (BookDAO.getVolume(v.edizione().getIsbn()) != null){
            return false;
        }
        return VolumeDAO.aggiungiVolume(v);
    }

    //TODO: da ricontrollare
    public static boolean deleteBook(String isbn, ArrayList<Prestito> prestiti){
        for (Prestito p : prestiti) {
            if(p.getVolume().edizione().getIsbn().contentEquals(isbn)){
                return false;
            }
        }
        return BookDAO.cancellaVolume(isbn);
    }

    //TODO: da ricontrollare
    public static Volume getVolume(String isbn){
        return VolumeDAO.getVolume(isbn);
    }

    public static boolean isAnyPropertyTooLong(Libro l) {
        return (l.getIsbn().length() > Libro.ISBN_MAX_LENGTH ||
                l.getTitolo().length() > Libro.TITOLO_MAX_LENGTH ||
                l.getAutore().length() > Libro.AUTORE_MAX_LENGTH ||
                l.getEditore().length() > Libro.EDITORE_MAX_LENGTH ||
                l.getGenere().length() > Libro.GENERE_MAX_LENGTH);
    }

    public static boolean isCopieValid(Libro l){
        return l.getCopie() >= Libro.MINIMUM_COPIE;
    }

    public static boolean updateBook(Libro l){
        return BookDAO.aggiornaLibro(l);
    }

    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }
}