
package com.beatchamber.testArquillian;

import com.beatchamber.entities.Clients;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
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
import org.junit.AfterClass;
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
public class ClientTest {
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
    private ClientsJpaController clientTesting ; 
    
    @Resource(lookup = "java:app/jdbc/CSgb1w21")
    private DataSource ds;
    
    /**
     * This method will check if the total number of clients is right
     */
    @Test
    public void test_clientNumber(){
        assertTrue(clientTesting.findClientsEntities().size()==10);
    }
    
    /**
     * This method is an alternative way of getting the total number of clients
     */
    @Test
    public void test_clientNumberWithMethod(){
        assertTrue(clientTesting.getClientsCount()==10);
    }
    
    /**
     * This test will test to see if we are able to retrieve a specific client
     */
    @Test
    public void test_getClient(){
        //this should be (1, 'Manager', 'Collingridge', 'Morton', 'DabZ', '8132 Lyons Plaza', '45059 Dottie Circle', 'Donnacona', 'Manitoba', 'Canada', 'G3M 3G5', '(546)267-4199', '(762)159-2854', 'cst.receive@gmail.com', 'DawsonManager','collegedawson'),
        assertTrue(clientTesting.findClients(1).getTitle().equals("Manager"));
    }
    
    /**
     * This test will destroy a user and check the size of the clients that are in the database
     * @throws IllegalOrphanException
     * @throws NonexistentEntityException
     * @throws NotSupportedException
     * @throws SystemException
     * @throws RollbackFailureException
     * @throws RollbackException
     * @throws HeuristicMixedException
     * @throws HeuristicRollbackException 
     */
    @Test
    public void test_destroyClient() throws IllegalOrphanException, NonexistentEntityException, NotSupportedException, SystemException, RollbackFailureException, RollbackException, HeuristicMixedException, HeuristicRollbackException{
        //id 1 should be  (1, 'Manager', 'Collingridge', 'Morton', 'DabZ', '8132 Lyons Plaza', '45059 Dottie Circle', 'Donnacona', 'Manitoba', 'Canada', 'G3M 3G5', '(546)267-4199', '(762)159-2854', 'cst.receive@gmail.com', 'DawsonManager','collegedawson'),
        clientTesting.destroy(1);
        
        assertTrue(clientTesting.getClientsCount()==9);
    }
    
    /**
     * This test will create a new user and check the new size of the list of clients returned
     * @throws RollbackFailureException 
     */
    @Test
    public void test_createClient() throws RollbackFailureException{
        Clients newCreateClient = new Clients();
        newCreateClient.setAddress1("dawson 123");
        newCreateClient.setAddress2("dawson2 123");
        newCreateClient.setCellPhone("5144565456");
        newCreateClient.setCity("Montreal");
        newCreateClient.setClientNumber(30);//It is 100% sure that it is not in the table 
        newCreateClient.setCompanyName("dawson.inc");
        newCreateClient.setCountry("Canada");
        newCreateClient.setEmail("myEmail@emails.com");
        newCreateClient.setFirstName("Bobby");
        newCreateClient.setHomePhone("5145454545");
        newCreateClient.setLastName("ybbob");
        newCreateClient.setPostalCode("h3b4v5");
        newCreateClient.setProvince("Quebec");
        newCreateClient.setSalt("asdasd");
        newCreateClient.setTitle("Consumer");
        newCreateClient.setUsername("qwerasdf");
        clientTesting.create(newCreateClient);
        assertTrue(clientTesting.getClientsCount() == 11);
    }
    
    /**
     * This test will test if when you edit a client the changes will be made
     * @throws NonexistentEntityException
     * @throws Exception 
     */
    @Test
    public void test_editClient() throws NonexistentEntityException, Exception {
        Clients newCreateClient = clientTesting.findClients(1);
        newCreateClient.setTitle("Consumer");
        clientTesting.edit(newCreateClient);
        assertTrue(clientTesting.findClients(1).getTitle().equals("Consumer"));
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
