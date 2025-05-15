import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;

public class RegistrationControllerTest extends ApplicationTest {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Utilizziamo il percorso assoluto al file FXML
        String absolutePath = "/Users/mattiadonadoni/Documents/progetto_swe_maven/biblioteca/src/main/resources/com/progettoswe/registrate.fxml";
        File fxmlFile = new File(absolutePath);
        System.out.println("FXML file exists: " + fxmlFile.exists()); // debug

        FXMLLoader loader = new FXMLLoader(fxmlFile.toURI().toURL());
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(1000);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    @BeforeEach
    void setupEach() {
        // Aspetta che JavaFX completi il rendering
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    public void testEmailNonValidaMostraErrore() throws InterruptedException {

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#nomeTextField").write("Mario");
        clickOn("#cognomeTextField").write("Rossi");
        clickOn("#codiceFiscaleTextField").write("RSSMRA80A01H501U");
        clickOn("#emailTextField").write("email-nonvalida");
        clickOn("#passwordTextField").write("P@ssw0rd");
        clickOn("#cellulareTextField").write("1234567890");
        clickOn("#dataNascitaPicker").write("01/01/2000").type(KeyCode.ENTER);
        clickOn("#indirizzoTextField").write("Via Roma 1");

        clickOn("#registerButton");

        FxAssert.verifyThat(".dialog-pane .content", LabeledMatchers.hasText("Email non valida\n" +
        "\n" +
        "L'email deve essere valida e non deve contenere spazi (es. esempio@prova.com)."));
        clickOn("OK");

    }
}