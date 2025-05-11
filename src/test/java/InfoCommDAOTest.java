import com.progettoswe.business_logic.InfoCommService;
import com.progettoswe.business_logic.LoanService;
import com.progettoswe.model.Session;
import com.progettoswe.model.Utente;
import com.progettoswe.model.Commento;
import com.progettoswe.model.Volume;
import com.progettoswe.model.Edizione;
import com.progettoswe.model.Opera;
import com.progettoswe.model.Prestito;
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

    // 1. Test aggiunta commento per volume
    @Test
    public void testAggiungiCommentoVolume() {
        boolean risultato = InfoCommService.aggiungiCommentoVolume(ID_PRESTITO_TEST, COMMENTO_TEST);

        assertTrue(risultato);
        Commento commento = InfoCommService.getCommentoUtente(LoanService.getPrestito(ID_PRESTITO_TEST).getVolume().getEdizione().getIdEdizione());
        assertEquals(COMMENTO_TEST, commento.getTesto(), "Il commento dovrebbe essere stato aggiunto correttamente");

        InfoCommService.eliminaCommento(ID_PRESTITO_TEST);
    }

    // 2. Test aggiunta commento per opera
    @Test
    public void testAggiungiCommentoOpera() {
        boolean risultato = InfoCommService.aggiungiCommento(COMMENTO_TEST, ID_EDIZIONE_TEST);

        assertTrue(risultato, "L'aggiunta del commento su opera dovrebbe riuscire");
        Commento commento = InfoCommService.getCommentoUtente(ID_EDIZIONE_TEST);
        assertEquals(COMMENTO_TEST, commento.getTesto(), "Il commento dovrebbe essere stato aggiunto correttamente");

        InfoCommService.eliminaCommento(ID_EDIZIONE_TEST);
    }

    // 3. Test eliminazione commento
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

    // 4. Test modifica commento
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