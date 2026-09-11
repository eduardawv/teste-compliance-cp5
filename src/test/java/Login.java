import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
@DisplayName("Checkpoint 5 - Login")
public class Login {


    private static final String BASE_URL = "https://www.saucedemo.com/";
    private static final String USUARIO_VALIDO = "standard_user";
    private static final String SENHA_VALIDA = "secret_sauce";

    private static final By CAMPO_USUARIO = By.id("user-name");
    private static final By CAMPO_SENHA =  By.id("password");
    private static final By BOTAO_LOGIN = By.id("login-button");
    private static final By ICONE_CARRINHO = By.id("shopping_cart_container");

    private WebDriver driver;

    @BeforeEach
    void abrirNavegador() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void fecharNavegador() {
        if (driver != null) driver.quit();
    }

    @Test
    @DisplayName("CTI - Login com sucesso")
    void deveLogarComCredenciaisValidas() {

        // dado que esteja na pagina saucedemo.com
        driver.get(BASE_URL);
        assert driver.getCurrentUrl().equals(BASE_URL);
        assert driver.getTitle().equals("Swag Labs");

        // email senha validos

        driver.findElement(CAMPO_USUARIO).sendKeys(USUARIO_VALIDO);
        driver.findElement(CAMPO_SENHA).sendKeys(SENHA_VALIDA);
        driver.findElement(BOTAO_LOGIN).click();

        assertEquals(BASE_URL +"inventory.html", driver.getCurrentUrl());

        assert driver.getCurrentUrl().equals("https://www.saucedemo.com/inventory.html");
        assert driver.findElement(ICONE_CARRINHO).isDisplayed();

    }


}