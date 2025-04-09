import com.progettoswe.model.Session;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import java.io.File;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;


public class HomePageTest extends ApplicationTest {

    private ListView<String> listaCatalogo;
    private ListView<String> listaPrestiti;
    private TextField ricerca;
    private Button btnPrenota;
    private Button btnCancellaPrestito;
    private Button btnProlungaPrestito;
    private Button btnCommentaVolume;
    private ComboBox<String> filtroPrestiti;
    private FxRobot robot = new FxRobot();


    @Override
    public void start(Stage stage) throws Exception {
        // Carica il file FXML per la scena

        String absolutePath = "/Users/mattiadonadoni/Documents/progetto_swe_maven/biblioteca/src/main/resources/com/progettoswe/homepage.fxml"; // Cambia questo con il percorso reale del tuo file FXML
        File fxmlFile = new File(absolutePath);
        System.out.println("FXML file exists: " + fxmlFile.exists()); // debug

        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.setWidth(1200);
        stage.setHeight(600);
        stage.show();
    }

    @BeforeEach
    void setUpFields() {
        // Aspetta che JavaFX completi il rendering
        WaitForAsyncUtils.waitForFxEvents();

        // Inizializza i campi necessari per il test
        listaCatalogo = lookup("#listaCatalogo").query();
        listaPrestiti = lookup("#listaPrestiti").query();
        ricerca = lookup("#ricerca").query();
        btnPrenota = lookup("#btnPrenota").query();
        btnCancellaPrestito = lookup("#btnCancellaPrestito").query();
        btnProlungaPrestito = lookup("#btnProlungaPrestito").query();
        btnCommentaVolume = lookup("#btnCommentaVolume").query();
        filtroPrestiti = lookup("#filtroPrestiti").query();
        Session.setUserEmail("mario.rossi@example.com");
    }

    @Test
    void testSearchBooks() {
        // Simula l'inserimento di una ricerca
        clickOn(ricerca).write("Il signore");

        // Simula l'azione di ricerca (se la logica di ricerca è attivata da un pulsante o un evento automatico)
        // Per esempio, simuliamo il click su un pulsante di ricerca se necessario
        clickOn("#bottoneRicerca");

        // Aspetta che i risultati vengano visualizzati
        WaitForAsyncUtils.waitForFxEvents();

        // Verifica che la ricerca abbia trovato dei risultati (modifica secondo il comportamento del tuo sistema)
        verifyThat(listaCatalogo, (list) -> !list.getItems().isEmpty());
    }

    @Test
    void testPrenotaLibro() {
        // Simula la selezione di un libro
        clickOn(listaCatalogo).clickOn("1984 - 1 edizione - Mondadori - George Orwell - Disponibile");

        // Simula la prenotazione di un libro
        clickOn(btnPrenota);

        clickOn("Sì");

        WaitForAsyncUtils.waitForFxEvents();

        // Verifica che il libro sia stato prenotato con successo
        verifyThat("Libro prenotato con successo.", hasText("Libro prenotato con successo."));

    }

    @Test
    void testCancellaPrestito() throws InterruptedException {

        clickOn(filtroPrestiti);
        clickOn("Tutti i prestiti");

        clickOn(filtroPrestiti);
        clickOn("Prestiti attivi");

        ListView<?> lista = lookup("#listaPrestiti").queryAs(ListView.class);
        String targetText = "1984 - 1 edizione - Mondadori - George Orwell";

        for (Node cell : lista.lookupAll(".list-cell")) {
            if (cell instanceof Labeled labeled) {
                String cellText = labeled.getText();
                if (cellText != null && cellText.contains(targetText)) {
                    robot.clickOn(cell);
                    break;
                }
            }
        }

        clickOn(btnCancellaPrestito);

        clickOn("Sì");

        WaitForAsyncUtils.waitForFxEvents();

        verifyThat("Prestito annullato con successo.", hasText("Prestito annullato con successo."));
    }

    @Test
    void testProlungaPrestito() {

        clickOn(filtroPrestiti);
        clickOn("Tutti i prestiti");

        clickOn(filtroPrestiti);
        clickOn("Prestiti attivi");

        ListView<?> lista = lookup("#listaPrestiti").queryAs(ListView.class);
        String targetText = "1984 - 1 edizione - Mondadori - George Orwell";

        for (Node cell : lista.lookupAll(".list-cell")) {
            if (cell instanceof Labeled labeled) {
                String cellText = labeled.getText();
                if (cellText != null && cellText.contains(targetText)) {
                    robot.clickOn(cell);
                    break;
                }
            }
        }

        clickOn(btnProlungaPrestito);

        clickOn("Sì");

        WaitForAsyncUtils.waitForFxEvents();

        // Verifica che il prestito sia stato prolungato
        verifyThat("Successo", hasText("Successo"));
    }

    @Test
    void testFiltraPrestiti() {
        // Simula il clic sulla ComboBox per aprirla
        clickOn(filtroPrestiti);

        // Seleziona l'opzione desiderata dalla lista
        clickOn("Prestiti attivi");

        // Aspetta che i prestiti vengano filtrati
        WaitForAsyncUtils.waitForFxEvents();

        // Verifica che la lista dei prestiti sia aggiornata correttamente
        verifyThat(listaPrestiti, (list) -> list.getItems().size() > 0);
    }
}