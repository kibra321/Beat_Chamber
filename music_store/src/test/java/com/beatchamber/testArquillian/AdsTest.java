
package com.beatchamber.testArquillian;

import com.beatchamber.entities.Ads;
import com.beatchamber.entities.Clients;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.jpacontroller.AdsJpaController;
import com.beatchamber.jpacontroller.ClientsJpaController;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author kibra
 */
@Ignore
@RunWith(Arquillian.class)
public class AdsTest {
    @Deployment
    public static WebArchive deploy() {

        // Use an alternative to the JUnit assert library called AssertJ
        // Need to reference MySQL driver as it is not part of either
        // embedded or remote
        final File[] dependencies = Maven
                .resolver()
                .loadPomFromFile("pom.xml")
                .resolve("mysql:mysql-connector-java",
                        "org.assertj:assertj-core",
                        "org.slf4j:slf4j-api",
                        "org.apache.logging.log4j:log4j-slf4j-impl",
                        "org.apache.logging.log4j:log4j-web"
                ).withTransitivity()
                .asFile();

        // The SQL script to create the database is in src/test/resources
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "test.war")
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"))
                .addPackage(ClientsJpaController.class.getPackage())
                .addPackage(Clients.class.getPackage())
                .addPackage(RollbackFailureException.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/payara-resources.xml"), "payara-resources.xml")
                .addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsResource(new File("src/main/resources/log4j2.xml"), "log4j2.xml")
                .addAsResource("create_Table_And_Data.sql")
                .addAsLibraries(dependencies);

        return webArchive;
    }
    
    @Inject
    private AdsJpaController adsController ; 
    
    @Resource(lookup = "java:app/jdbc/CSgb1w21")
    private DataSource ds;
    
    /**
     * test if we can get the correct link
     */
    /*@Ignore*/
    @Test
    public void test_getLink(){
        assertTrue(adsController.getAdLink(1).equals("https://www.crunchyroll.com/"));
    }
    
    /**
     * see if we can get the correct path of a specific ad
     */
    /*@Ignore*/
    @Test
    public void test_getAdPath(){
        System.out.println(adsController.getAdLink(1) + " %");
        assertTrue(adsController.getAdPath(1).equals("ads/anime.png"));
    }
    
    /**
     * see if we can get the correct number of add that are in the database
     */
    /*@Ignore*/
    @Test
    public void test_getCount(){
        assertTrue(adsController.getAdsCount() == 10);
    }
    
    /**
     * see if we can find a specific ad
     */
    @Test
    public void test_findAd(){
        assertTrue(adsController.findAds(1).getLink().equals("https://www.crunchyroll.com/"));
    }
    
    /**
     * an alternative way to get all ads
     */
    /*@Ignore*/
    @Test
    public void test_findAllAds(){
        assertTrue(adsController.findAdsEntities().size() == 10);
    }
    
    /**
     * This test will see if we can delete an ad
     * @throws IllegalOrphanException
     * @throws NonexistentEntityException
     * @throws NotSupportedException
     * @throws SystemException
     * @throws RollbackFailureException
     * @throws RollbackException
     * @throws HeuristicMixedException
     * @throws HeuristicRollbackException 
     */
    /*@Ignore*/
    @Test
    public void test_destroyAds() throws IllegalOrphanException, NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException{
        adsController.destroy(1);
        assertTrue(adsController.getAdsCount()==9);
    }
    
    /**
     * Test to see if the edit works
     * @throws Exception 
     */
    /*@Ignore*/
    @Test
    public void test_Edit() throws Exception{
        Ads returnedAd = adsController.findAds(1);
        returnedAd.setFileName("bob");
        adsController.edit(returnedAd);
        assertTrue(adsController.findAds(1).getFileName().equals("bob"));
    }
    
    /**
     * Test to see if we can add an ad
     * @throws RollbackFailureException 
     */
    /*@Ignore*/
    @Test
    public void test_addAds() throws RollbackFailureException{
        Ads ads = new Ads();
        ads.setAdId(21);
        ads.setFileName("avengers");
        ads.setLink("google.com");
        adsController.create(ads);
        assertTrue(adsController.findAdsEntities().size() == 11);
    }
    


/**
     * The database is recreated before each test. If the last test is
     * destructive then the database is in an unstable state. @AfterClass is
     * called just once when the test class is finished with by the JUnit
     * framework. It is instantiating the test class anonymously so that it can
     * execute its non-static seedDatabase routine.
     *
    @AfterClass
    public static void seedAfterTestCompleted() {
        //ClientTest clientTest = new ClientTest();
        new ClientTest().seedDatabase();
        //clientTest.seedDatabase();
    }*/

    /**
     * This routine recreates the database before every test. This makes sure
     * that a destructive test will not interfere with any other test. Does not
     * support stored procedures.
     *
     * This routine is courtesy of Bartosz Majsak, the lead Arquillian developer
     * at JBoss
     */
    @Before
    public void seedDatabase() {
        final String seedDataScript = loadAsString("create_Table_And_Data.sql");
        try (Connection connection = ds.getConnection()) {
            for (String statement : splitStatements(new StringReader(
                    seedDataScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed seeding database", e);
        }
    }

    /**
     * The following methods support the seedDatabase method
     */
    private String loadAsString(final String path) {

        try ( InputStream inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(path);  Scanner scanner = new Scanner(inputStream);) {
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close input stream.", e);
        }
    }

    private List<String> splitStatements(Reader reader,
            String statementDelimiter) {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder sqlStatement = new StringBuilder();
        final List<String> statements = new LinkedList<>();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || isComment(line)) {
                    continue;
                }
                sqlStatement.append(line);
                if (line.endsWith(statementDelimiter)) {
                    statements.add(sqlStatement.toString());
                    sqlStatement.setLength(0);
                }
            }
            return statements;
        } catch (IOException e) {
            throw new RuntimeException("Failed parsing sql", e);
        }
    }

    private boolean isComment(final String line) {
        return line.startsWith("--") || line.startsWith("//")
                || line.startsWith("/*");
    }
}