import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CheckPoint 5 - Login")
public class Login {

    // ---------------------------------------------------------------
    // Dados de teste
    // ---------------------------------------------------------------
    private static final String BASE_URL = "https://www.saucedemo.com/";
    private static final String URL_INVENTARIO = BASE_URL + "inventory.html";

    private static final String USUARIO_VALIDO = "standard_user";
    private static final String USUARIO_BLOQUEADO = "locked_out_user";
    private static final String USUARIO_LENTO = "performance_glitch_user";
    private static final String USUARIO_INEXISTENTE = "usuario_inexistente";
    private static final String SENHA_VALIDA = "secret_sauce";
    private static final String SENHA_INVALIDA = "senha_incorreta";
    private static final String VAZIO = "";

    // Mensagens esperadas (site de treino saucedemo.com)
    private static final String ERRO_BLOQUEADO =
            "Epic sadface: Sorry, this user has been locked out.";
    private static final String ERRO_CREDENCIAIS =
            "Epic sadface: Username and password do not match any user in this service";
    private static final String ERRO_USUARIO_OBRIGATORIO =
            "Epic sadface: Username is required";
    private static final String ERRO_SENHA_OBRIGATORIA =
            "Epic sadface: Password is required";

    // ---------------------------------------------------------------
    // Locators
    // ---------------------------------------------------------------
    private static final By CAMPO_USUARIO = By.id("user-name");
    private static final By CAMPO_SENHA = By.id("password");
    private static final By BOTAO_LOGIN = By.id("login-button");
    private static final By ICONE_CARRINHO = By.id("shopping_cart_container");
    private static final By TITULO_PAGINA = By.className("title");
    private static final By MENSAGEM_ERRO = By.cssSelector("[data-test='error']");

    private WebDriver driver;
    private WebDriverWait wait;

