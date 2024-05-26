package applicationTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;

public class imageTest {
    private WebDriver driver;

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
    public void testArchiveImages() {

    }

    @Test
    public void testRecoverArchivedImages() {

    }

    @Test
    public void testFilteringImages() {

    }

}
