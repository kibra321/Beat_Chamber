
package com.beatchamber.beans;

import com.beatchamber.entities.Albums;
import com.beatchamber.entities.OrderAlbum;
import com.beatchamber.entities.OrderTrack;
import com.beatchamber.entities.Orders;
import com.beatchamber.entities.Tracks;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.jpacontroller.AlbumsJpaController;
import com.beatchamber.jpacontroller.ClientsJpaController;
import com.beatchamber.jpacontroller.OrderAlbumJpaController;
import com.beatchamber.jpacontroller.OrderTrackJpaController;
import com.beatchamber.jpacontroller.OrdersJpaController;
import com.beatchamber.jpacontroller.ProvincesJpaController;
import com.beatchamber.jpacontroller.TracksJpaController;
import java.io.IOException;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import javax.transaction.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kibra
 */
@Named("checkoutData")
@RequestScoped
public class CheckoutBean implements Serializable {

    private String cardNumber = "";
    private String expDate = "";
    private String cvv = "";
    private String name = "";
    private String address = "";
    private String city = "";
    private String province = "-1";//This needs to be -1 so that we know that it is when the page has just been loaded and the first province will be shown (alberta)
    private double totalPrice = 0;
    private double totalPst = 0;
    private double totalGst = 0;
    private double totalHst = 0;
    
    private double pstVal = 0;
    private double gstVal = 0;
    private double hstVal = 0;
    
    @PersistenceContext(unitName = "music_store_persistence")
    private EntityManager em;

    @Inject
    private LoginRegisterBean userLoginBean;
    
    @Inject
    private InvoiceBean invoice;

    @Inject
    private OrdersJpaController orderController;

    @Inject
    private ClientsJpaController clientController;

    @Inject
    private OrderAlbumJpaController orderAlbumController;

    @Inject
    private OrderTrackJpaController orderTrackController;

    @Inject
    private TracksJpaController trackController;

    @Inject
    private AlbumsJpaController albumController;

    @Inject
    private ProvincesJpaController provinceController;
    
    @Inject
    private LocaleChanger localeChanger;

    private final static Logger LOG = LoggerFactory.getLogger(OrdersJpaController.class);

    /*all of the setters */
    public void setCardNumber(String value) {
        this.cardNumber = value;
    }

    public void setDate(String value) {
        this.expDate = value;
    }

    public void setCvv(String value) {
        this.cvv = value;
    }

    public void setName(String value) {
        this.name = value;
    }

    public void setAddress(String value) {
        this.address = value;
    }

    public void setCity(String value) {
        this.city = value;
    }

    public void setProvince(String value) {
        this.province = value;
    }

