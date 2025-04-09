import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class LoginTest extends ApplicationTest {

    private TextField emailTextField;
    private TextField passwordTextField;

    @Override
    public void start(Stage stage) throws Exception {
        // Carica il file FXML per la scena
        String absolutePath = "/Users/mattiadonadoni/Documents/progetto_swe_maven/biblioteca/src/main/resources/com/progettoswe/login.fxml";
        File fxmlFile = new File(absolutePath);
        System.out.println("FXML file exists: " + fxmlFile.exists()); // debug

        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.show();
    }

    @BeforeEach
    void setUpFields() {
        // Aspetta che JavaFX completi il rendering
        WaitForAsyncUtils.waitForFxEvents();

        // Inizializza i campi di testo dopo che la scena è pronta
        emailTextField = lookup("#emailTextField").query();
        passwordTextField = lookup("#passwordTextField").query();
    }

    @Test
    void testLoginSuccess() {
        // Simula l'inserimento dell'email e della password
        clickOn(emailTextField).write("mario.rossi@example.com");
        clickOn(passwordTextField).write("1");

        // Simula il clic sul pulsante di login
        clickOn("#loginButton");

        // Aspetta che la scena cambi
        WaitForAsyncUtils.waitForFxEvents();
        

    }

    @Test
    void testLoginFail() {
        clickOn(emailTextField).write("wronguser@example.com");
        clickOn(passwordTextField).write("wrongpassword");

        clickOn("#loginButton");

        WaitForAsyncUtils.waitForFxEvents();

        verifyThat("Errore di accesso", hasText("Errore di accesso"));
    }
}