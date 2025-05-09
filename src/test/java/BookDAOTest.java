import com.progettoswe.ORM.BookDAO;
import com.progettoswe.model.Catalogo;
import com.progettoswe.model.Volume;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookDAOTest {

    @Test
    public void testOttieniIsbnEsistente() {
        // Questi valori devono esistere nel database
        String titolo = "Il Signore degli Anelli";
        String autore = "J.R.R. Tolkien";
        int numeroEdizione = 1;

        String isbn = BookDAO.ottieniIsbn(titolo, autore, numeroEdizione);

        assertNotNull(isbn, "L'ISBN non dovrebbe essere nullo per un libro esistente.");
        assertEquals("9788845292610", isbn); // Sostituisci con l'ISBN atteso
    }

    @Test
    public void testOttieniIsbnInesistente() {
        // Dati che non esistono nel database
        String titolo = "Libro Inesistente";
        String autore = "Autore Falso";
        int numeroEdizione = 99;

        String isbn = BookDAO.ottieniIsbn(titolo, autore, numeroEdizione);

        assertNull(isbn, "L'ISBN dovrebbe essere nullo per un libro non esistente.");
    }

    @Test
    public void testCaricaCatalogo() {
        Catalogo catalogo = BookDAO.caricaCatalogo();
        assertNotNull(catalogo, "Il catalogo non deve essere nullo");

        List<Volume> volumi = catalogo.getVolumi();
        assertNotNull(volumi, "La lista dei volumi non deve essere nulla");
        assertFalse(volumi.isEmpty(), "Il catalogo dovrebbe contenere almeno un volume");

        // Verifica base su un volume (opzionale e modificabile)
        Volume volume = volumi.get(0);
        assertNotNull(volume.getEdizione(), "L'edizione non deve essere nulla");
        assertNotNull(volume.getEdizione().getOpera(), "L'opera non deve essere nulla");
        assertNotNull(volume.getStato(), "Lo stato del volume non deve essere nullo");
    }
}