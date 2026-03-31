import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

public  abstract class AbstractFxTest {


    // https://stackoverflow.com/questions/45109876/toolkit-not-initialized-exception-when-unit-testing-an-javafx-application
    @BeforeAll
    static void initJfxRuntime() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {

        }
    }
}
