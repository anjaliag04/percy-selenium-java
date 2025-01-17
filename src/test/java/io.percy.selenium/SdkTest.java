package io.percy.selenium;

import java.io.IOException;
import java.util.Arrays;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import selenium.Percy;


public class SdkTest {
  private static final String TEST_URL = "http://localhost:8000";
  private static WebDriver driver;
  private static Percy percy;

  @BeforeAll
  public static void testSetup() throws IOException {
    // Disable browser logs from being logged to stdout
    //System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");
    WebDriverManager.chromedriver().setup();
    TestServer.startServer();
    ChromeOptions options = new ChromeOptions();
    options.setHeadless(true);
    driver = new ChromeDriver(options);
    percy = new Percy(driver);
  }

  @AfterAll
  public static void testTeardown() {
    // Close our test browser.
    driver.quit();
    // Shutdown our server and make sure the threadpool also terminates.
    TestServer.shutdown();
  }

  @Test
  public void takesLocalAppSnapshotWithProvidedName() {
    driver.get(TEST_URL);
    percy.snapshot("Snapshot with provided name");
  }

  @Test
  public void takesLocalAppSnapshotWithProvidedNameAndWidths() {
    driver.get(TEST_URL);
    percy.snapshot("Snapshot with provided name and widths", Arrays.asList(768, 992, 1200));
  }

  @Test
  public void takesLocalAppSnapshotWithProvidedNameAndMinHeight() {
    driver.get(TEST_URL);
    percy.snapshot("Snapshot with provided name and min height", null, 2000);
  }

  @Test
  public void takesMultipleSnapshotsInOneTestCase() {
    driver.get(TEST_URL);

    WebElement newTodoEl = driver.findElement(By.className("new-todo"));
    newTodoEl.sendKeys("A new todo to check off");
    newTodoEl.sendKeys(Keys.RETURN);
    percy.snapshot("Multiple snapshots in one test case -- #1", Arrays.asList(768, 992, 1200));

    driver.findElement(By.cssSelector("input.toggle")).click();
    percy.snapshot("Multiple snapshots in one test case -- #2", Arrays.asList(768, 992, 1200));
  }

  @Test
  public void snapshotALiveHTTPSite() {
    driver.get("http://example.com");
    percy.snapshot("Site served with HTTP");
  }

  @Test
  public void snapshotsWithJavaScriptEnabled() {
    driver.get("https://example.com");
    percy.snapshot("Site with JS enabled", null, null, true);
  }

  @Test
  public void snapshotsWithPercyCSS() {
    driver.get("https://example.com");
    percy.snapshot("Site with Percy CSS", null, null, false, "body { background-color: purple; }");
  }

  @Test
  public void snapshotsALiveHTTPSSite() {
    driver.get("https://sdk-test.percy.dev");
    percy.snapshot("Site with HTTPS, strict CSP, CORS and HSTS setup");
  }
}
