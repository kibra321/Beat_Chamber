package com.beatchamber.beans.manager;

import com.beatchamber.entities.OrderTrack;
import com.beatchamber.entities.Orders;
import com.beatchamber.entities.Tracks;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.jpacontroller.OrderAlbumJpaController;
import com.beatchamber.jpacontroller.OrderTrackJpaController;
import com.beatchamber.jpacontroller.OrdersJpaController;
import com.beatchamber.jpacontroller.TracksJpaController;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *This class will be used as the bean for the order management page
 * @author Massimo Di Girolamo
 */
@Named
@RequestScoped
public class OrderBean implements Serializable {
    
    private final static Logger LOG = LoggerFactory.getLogger(OrderBean.class);

    @Inject
    private OrdersJpaController ordersJpaController;
    
    @Inject
    private OrderAlbumJpaController orderAlbumJpaController;
    
    @Inject
    private OrderTrackJpaController orderTrackJpaController;
    
    @Inject
    private TracksJpaController tracksJpaController;
    
    private String trackName;
    
    private List<OrderTrack> orderTrackList;
    
    private List<Orders> ordersList;

    private Orders selectedOrder;
    
    private List<Orders> filteredOrdersList;
    
    //All the db values
    private int order_id;
    private double order_total;
    private int client_number;
    private Date order_date;
    private boolean visible;
    
    //Order Track
    private int orderTrackId;
    
    //Order Album
    private int orderAlbumId;
    
    /**
     * Initialization.
     */
    @PostConstruct
    public void init() {
        this.ordersList = ordersJpaController.findOrdersEntities();
        //this.orderTrackList = orderTrackJpaController.findOrderTrackEntities();
    }
    
    /**
     * Constructor
     */
    public OrderBean(){
        this.selectedOrder = new Orders();
    }
    
    //Getters and setters for all the dv values
    
    public int getOrder_id(){
        return this.order_id;
    }
    
    public void setOrder_id(int ordId){
        this.order_id = ordId;
    }
    
    public double getOrder_total(){
        return this.order_total;
    }
    
    public void setOrder_total(double ordTotal){
        this.order_total = ordTotal;
    }
    
    public int getClient_number(){
        return this.client_number;
    }
    
    public void setClient_number(int clntNum){
        this.client_number = clntNum;
    }
    
    public Date getOrder_date(){
        return this.order_date;
    }
    
    public void setOrder_date(Date ordDate){
        this.order_date = ordDate;
    }
    
    public boolean getVisible(){
        return this.visible;
    }
    
    public void setVisible(boolean isVisible){
        this.visible = isVisible;
    }
    
    public int getOrderTrackId(){
        return this.orderTrackId;
    }
    
    public void setOrderTrackId(int trackId){
        this.orderTrackId = trackId;
    }
    
    public String getTrackName(){
        return this.trackName;
    }
    
    public void setTrackName(String trckName){
        this.trackName = trckName;
    }
    
    public int getOrderAlbumId(){
        return this.orderAlbumId;
    }
    
    public void setOrderAlbumId(int albumId){
        this.orderAlbumId = albumId;
    }
    
    
    /**
     * Get all the orders.
     *
     * @return a list of orders in the database.
     */
    public List<Orders> getOrdersList() {

        return ordersList;
    }
    
    /**
     * Method that gets all the tracks in an order
     * @param intOrderId
     * @return 
     */
    public List<OrderTrack> getOrderTrackList(int intOrderId){
        Orders specificOrder = this.ordersJpaController.findOrders(intOrderId);
        return this.orderTrackList = this.orderTrackJpaController.getOrderTracksByOrderId(specificOrder);
    }

    /**
     * Get the selected the order.
     *
     * @return the selected order.
     */
    public Orders getSelectedOrder() {
        return selectedOrder;
    }
    
    /**
     * Set the selected order.
     *
     * @param selectedOrder the selected order.
     */
    public void setSelectedOrder(Orders selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    /**
     * Get the filteredOrdersList.
     *
     * @return a list of filteredOrdersList.
     */
    public List<Orders> getFilteredOrdersList() {
        return filteredOrdersList;
    }

    /**
     * Set the filteredOrdersList.
     *
     * @param filteredOrdersList a list of filteredOrdersList.
     */
    public void setFilteredOrdersList(List<Orders> filteredOrdersList) {
        this.filteredOrdersList = filteredOrdersList;
    }
    
    
    /**
     * The message that shows when visibility changes
     */
    public void showVisibleMessage(){
        if(this.visible){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The order is visible"));
        }
        
        else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The order is no longer visible"));
        }
        
    }
    
    public void changeVisibility(Orders order) throws NonexistentEntityException, Exception{
        
        this.selectedOrder = order;
        this.visible = order.getVisible();
        LOG.debug("Log order track collection " + order.getOrderId());
        LOG.debug("About to change the visibility of the order");
        LOG.debug("Order visibility is currently: " + order.getVisible());
        
        boolean newVis = changeVis(this.visible);
        
        //this.selectedOrder.setVisible(newVis);
        this.selectedOrder.setPst(20.0);
        this.ordersJpaController.edit(this.selectedOrder);
        LOG.debug("Visibility is now " + newVis);
    }
    
    /**
     * Private method that takes the visibility and changes its value
     * @param bool
     * @return 
     */
    private boolean changeVis(boolean bool){
        if(bool){
            return false;
        }
        
        else{
            return true;
        }
    }
    
    public String returnTrackName(int trackId){
         //to display for review page
            Tracks track = (tracksJpaController.findTracks(trackId));
            this.trackName = track.getTrackTitle();
            return this.trackName;
    }
    
    
}