    /*all of the getters*/
    public String getProvince() {
        return this.province;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public String getCvv() {
        return this.cvv;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public String getCity() {
        return this.city;
    }

    public String getDate() {
        return this.expDate;
    }

    /*overiding the toString method*/
    @Override
    public String toString() {
        return "";
    }

    /**
     * This method will reload the page
     */
    public void reload() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }

    /**
     * This method will return the int value of the string
     *
     * @param strToParse
     * @return in
     * @author Ibrahim
     */
    private int parseStringToInt(String strToParse) {
        return Integer.parseInt(strToParse);
    }

    /**
     * This method will get the total price of the items that are in the cart
     * and return the value
     *
     * @param trackTotal
     * @param albumTotal
     * @return String
     * @author Ibrahim
     */
    public String computePrices(String trackTotal, String albumTotal) {
        totalPrice = (convertStringToDouble(trackTotal) + convertStringToDouble(albumTotal));
        return totalPrice + "";
    }

    /**
     * This method will convert a string into a double
     *
     * @param data
     * @return double
     * @author Ibrahim
     */
    private double convertStringToDouble(String data) {
        return Double.parseDouble(data);
    }

    /**
     * This method will compute the pst value from the total price before tax
     *
     * @param total
     * @param percentage
     * @return String
     * @author Ibrahim
     */
    public String computePst(String total, String percentage) {
        pstVal=convertStringToDouble(percentage);
        this.totalPst = getPercentagePrice(convertStringToDouble(total), convertStringToDouble(percentage));
        return this.totalPst + "";
    }

    /**
     * This method will compute the gst value from the total price before tax
     *
     * @param total
     * @param percentage
     * @return String
     * @author Ibrahim
     */
    public String computeGst(String total, String percentage) {
        gstVal=convertStringToDouble(percentage);
        this.totalGst = getPercentagePrice(convertStringToDouble(total), convertStringToDouble(percentage));
        return this.totalGst + "";
    }

    /**
     * This method will compute the pst value from the total price before tax
     *
     * @param total
     * @param percentage
     * @return String
     * @author Ibrahim
     */
    public String computeHst(String total, String percentage) {
        hstVal=convertStringToDouble(percentage);
        this.totalHst = getPercentagePrice(convertStringToDouble(total), convertStringToDouble(percentage));
        return this.totalHst + "";
    }

    /**
     * This method will return the string of the total price
     *
     * @return String
     * @author Ibrahim
     */
    public String getPriceBeforeTax() {
        return this.totalPrice + "";
    }

    /**
     * This method will return the total price with the tax added to it
     *
     * @param total
     * @param pstValue
     * @param gstValue
     * @param hstValue
     * @return String
     * @author Ibrahim
     */
    public double getTotalPriceWithTax(String total, String pstValue, String gstValue, String hstValue) {
        return roundDoubleValue(convertStringToDouble(total) + convertStringToDouble(computePst(total, pstValue)) + convertStringToDouble(computePst(total, gstValue)) + convertStringToDouble(computePst(total, hstValue)));
    }

    /**
     * This method will compute the percentage of the double value that has been
     * passed (it will also round the value to 2 digits)
     *
     * @param value
     * @param percentage
     * @return double
     * @author Ibrahim
     */
    private double getPercentagePrice(double value, double percentage) {
        double result = 0;
        if (percentage == 0) {
            result = 0;
        } else {
            result = roundDoubleValue((value * percentage) / 100);
        }

        return result;

    }

    /**
     * This method will round the double to 2 digit found some help on this
     * website :
     * https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
     *
     * @param value
     * @return Double
     * @author Ibrahim
     *
     */
    private Double roundDoubleValue(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.valueOf(df.format(value));
    }

    /**
     * This method will check if the use is logged in if they are not they will
     * go to the login page
     *
     * @param fileToGoTo
     * @return String
     */
    public String checkoutRedirect(String fileToGoTo) {

        if (userLoginBean.getClient().getFirstName() == null) {
            return "login.xhtml";
        } else {
            return fileToGoTo;
        }
    }

   

    
    /**
     * Method adds a new order to the database
     * 
     * @return 
     * 
     * @Korjon Chang-Jones Ibrahim Kebe
     */
    public String addOrders() {

        //getting the tracks and albums from a cart
        List<Albums> albums = albumController.retrieveAllAlbumsInTheCart();
        List<Tracks> tracks = trackController.retrieveAllTracksInTheCart();

        //the total price (taxes included)
        double totalTaxedPrice = combinePrices();

        //Creating an order entity
        Orders order = new Orders();
        
        CookieManager cookiesManager = new CookieManager();

        setOrderFields(order);
        
        try {
            orderController.create(order);
        } catch (RollbackFailureException ex) {
            LOG.error("orders order roll back error");
        } catch (SystemException ex) {
            java.util.logging.Logger.getLogger(CheckoutBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Creating order album
        for (Albums item : albumController.retrieveAllAlbumsInTheCart()) {
            OrderAlbum orderAlbum = new OrderAlbum();
            orderAlbum.setAlbumId(item);
            orderAlbum.setOrderId(orderController.findOrders(order.getOrderId()));
            orderAlbum.setPriceDuringOrder(item.getListPrice());
            try {
                orderAlbumController.create(orderAlbum);
            } catch (RollbackFailureException ex) {
                LOG.error("order rollback error");
            }
        }
        
        sendEmailToCustomer(order);
        
        
        //All the tracks from different purchased albums
        List<List<Tracks>> albumTracks = getTracksFromAlbums(albums);
        
        //storing randomly purchased songs
        storeOrderTracks(tracks, order);

        //storing songs from purchased albums
        albumTracks.forEach(trackList -> {
            storeOrderTracks(trackList, order);
        });

        cookiesManager.clearTheCartWithoutRefresh();

        
        return "checkout_complete.xhtml?faces-redirect=true";
    }
    
    
    /**
     * Method sets the fields and information for a new order
     * @param order 
     * 
     * @author Korjon Chang-Jones
     */
    private void setOrderFields(Orders order){
        
        Date date = new Date();
        double totalTaxedPrice = combinePrices();

        order.setOrderDate(date);
        order.setGst(this.gstVal);
        order.setPst(this.pstVal);
        order.setHst(this.hstVal);
        order.setClientNumber(clientController.findClients(userLoginBean.getClient().getClientNumber()));
        order.setVisible(true);
        order.setOrderTotal(convertStringToDouble(computePrices(trackController.getTotalPrice(), albumController.getTotalPrice())));
        order.setOrderGrossTotal(totalTaxedPrice);
    }

    /**
     * Method combines the prices and taxes to get the complete price
     *
     * @return
     * 
     * @Korjon Chang-Jones Ibrahim Kebe
     */
    private double combinePrices() {

        String total = computePrices(trackController.getTotalPrice(), albumController.getTotalPrice());
        String hst = Double.toString(provinceController.findProvinces(Integer.parseInt(province)).getPst());
        String gst = Double.toString(provinceController.findProvinces(Integer.parseInt(province)).getGst());
        String pst = Double.toString(provinceController.findProvinces(Integer.parseInt(province)).getHst());

        double totalTaxedPrice = getTotalPriceWithTax(total, pst, gst, hst);

        return totalTaxedPrice;
    }

    /**
     * Method stores tracks into the order_tracks bridging table
     * 
     * @Korjon Chang-Jones Ibrahim Kebe
     */
    private void storeOrderTracks(List<Tracks> trackList, Orders order) {

        //creating the orderTrack
        for (Tracks track : trackList) {
            OrderTrack orderTrack = new OrderTrack();
            orderTrack.setOrderId(order);
            orderTrack.setPriceDuringOrder(track.getListPrice());
            orderTrack.setTrackId(track);
            try {
                orderTrackController.create(orderTrack);
            } catch (RollbackFailureException ex) {
                LOG.error("order track error");
            }
        }
    }

    /**
     * Method gets all of the tracks from every ordered album
     *
     * @param albums
     * @return
     * 
     * @Korjon Chang-Jones
     */
    private List<List<Tracks>> getTracksFromAlbums(List<Albums> albums) {

        List<List<Tracks>> allTracks = new ArrayList<>();

        for (var album : albums) {

            allTracks.add(trackController.findTracksByAlbum(album.getAlbumNumber()));
        }

        return allTracks;
    }

    /**
     * Sending an email of the order for the customer
     * Source: https://www.codespeedy.com/how-to-send-email-in-java-using-javax-mail-api/
     * @param order The client's order
     * @author Susan Vuu - 1735488
     */
    private void sendEmailToCustomer(Orders order){
        //Get the locale to display with the right language
        ResourceBundle languageBundle = ResourceBundle.getBundle("com.beatchamber.bundles.messages", localeChanger.getLocale());
        
        String toEmail = userLoginBean.getClient().getEmail();
        //Email from last semester
        String fromEmail = "svsender989@gmail.com";
        String fromPassword = "LeL9yq3nYvz867u";
        
        //Set up the gmail properties to send
        Properties properties = System.getProperties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");
        
        //Authenticate the sender
        Session session = Session.getInstance(properties, new Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(fromEmail, fromPassword);
         
            }
        });
        
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(languageBundle.getString("emailSubject") + this.orderController.getOrdersCount());
            message.setText(buildContent(languageBundle, order));
            
            Transport.send(message);
            LOG.debug(languageBundle.getString("emailSent"));
        } catch (MessagingException e) {
            LOG.error("sendEmailToCustomer():" + e.toString());
        }
    }
    
    /**
     * @param languageBundle The current language of the website
     * @param order The client's order
     * @return The content of the email
     * @author Susan Vuu - 1735488
     */
    private String buildContent(ResourceBundle languageBundle, Orders order){
        List<Tracks> purchasedTracks = this.trackController.retrieveAllTracksInTheCart();
        
        String content = languageBundle.getString("emailContentStart");
        for(Tracks track : purchasedTracks) {
            content = content + "\n" + track.getTrackTitle() + " " + languageBundle.getString("ofAlbum") + 
                    track.getAlbumNumber().getAlbumTitle() + " " + languageBundle.getString("by") +
                    track.getAlbumNumber().getArtistAlbumsList().get(0).getArtistId().getArtistName() +
                    " - " + track.getListPrice();
        }
        content = content + "\n" + languageBundle.getString("totalPrice") + order.getOrderGrossTotal();
        
        return content;
    }
}
