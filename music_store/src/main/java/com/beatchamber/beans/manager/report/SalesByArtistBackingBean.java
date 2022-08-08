package com.beatchamber.beans.manager.report;

import com.beatchamber.entities.Artists;
import com.beatchamber.entities.Orders;
import com.beatchamber.jpacontroller.ArtistsJpaController;
import com.beatchamber.jpacontroller.OrderTrackJpaController;
import com.beatchamber.jpacontroller.OrdersJpaController;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This is the backing bean for the sales by artist tab in the reports page
 *
 * @author Massimo Di Girolamo
 */
@Named("SalesByArtistBean")
@ViewScoped
public class SalesByArtistBackingBean implements Serializable {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Inject
    private ArtistsJpaController artistsJpaController;
    
    @Inject
    private OrdersJpaController ordersJpaController;
    
    @Inject
    private OrderTrackJpaController orderTrackJpaController;
    
    private int artist_id;
    private String artist_name;
    private double order_total;
    
    
    private java.util.Date saleStartDate;
    private java.util.Date saleEndDate;
    
    private List<Artists> artistSalesList = new ArrayList<>();

    public void setArtistSalesList(List<Artists> artistSalesList) {
        this.artistSalesList = artistSalesList;
    }

    public List<Artists> getArtistSalesList() {
        return artistSalesList;
    }

    public void setArtist_id(int artist_id) {
        this.artist_id = artist_id;
    }

    public void setArtist_name(String album_name) {
        this.artist_name = album_name;
    }

    public void setOrder_total(double order_total) {
        this.order_total = order_total;
    }

    public void setSaleStartDate(Date saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    public void setSaleEndDate(Date saleEndDate) {
        this.saleEndDate = saleEndDate;
    }

    public int getArtist_id() {
        return artist_id;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public double getOrder_total() {
        return order_total;
    }

    public Date getSaleStartDate() {
        return saleStartDate;
    }

    public Date getSaleEndDate() {
        return saleEndDate;
    }
    
    
    public void findSalesByArtists(){
        this.artistSalesList = new ArrayList<>();
        
        List<Artists> artists = artistsJpaController.findArtistsEntities();
        for (Artists artist : artists) {
            int artistId = artist.getArtistId();
            //call helper method
        }
    }

}
