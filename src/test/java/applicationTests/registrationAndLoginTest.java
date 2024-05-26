package applicationTests;

import dao.AccountDao;
import model.Account;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import security.TokenManager;

import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class registrationAndLoginTest {
    private WebDriver driver;
    private final String USERNAME = "registrationAndLoginTest";
    private final String EMAIL = "registrationAndLoginTest@gmail.com";
    private final String PASSWORD = "registrationAndLoginTestPassword";
    @Before
    public void setUp() {
        // Set up WebDriver configuration
        driver = new FirefoxDriver();
        driver.manage().deleteAllCookies();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @After
    public void tearDown() {
        // Clean up after each test
        driver.quit();
    }
    
    @Test
    public void testRegistration() throws SQLException, InterruptedException {
        driver.get("http://localhost:8080/bad/");
        driver.findElement(By.id("start-button")).click();
        driver.findElement(By.id("Name")).sendKeys(USERNAME);
        driver.findElement(By.id("Email")).sendKeys(EMAIL);
        driver.findElement(By.id("Email")).sendKeys(Keys.RETURN);
        driver.findElement(By.name("password")).sendKeys(PASSWORD);
        driver.findElement(By.name("repeat-password")).sendKeys(PASSWORD);
        driver.findElement(By.name("phone")).sendKeys("+31 681254054");
        driver.findElement(By.name("phone")).sendKeys(Keys.RETURN);

        Thread.sleep(2000);

        Account account = AccountDao.instance.getAccountByEmail(EMAIL);

        Assertions.assertNotEquals(null, account);
        Assertions.assertEquals(EMAIL, account.getEmail());
        Assertions.assertNotEquals(null, account.getPasswordHash());
        Assertions.assertEquals("+31 681254054", account.getTelephone());

        // Clean up
        Assertions.assertTrue(AccountDao.instance.deleteAccountById(account.getId()));
    }

    @Test
    public void testLogin() {
        driver.get("http://localhost:8080/bad/");
        driver.findElement(By.id("login-button")).click();
        driver.findElement(By.id("logname")).sendKeys("testaccount@gmail.com");
        driver.findElement(By.id("logpass")).sendKeys("testaccountpassword");
        driver.findElement(By.id("logpass")).sendKeys(Keys.RETURN);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Main"));

        assertTrue(driver.findElement(By.className("original")).isDisplayed());
    }

    @Test
    public void testLogOut() {
        driver.get("http://localhost:8080/bad/");
        driver.findElement(By.id("login-button")).click();
        driver.findElement(By.id("logname")).sendKeys("testaccount@gmail.com");
        driver.findElement(By.id("logpass")).sendKeys("testaccountpassword");
        driver.findElement(By.id("logpass")).sendKeys(Keys.RETURN);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Main"));

        assertTrue(driver.findElement(By.className("original")).isDisplayed());
        driver.findElement(By.id("logout-button")).click();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.titleIs("Index Landing Page"));
    }
}
