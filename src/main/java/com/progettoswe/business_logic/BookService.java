package com.progettoswe.business_logic;

import java.util.ArrayList;

import com.progettoswe.ORM.BookDAO;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Libro;
import com.progettoswe.model.Prestito;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;


public class BookService {

    private static void aggiornaCatalogoISBN(Catalogo catalogo, ListView<String> listaCatalogo) {
        listaCatalogo.getItems().clear();
        for (int i = 0; i < catalogo.getLibri().size(); i++) {
            String isbn = catalogo.getLibri().get(i).getIsbn();
            String titolo = catalogo.getLibri().get(i).getTitolo();
            String autore = catalogo.getLibri().get(i).getAutore();
            String disponibile;
            if(catalogo.getLibri().get(i).getCopie() == 0){
                disponibile = "Non disponibile";
            }else{
                disponibile = "Disponibile";
            }
            listaCatalogo.getItems().add(isbn + " - " + titolo + " - " + autore + " - " + disponibile);
        }
    }

    private static void aggiornaCatalogo(Catalogo catalogo, ListView<String> listaCatalogo) {
        listaCatalogo.getItems().clear();
        for (int i = 0; i < catalogo.getLibri().size(); i++) {
            String titolo = catalogo.getLibri().get(i).getTitolo();
            String autore = catalogo.getLibri().get(i).getAutore();
            String disponibile;
            if(catalogo.getLibri().get(i).getCopie() == 0){
                disponibile = "Non disponibile";
            }else{
                disponibile = "Disponibile";
            }
            listaCatalogo.getItems().add(titolo + " - " + autore + " - " + disponibile);
        }
    }

    public static void stampaCatalogo(Catalogo catalogo, ListView<String> listaCatalogo) {
        catalogo = BookDAO.caricaCatalogo();
        aggiornaCatalogo(catalogo, listaCatalogo);
    }

    public static void stampaCatalogoISBN(Catalogo catalogo, ListView<String> listaCatalogo) {
        catalogo = BookDAO.caricaCatalogo();
        aggiornaCatalogoISBN(catalogo, listaCatalogo);
    }
    
    public static void searchBooks(Catalogo catalogo, ListView<String> listaCatalogo, TextField ricerca) {
        String searchText = ricerca.getText();
        listaCatalogo.getItems().clear();
        catalogo = BookDAO.ricercaLibro(searchText);
        aggiornaCatalogo(catalogo, listaCatalogo);
    }

    public static void searchBooksISBN(Catalogo catalogo, ListView<String> listaCatalogo, TextField ricerca) {
        String searchText = ricerca.getText();
        listaCatalogo.getItems().clear();
        catalogo = BookDAO.ricercaLibro(searchText);
        aggiornaCatalogoISBN(catalogo, listaCatalogo);
    }

    public static boolean addBook(Libro l){
        if(BookDAO.libroDisponibile(l.getIsbn())){
            for (int i = 0; i < l.getCopie(); i++) {
                BookDAO.aumentaCopieLibro(l.getIsbn());
            }
            return true;
        }

        return BookDAO.aggiungiLibro(l);
    }

    public static boolean deleteBook(String isbn, ArrayList<Prestito> prestiti){
        for (Prestito p : prestiti) {
            if(p.getLibro().getIsbn().contentEquals(isbn) && !p.isRestituito()){
                return false;
            }
        }
        return BookDAO.cancellaLibro(isbn);
    }
    
    public static Libro getBook(String isbn){
        return BookDAO.getLibro(isbn);
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
}