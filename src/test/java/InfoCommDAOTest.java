import com.progettoswe.business_logic.InfoCommService;
import com.progettoswe.model.Session;
import com.progettoswe.model.Utente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InfoCommDAOTest {

    // Dati di test
    private static final int ID_EDIZIONE_TEST = 1;       // Per commento su opera
    private static final int ID_PRESTITO_TEST = 1;       // Per commento su volume
    private static final String COMMENTO_TEST = "Commento di test";
    private static final String COMMENTO_MODIFICATO = "Commento modificato";

    @BeforeEach
    public void setup() {
        // Configura un utente fittizio per i test
        Utente utenteTest = new Utente(1, "Test", "User", "CFTEST", "test@email.com", "1234567890", null, null);
        Session.setUtente(utenteTest);
    }

    // 1. Test aggiunta commento per opera

    @Test
    public void testAggiungiCommentoOpera() {
        boolean risultato = InfoCommService.aggiungiCommento(COMMENTO_TEST, ID_EDIZIONE_TEST);

        assertTrue(risultato, "L'aggiunta del commento su opera dovrebbe riuscire");
        String commento = InfoCommService.getCommentoUtente(ID_EDIZIONE_TEST);
        assertEquals(COMMENTO_TEST, commento, "Il commento dovrebbe essere stato aggiunto correttamente");

        InfoCommService.eliminaCommento(ID_EDIZIONE_TEST);
    }

    // 2. Test eliminazione commento
    @Test
    public void testEliminaCommento() {
        InfoCommService.aggiungiCommento(COMMENTO_TEST, ID_EDIZIONE_TEST);

        String commentoEsistente = InfoCommService.getCommentoUtente(ID_EDIZIONE_TEST);
        assertNotNull(commentoEsistente, "Il commento dovrebbe esistere prima dell'eliminazione");

        InfoCommService.eliminaCommento(ID_EDIZIONE_TEST);

        // Verifica che sia stato eliminato
        String commentoDopoEliminazione = InfoCommService.getCommentoUtente(ID_EDIZIONE_TEST);
        assertNull(commentoDopoEliminazione, "Il commento dovrebbe essere stato eliminato");
    }

    // 3. Test modifica commento
    @Test
    public void testModificaCommento() {
        InfoCommService.aggiungiCommento(COMMENTO_TEST, ID_EDIZIONE_TEST);

        boolean risultatoModifica = InfoCommService.modificaCommento(COMMENTO_MODIFICATO, ID_EDIZIONE_TEST);
        assertTrue(risultatoModifica, "La modifica del commento dovrebbe riuscire");

        String commentoModificato = InfoCommService.getCommentoUtente(ID_EDIZIONE_TEST);
        assertEquals(COMMENTO_MODIFICATO, commentoModificato, "Il commento dovrebbe essere stato modificato");

        // Pulizia dopo il test
        InfoCommService.eliminaCommento(ID_EDIZIONE_TEST);
    }
}