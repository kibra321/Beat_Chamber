/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans;

import com.beatchamber.entities.Clients;
import com.beatchamber.jpacontroller.ClientsJpaController;
import com.beatchamber.jpacontroller.ProvincesJpaController;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1733570 Yan Tang
 */
@Named("theLoginRegister")
@SessionScoped
public class LoginRegisterBean implements Serializable {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(LoginRegisterBean.class);

    private Integer ClientId;
    private String username;
    private String password;
    private String passwordConfirm;

    private Clients client;

    private boolean loggedIn;
    private boolean isManager;

    private static SecureRandom random = new SecureRandom();

    @Inject
    private ClientsJpaController clientsJpaController;

    @Inject
    private ProvincesJpaController provincesJpaController;
    
    @Inject 
    private CookieManager cookiesManager;

    // ------------------------------
    // Getters & Setters
    // ------------------------------
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getClientId() {
        return this.ClientId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return this.passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public Clients getClient() {
        if (this.client == null) {
            this.client = new Clients();
        }
        return this.client;
    }

    public void setClient(Clients client) {
        this.client = client;
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isManager() {
        return this.isManager;
    }

    public List<Clients> getClients() {
        return clientsJpaController.findClientsEntities();
    }

    public List<String> getProvices() {
        List<String> proviceList = new ArrayList<>();
        this.provincesJpaController.findProvincesEntities().forEach(item -> proviceList.add(item.getChoiceName()));
        return proviceList;
    }

    /**
     * Login operation.
     *
     * @return String
     */
    public String doLogin() {
        List<Clients> clientsList = getClients();
        for (Clients clientItem : clientsList) {
            ClientId = clientItem.getClientNumber();
            String dbUsername = clientItem.getUsername();
            String dbEmail = clientItem.getEmail();
            String dbSalt = clientItem.getSalt();
            String dbhashPassword = clientItem.getHash();

            // check username and password
            if ((dbUsername.toLowerCase().equals(username.toLowerCase()) || dbEmail.toLowerCase().equals(username.toLowerCase()))) {
                boolean isPasswordMatch = false;
                if (dbSalt != null && !dbSalt.equals("test")) {
                    byte[] saltByte = Base64.getDecoder().decode(dbSalt);
                    String hashpsword = getSecurePassword(password, saltByte);
                    if (dbhashPassword.equals(hashpsword)) {
                        isPasswordMatch = true;
                    }
                } else if (dbhashPassword.equals(password)) {
                    isPasswordMatch = true;
                }

                // Successful login
                if (isPasswordMatch) {
                    LOG.info("Successful login");
                    loggedIn = true;
                    this.client = clientItem;
                    this.username = this.client.getUsername();

                    if (client.getTitle().equals("Manager")) {
                        this.isManager = true;
                        return "redirectToManagement";
                    } else {
                        this.isManager = false;
                        return "redirectToIndex";
                    }
                }
            }
        }
        LOG.info("Unsuccessful login");

        // Set login ERROR
        FacesContext context = FacesContext.getCurrentInstance();
        String message = context.getApplication().evaluateExpressionGet(context, "#{msgs['incorrectLogin']}", String.class);
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        // To to login page
        return "toLogin";

    }

    /**
     * Logout operation.
     *
     * @return
     */
    public String doLogout() {
        // Set the parameter indicating that user is logged in to false
        loggedIn = false;

        // Set logout message
        FacesMessage msg = new FacesMessage("Logout success!", "INFO MSG");
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage("errors", msg);

        //clear the form
        this.client = new Clients();
        LOG.info("Successful logout");
        return "redirectToIndex";
    }

    public void validatePasswordCorrect(FacesContext context, UIComponent component,
            Object value) {

        LOG.debug("validatePasswordCorrect");

        // Retrieve the value passed to this method
        String confirmPassword = (String) value;

        // Retrieve the temporary value from the password field
        UIInput passwordInput = (UIInput) component.findComponent("password");
        String password = (String) passwordInput.getLocalValue();

        if (password == null || confirmPassword == null || !password.equals(confirmPassword)) {
            LOG.debug("validatePasswordCorrect: " + password + " and " + confirmPassword);
            String message = context.getApplication().evaluateExpressionGet(context, "#{msgs['nomatch']}", String.class);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
            throw new ValidatorException(msg);
        }
    }

    /**
     * The method verifies that the Login name is not already in the database
     *
     * @param context
     * @param component
     * @param value
     */
    public void validateUniqueUser(FacesContext context, UIComponent component,
            Object value) {

        LOG.debug("validateUniqueUserName");

        // Retrieve the value passed to this method
        String username = (String) value;

        LOG.debug("validateUniqueUserName: " + username);
        List<Clients> clientsList = clientsJpaController.findClientsEntities();

        for (Clients client : clientsList) {
            if (client.getUsername().toLowerCase().equals(username.toLowerCase())) {
                String message = context.getApplication().evaluateExpressionGet(context, "#{msgs['duplicateName']}", String.class);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                throw new ValidatorException(msg);
            }
        }
    }

    /**
     * The method verifies that the Login name is not already in the database
     *
     * @param context
     * @param component
     * @param value
     */
    public void validateEmail(FacesContext context, UIComponent component,
            Object value) {
        // Retrieve the value passed to this method
        String emailStr = (String) value;
        // Reqular expression pattern to validate the format submitted
        String validator = "^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+"
                + "(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{2,})$";

        if (!emailStr.matches(validator)) {
            String message = context.getApplication().evaluateExpressionGet(context, "#{msgs['invalidEmailFormat']}", String.class);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
            throw new ValidatorException(msg);
        }

        LOG.debug("validateUniqueEmail");

        // Retrieve the value passed to this method
        String username = (String) value;

        LOG.debug("validateUniqueEmail: " + username);
        List<Clients> clientsList = clientsJpaController.findClientsEntities();

        for (Clients client : clientsList) {
            if (client.getEmail().toLowerCase().equals(username.toLowerCase())) {
                String message = context.getApplication().evaluateExpressionGet(context, "#{msgs['duplicateEmail']}", String.class);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                throw new ValidatorException(msg);
            }
        }
    }

    /**
     * Create user when user register successfully.
     *
     * @return String redirect to index
     * @throws Exception
     */
    public String doCreateUser() throws Exception {
        // set all the necessary fields which cannot get from input to DB
        setClientFields();
        // create a new client
        clientsJpaController.create(this.client);

        //set all the fields for passing value to other pages
        loggedIn = true;
        this.ClientId = this.client.getClientNumber();
        this.username = this.client.getUsername();

        //clear the form
        this.client = new Clients();
        return "redirectToIndex";
    }

    /*
    * Set all the necessary fields which cannot get from input to DB
     */
    private void setClientFields() {

        // Set title
        this.client.setTitle("Consumer");

        //Set salt and hashed password
        byte[] salt = getSalt();
        String saltStr = Base64.getEncoder().encodeToString(salt);
        this.client.setSalt(saltStr);

        String securePassword = getSecurePassword(this.password, salt);
        this.client.setHash(securePassword);
    }

    /*
    * Get a random salt value
     */
    private byte[] getSalt() {
        //Create array for salt
        byte[] salt = new byte[16];
        try {
            //Always use a SecureRandom generator
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            //Get a random salt
            sr.nextBytes(salt);

        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            Logger.getLogger(LoginRegisterBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        //return salt
        return salt;
    }

    /*
    * Generate a Secure Password using salt
     */
    private String getSecurePassword(String passwordToHash, byte[] salt) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(salt);
            //Get the hash's bytes 
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(LoginRegisterBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return generatedPassword;
    }

}
