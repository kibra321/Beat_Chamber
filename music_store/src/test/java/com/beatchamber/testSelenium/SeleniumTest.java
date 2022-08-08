/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.testSelenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author kibra
 */
public class SeleniumTest {
    private WebDriver driver;

    @BeforeClass
    public static void setupClass() {
        // Normally an executable that matches the browser you are using must
        // be in the classpath. The webdrivermanager library by Boni Garcia
        // downloads the required driver and makes it available
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void setupTest() {
        driver = new ChromeDriver();
    }

    /**
     * The most basic Selenium test method that tests to see if the page name
     * matches a specific name.
     *
     * @throws Exception
     */
    @Test
    public void testSimple() throws Exception {
        // And now use this to visit this project
        driver.get("http://localhost:8080/music_store/");

        // Wait for the page to load, timeout after 10 seconds
        WebDriverWait wait = new WebDriverWait(driver, 10);
        // Wait for the page to load, timeout after 10 seconds
        wait.until(ExpectedConditions.titleIs("Beat Chamber"));

        List<WebElement> inputElements = new ArrayList<WebElement>();
        
        WebElement inputElement = null;
        
        
        
        //              add item to cart              
        
        inputElements = driver.findElements(By.tagName("a"));
        inputElements.get(15).click();
        
        inputElements = driver.findElements(By.tagName("button"));
        inputElements.get(1).click();
        
        inputElements = driver.findElements(By.tagName("button"));
        inputElements.get(1).click();
        
        
        driver.navigate().to("http://localhost:8080/music_store/index.xhtml");
        inputElements = driver.findElements(By.tagName("a"));
        inputElements.get(15).click();
        
        inputElements = driver.findElements(By.tagName("button"));
        inputElements.get(1).click();
        
        inputElements = driver.findElements(By.tagName("button"));
        inputElements.get(2).click();
        
        driver.navigate().to("http://localhost:8080/music_store/index.xhtml");
        
        
        //              create user              
        
        //go to log in page
        inputElements = driver.findElements(By.tagName("a"));
        inputElements.get(3).click();
        
        
        inputElements = driver.findElements(By.tagName("a"));
        inputElements.get(6).click();
        
        
        //make user
        inputElement = driver.findElement(By.id("form:username"));
        inputElement.sendKeys("dawsonTestUser");
        
        inputElement = driver.findElement(By.id("form:email"));
        inputElement.sendKeys("mydawson@Email.com");
        
        inputElement = driver.findElement(By.id("form:password"));
        inputElement.sendKeys("thisIsMyPassword2021");
        
        inputElement = driver.findElement(By.id("form:re-password"));
        inputElement.sendKeys("thisIsMyPassword2021");
        
        inputElement = driver.findElement(By.id("form:first_name"));
        inputElement.sendKeys("bullwinkle");
        
        inputElement = driver.findElement(By.id("form:last_name"));
        inputElement.sendKeys("J.moose");
        
        inputElement = driver.findElement(By.id("form:registerSubmitBtn"));
        inputElement.click();
        
        
        
        
        inputElements = driver.findElements(By.tagName("a"));
        inputElements.get(4).click();
        
        driver.navigate().to("http://localhost:8080/music_store/login.xhtml");
        
        //complete login
        inputElements = driver.findElements(By.className("login-input"));
        inputElements.get(0).clear();
        inputElements.get(0).sendKeys("dawsonTestUser");
        inputElements.get(1).clear();
        inputElements.get(1).sendKeys("thisIsMyPassword2021");
        
        inputElements = driver.findElements(By.className("loginButton"));
        inputElements.get(0).click();
        
        driver.navigate().to("http://localhost:8080/music_store/checkout.xhtml");
        
        
        
        //fill the checkout
        inputElements = driver.findElements(By.className("input_info"));
        inputElements.get(0).sendKeys("4111111111111111");
        
        inputElements = driver.findElements(By.className("input_info"));
        inputElements.get(1).sendKeys("12/2022");
        
        inputElements = driver.findElements(By.className("input_info"));
        inputElements.get(2).sendKeys("123");
        
        inputElements = driver.findElements(By.className("input_info"));
        inputElements.get(3).sendKeys("bobby");
        
        inputElements = driver.findElements(By.className("input_info"));
        inputElements.get(4).sendKeys("dawson 123");
        
        inputElements = driver.findElements(By.className("input_info"));
        inputElements.get(5).sendKeys("Monttreal");
        
        inputElements = driver.findElements(By.tagName("button"));
        inputElements.get(0).click();
        
        
        inputElement = driver.findElement(By.className("ui-radiobutton-icon"));
        inputElement.click();
        
        inputElements = driver.findElements(By.tagName("button"));
        inputElements.get(2).click();
        
        
        
        wait.until(ExpectedConditions.urlToBe("http://localhost:8080/music_store/index.xhtml"));

    }

    @After
    public void shutdownTest() {
        driver.quit();
    }
}
