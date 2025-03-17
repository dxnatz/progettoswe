package com.progettoswe.business_logic;

import com.progettoswe.ORM.BookDAO;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Libro;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;


public class BookService {

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
    
    public static void searchBooks(Catalogo catalogo, ListView<String> listaCatalogo, TextField ricerca) {
        String searchText = ricerca.getText();
        listaCatalogo.getItems().clear();
        catalogo = BookDAO.ricercaLibro(searchText);
        aggiornaCatalogo(catalogo, listaCatalogo);
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

}