    // ---------------------------------------------------------------
    // Pre e pos-condicao de todo cenario
    // ---------------------------------------------------------------
    @BeforeEach
    void abrirNavegador() {
        driver = new ChromeDriver(opcoesDoChrome());
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void fecharNavegador() {
        if (driver != null) driver.quit();
    }

    // ===============================================================
    // CT1 - Login com credenciais validas (caminho feliz)
    // ===============================================================
    @Test
    @DisplayName("CT1 - Login com credenciais validas redireciona para inventory.html")
    void deveLogarComCredenciaisValidas() {
        // Dado: que esteja na pagina saucedemo.com
        dadoQueEstouNaPaginaDeLogin();

        // Quando: inserir dados de usuario e senha validos
        quandoInformoAsCredenciais(USUARIO_VALIDO, SENHA_VALIDA);

        // E: clicar no botao "Login"
        eClicoEmLogin();

        // Entao: deve ser redirecionado para a pagina inventory.html
        wait.until(ExpectedConditions.urlContains("inventory.html"));
        assertEquals(URL_INVENTARIO, driver.getCurrentUrl());
        assertEquals("Products", driver.findElement(TITULO_PAGINA).getText());
        assertTrue(driver.findElement(ICONE_CARRINHO).isDisplayed(),
                "O icone do carrinho deveria estar visivel apos o login");
    }

    // ===============================================================
    // CT2 - Usuario bloqueado
    // ===============================================================
    @Test
    @DisplayName("CT2 - Usuario bloqueado nao acessa e exibe mensagem de bloqueio")
    void naoDeveLogarComUsuarioBloqueado() {
        // Dado: que esteja na pagina saucedemo.com
        dadoQueEstouNaPaginaDeLogin();

        // Quando: inserir um usuario bloqueado com senha valida
        quandoInformoAsCredenciais(USUARIO_BLOQUEADO, SENHA_VALIDA);

        // E: clicar no botao "Login"
        eClicoEmLogin();

        // Entao: deve permanecer no login exibindo a mensagem de bloqueio
        assertEquals(ERRO_BLOQUEADO, mensagemDeErro());
        assertEquals(BASE_URL, driver.getCurrentUrl());
    }

    // ===============================================================
    // CT3 - Senha invalida
    // ===============================================================
    @Test
    @DisplayName("CT3 - Senha invalida nao autentica o usuario")
    void naoDeveLogarComSenhaInvalida() {
        // Dado: que esteja na pagina saucedemo.com
        dadoQueEstouNaPaginaDeLogin();

        // Quando: inserir usuario valido e senha incorreta
        quandoInformoAsCredenciais(USUARIO_VALIDO, SENHA_INVALIDA);

        // E: clicar no botao "Login"
        eClicoEmLogin();

        // Entao: deve exibir mensagem generica de credenciais invalidas
        assertEquals(ERRO_CREDENCIAIS, mensagemDeErro());
        assertEquals(BASE_URL, driver.getCurrentUrl());
    }

    // ===============================================================
    // CT4 - Usuario inexistente
    // ===============================================================
    @Test
    @DisplayName("CT4 - Usuario inexistente nao autentica")
    void naoDeveLogarComUsuarioInexistente() {
        // Dado: que esteja na pagina saucedemo.com
        dadoQueEstouNaPaginaDeLogin();

        // Quando: inserir um usuario que nao existe na base
        quandoInformoAsCredenciais(USUARIO_INEXISTENTE, SENHA_VALIDA);

        // E: clicar no botao "Login"
        eClicoEmLogin();

        // Entao: a mensagem deve ser a mesma da senha invalida (nao revela qual campo errou)
        assertEquals(ERRO_CREDENCIAIS, mensagemDeErro());
        assertEquals(BASE_URL, driver.getCurrentUrl());
    }

    // ===============================================================
    // CT5 - Campo usuario obrigatorio
    // ===============================================================
    @Test
    @DisplayName("CT5 - Campo usuario vazio exibe mensagem de obrigatoriedade")
    void deveExigirPreenchimentoDoUsuario() {
        // Dado: que esteja na pagina saucedemo.com
        dadoQueEstouNaPaginaDeLogin();

        // Quando: deixar o usuario em branco e preencher apenas a senha
        quandoInformoAsCredenciais(VAZIO, SENHA_VALIDA);

        // E: clicar no botao "Login"
        eClicoEmLogin();

        // Entao: deve exigir o preenchimento do usuario
        assertEquals(ERRO_USUARIO_OBRIGATORIO, mensagemDeErro());
        assertEquals(BASE_URL, driver.getCurrentUrl());
    }

    // ===============================================================
    // CT6 - Campo senha obrigatorio
    // ===============================================================
    @Test
    @DisplayName("CT6 - Campo senha vazio exibe mensagem de obrigatoriedade")
    void deveExigirPreenchimentoDaSenha() {
        // Dado: que esteja na pagina saucedemo.com
        dadoQueEstouNaPaginaDeLogin();

        // Quando: preencher apenas o usuario e deixar a senha em branco
        quandoInformoAsCredenciais(USUARIO_VALIDO, VAZIO);

        // E: clicar no botao "Login"
        eClicoEmLogin();

        // Entao: deve exigir o preenchimento da senha
        assertEquals(ERRO_SENHA_OBRIGATORIA, mensagemDeErro());
        assertEquals(BASE_URL, driver.getCurrentUrl());
    }

    // ===============================================================
    // CT7 - Login com resposta lenta (sincronizacao)
    // ===============================================================
    @Test
    @DisplayName("CT7 - Login com usuario de resposta lenta conclui dentro do timeout")
    void deveLogarComUsuarioDeRespostaLenta() {
        // Dado: que esteja na pagina saucedemo.com
        dadoQueEstouNaPaginaDeLogin();

        // Quando: inserir o usuario que simula lentidao do servidor
        quandoInformoAsCredenciais(USUARIO_LENTO, SENHA_VALIDA);

        // E: clicar no botao "Login"
        eClicoEmLogin();

        // Entao: a espera explicita segue assim que a pagina carrega, sem tempo fixo
        wait.until(ExpectedConditions.visibilityOfElementLocated(ICONE_CARRINHO));
        assertEquals(URL_INVENTARIO, driver.getCurrentUrl());
    }

    // ---------------------------------------------------------------
    // Passos reutilizaveis (Gherkin -> metodo)
    // ---------------------------------------------------------------
    private void dadoQueEstouNaPaginaDeLogin() {
        driver.get(BASE_URL);
        assertEquals(BASE_URL, driver.getCurrentUrl());
        assertEquals("Swag Labs", driver.getTitle());
    }

    private void quandoInformoAsCredenciais(String usuario, String senha) {
        driver.findElement(CAMPO_USUARIO).clear();
        driver.findElement(CAMPO_SENHA).clear();
        if (!usuario.isEmpty()) driver.findElement(CAMPO_USUARIO).sendKeys(usuario);
        if (!senha.isEmpty()) driver.findElement(CAMPO_SENHA).sendKeys(senha);
    }

    private void eClicoEmLogin() {
        driver.findElement(BOTAO_LOGIN).click();
    }

    private String mensagemDeErro() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(MENSAGEM_ERRO)).getText();
    }

    /**
     * Desliga o gerenciador de senhas e a deteccao de vazamento do Chrome.
     * Sem isso, o navegador abre um dialogo nativo sobre a senha apos o login
     * e o teste falha por um motivo que nao e do sistema sob teste.
     */
    private ChromeOptions opcoesDoChrome() {
        Map<String, Object> preferencias = new HashMap<>();
        preferencias.put("credentials_enable_service", false);
        preferencias.put("profile.password_manager_enabled", false);
        preferencias.put("profile.password_manager_leak_detection", false);

        ChromeOptions opcoes = new ChromeOptions();
        opcoes.setExperimentalOption("prefs", preferencias);
        opcoes.addArguments("--start-maximized");
        opcoes.addArguments("--disable-search-engine-choice-screen");
        return opcoes;
    }
}
