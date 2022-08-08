
package com.beatchamber.testArquillian;

import com.beatchamber.entities.Ads;
import com.beatchamber.entities.Albums;
import com.beatchamber.entities.Clients;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.jpacontroller.AdsJpaController;
import com.beatchamber.jpacontroller.AlbumsJpaController;
import com.beatchamber.jpacontroller.ClientsJpaController;
import com.beatchamber.jpacontroller.GenresJpaController;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import static org.eclipse.jetty.http.DateParser.parseDate;
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
public class AlbumTest {
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
    private AlbumsJpaController albumController ; 
    
    @Inject
    private GenresJpaController genreController;
    
    @Resource(lookup = "java:app/jdbc/CSgb1w21")
    private DataSource ds;
    
    //quick note we did not test the destroy because we dont remove the albums from the table we disable them by changing a boolean value
    
    
    /**
     * find the total number of albums
     */
    /*@Ignore*/
    @Test
    public void test_getTotalNumber(){
        assertTrue(albumController.findAlbumsEntities().size() == 24);
    }
    
    /**
     * find the genre of album
     */
    /*@Ignore*/
    @Test
    public void test_findGenre(){
        assertTrue(albumController.findGenre(1).getGenreName().equals("Hip Hop"));
    }
    
    /**
     * find the one album that contains both the string and the genre
     */
    /*@Ignore*/
    @Test
    public void test_findAllAlbumFromGenre(){
        //hip hop is the genre
        assertTrue(albumController.findAlbumsByGenre(genreController.findGenres(1), "Antisocial").size()==1);
    }
    
    /**
     * find the 1 albums that contains this string
     */
    /*@Ignore*/
    @Test
    public void test_findAlbumsByTitle(){
        assertTrue(albumController.findAlbumsByTitle("Antisocial").size()==1);
    }
    
    /**
     * check if it find the one albums that is in the date range
     *
    @Test
    public void test_findAlbumByDate() throws ParseException{
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse("2000-02-01");
        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse("2019-12-07");
        //should be Please Excuse Me For Being Antisocial
        System.out.println(albumController.findAlbumsByDate(date1,date2).size() + " sizing");
        assertTrue(albumController.findAlbumsByDate(date1,date2).size()==1);
    }*/
    
    /**
     * check if the album image is correct for the large images
     */
    /*@Ignore*/
    @Test
    public void test_checkIfImageLargeIsGood(){
        assertTrue(albumController.getAlbumPath(5,false).equals("albums/hip_hop/astroworld_large.jpg"));
    }
    
    /**
     * check if the album image is correct for the small images
     */
    /*@Ignore*/
    @Test
    public void test_checkIfImageSmallIsGood(){
        assertTrue(albumController.getAlbumPath(5,true).equals("albums/hip_hop/astroworld_small.jpg"));
    }
    
    /**
     * This will return the artist from the album they are from
     */
    /*@Ignore*/
    @Test
    public void test_checkIfAlbumIsCorrectlyReturned(){
        assertTrue(albumController.getAlbumArtist(3).getArtistName().equals("The Weeknd"));
    }
    
    /**
     * This will test if we get the correct total number of albums
     */
    /*@Ignore*/
    @Test
    public void test_checkTotalNumber(){
        assertTrue(albumController.getAlbumsCount() == 24);
    }
    
    /**
     * This tests will find a specific album in the db
     */
    /*@Ignore*/
    @Test
    public void test_findSpecificAlbum(){
        assertTrue(albumController.findAlbums(6).getAlbumTitle().equals("BOSSANOVA"));
    }
    
    /**
     * This is an alternative way of find the total number of albums
     */
    /*@Ignore*/
    @Test
    public void test_findtheTotalNumberOfAlbums(){
        assertTrue(albumController.findAlbumsEntities().size() == 24);
    }
    
    /**
     * This method will see if editing does work
     * @throws NonexistentEntityException
     * @throws Exception 
     */
    /*@Ignore*/
    @Test
    public void test_edit() throws NonexistentEntityException, Exception{
        Albums album = albumController.findAlbums(1);
        album.setAlbumTitle("free albums");
        albumController.edit(album);
        assertTrue(albumController.findAlbums(1).getAlbumTitle().equals("free albums"));
    }
    
    /**
     * This method will create an Album and insert it into the db
     */
    /*@Ignore*/
    @Test
    public void test_create() throws RollbackFailureException{
        Albums album = new Albums();
        album.setAlbumNumber(30);
        album.setAlbumTitle("new album");
        album.setCostPrice(2.1);
        album.setEntryDate(new Date());
        album.setListPrice(3.2);
        album.setRecordingLabel("dawson sound");
        album.setReleaseDate(new Date());
        album.setRemovalDate(new Date());
        album.setSalePrice(5.2);
        album.setTotalTracks(4);
        albumController.create(album);
        assertTrue(albumController.findAlbumsEntities().size() == 25);
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
