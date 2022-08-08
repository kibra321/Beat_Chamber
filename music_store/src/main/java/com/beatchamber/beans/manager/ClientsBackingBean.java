/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.manager;

import com.beatchamber.entities.Clients;
import com.beatchamber.entities.Orders;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
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
import java.util.Locale;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import org.primefaces.PrimeFaces;
import org.primefaces.util.LangUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This backing bean is used for client management page
 *
 * @author 1733570 Yan Tang
 */
@Named("theClients")
@ViewScoped
public class ClientsBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ClientsBackingBean.class);

    @Inject
    private ClientsJpaController clientsJpaController;

    @Inject
    private ProvincesJpaController provincesJpaController;

    private List<Clients> clients;

    private Clients selectedClient;

    private List<Clients> filteredClients;
        
    /**
     * Initialization.
     */
    @PostConstruct
    public void init() {
        this.clients = clientsJpaController.findClientsEntities();
    }

    /**
     * Get all the clients.
     *
     * @return a list of clients in the database.
     */
    public List<Clients> getClients() {
        return clients;
        
    }

    /**
     * Get the selected the client.
     *
     * @return the selected client.
     */
    public Clients getSelectedClient() {
        return selectedClient;
    }

    /**
     * Set the selected client.
     *
     * @param selectedClient the selected client.
     */
    public void setSelectedClient(Clients selectedClient) {
        this.selectedClient = selectedClient;
    }

    /**
     * Get the filteredClients.
     *
     * @return a list of filtedClients.
     */
    public List<Clients> getFilteredClients() {
        return filteredClients;
    }

    /**
     * Set the filtedClients.
     *
     * @param filteredClients a list of filtedClients.
     */
    public void setFilteredClients(List<Clients> filteredClients) {
        this.filteredClients = filteredClients;
    }

    /**
     * Get all the provinces in the database.
     *
     * @return a list of all the provinces.
     */
    public List<String> getProvinces() {
        List<String> proviceList = new ArrayList<>();
        this.provincesJpaController.findProvincesEntities().forEach(item -> proviceList.add(item.getChoiceName()));
        return proviceList;
    }

    /**
     * Check if the client is a manager.
     *
     * @return true if the client is a manager, otherwise false.
     */
    public List<Boolean> isManager() {
        List<Boolean> isManagerList = new ArrayList<>();
        this.clients.forEach(item -> isManagerList.add(item.getTitle().equals("Manager")));
        return isManagerList;
    }

    /**
     * Initialize the selectClient field when opening the add new dialog.
     */
    public void openNew() {
        this.selectedClient = new Clients();
    }

    /**
     * Add or save the changes to the client table.
     */
    public void saveClient() {
        try {
            //when add a new client
            if (this.selectedClient.getClientNumber() == null) {
                this.setPassword();
                clientsJpaController.create(this.selectedClient);
                this.clients.add(this.selectedClient);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Client Added"));
            } else {
                //when edit a existing client
                clientsJpaController.edit(this.selectedClient);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Client Updated"));
            }
        } catch (RollbackFailureException | IllegalOrphanException | NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(ClientsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ClientsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        PrimeFaces.current().executeScript("PF('manageClientDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-clients");
    }

    /**
     * Delete the client when delete button is clicked.
     */
    public void deleteClient() {
        try {
            clientsJpaController.destroy(this.selectedClient.getClientNumber());
        } catch (IllegalOrphanException | NonexistentEntityException | NotSupportedException | SystemException | RollbackFailureException | RollbackException | HeuristicMixedException | HeuristicRollbackException ex) {
            java.util.logging.Logger.getLogger(ClientsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.clients.remove(this.selectedClient);
        this.selectedClient = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Client Removed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-clients");
    }

    /**
     * Set the hashed password for a new client.
     */
    private void setPassword() {
        String initPassword = "123456";

        //Set salt
        byte[] salt = getSalt();
        String saltStr = Base64.getEncoder().encodeToString(salt);
        this.selectedClient.setSalt(saltStr);

        //Set hashed password
        String securePassword = getSecurePassword(initPassword, salt);
        this.selectedClient.setHash(securePassword);
    }

    /**
     * Get a random salt value.
     *
     * @return a list of byte array for random number.
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
            java.util.logging.Logger.getLogger(ClientsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        //return salt
        return salt;
    }

    /**
     * Generate a Secure Password using salt.
     *
     * @param passwordToHash the password need to be hashed.
     * @param salt the salt.
     * @return a string of a hashed password.
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
            java.util.logging.Logger.getLogger(ClientsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return generatedPassword;
    }

    /**
     * The method verifies that the username is not already in the database.
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
            if (client.getUsername().toLowerCase().equals(username.toLowerCase())
                    && this.selectedClient.getClientNumber() != client.getClientNumber()) {
                String message = context.getApplication().evaluateExpressionGet(context, "#{msgs['duplicateName']}", String.class);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                throw new ValidatorException(msg);
            }
        }
    }

    /**
     * The method verifies that the email is not already in the database.
     *
     * @param context
     * @param component
     * @param value
     */
    public void validateEmail(FacesContext context, UIComponent component,
            Object value) {
        LOG.debug("validateEmailFormat");

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
            if (client.getEmail().equals(username)
                    && this.selectedClient.getClientNumber()!= client.getClientNumber()) {
                String message = context.getApplication().evaluateExpressionGet(context, "#{msgs['duplicateEmail']}", String.class);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                throw new ValidatorException(msg);
            }
        }
    }

    /**
     * It is used for a global search.
     *
     * @param value
     * @param filter
     * @param locale
     * @return
     */
    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isValueBlank(filterText)) {
            return true;
        }

        Clients client = (Clients) value;
        return client.getUsername().toLowerCase().contains(filterText)
                || client.getEmail().toLowerCase().contains(filterText)
                || client.getFirstName().toLowerCase().contains(filterText)
                || client.getLastName().toLowerCase().contains(filterText)
                || client.getCellPhone().toLowerCase().contains(filterText);
    }
    
    /**
     * Ger the total value of purchase of the client
     * @param client
     * @return the amount the client purchased
     */
    public double getTotalConsumption(Clients client){
        double totalConsumption = 0;
        for(Orders order: client.getOrdersCollection()){
            totalConsumption += order.getOrderTotal();
        }
        return totalConsumption;
    }
}
