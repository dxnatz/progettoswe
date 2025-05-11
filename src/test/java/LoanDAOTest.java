import com.progettoswe.ORM.LoanDAO;
import com.progettoswe.model.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

;

public class LoanDAOTest {

    private String isbnDisponibile;

    @BeforeEach
    void setUp() {
        isbnDisponibile = "9788845292610";
        Session.setUserEmail("mario.rossi@example.com");
    }

    @Test
    void testPrenotaEAnnullaPrestito() {
        boolean prenotazioneSuccesso = LoanDAO.prenotaLibro(isbnDisponibile);
        assertTrue(prenotazioneSuccesso, "La prenotazione del libro dovrebbe andare a buon fine");

        boolean annullamentoSuccesso = LoanDAO.annullaPrestito(isbnDisponibile);
        assertTrue(annullamentoSuccesso, "L'annullamento del prestito dovrebbe andare a buon fine");
    }
}