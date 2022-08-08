package com.beatchamber.beans.manager.report;

import com.beatchamber.entities.Albums;
import com.beatchamber.jpacontroller.AlbumsJpaController;
import com.beatchamber.jpacontroller.OrderAlbumJpaController;
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
 *
 * @author Massimo Di Girolamo
 */
@Named("SalesByAlbumBean")
@ViewScoped
public class SalesByAlbumBackingBean implements Serializable {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Inject
    private AlbumsJpaController artistsJpaController;
    
    @Inject
    private OrdersJpaController ordersJpaController;
    
    @Inject
    private OrderAlbumJpaController orderAlbumJpaController;
    
    private int album_number;
    private String album_title;
    private Date release_date;
    
    private double order_total;
    
    
    private java.util.Date saleStartDate;
    private java.util.Date saleEndDate;
    
    private List<Albums> topAlbumsSales = new ArrayList<>();

    public void setTopAlbumsSales(List<Albums> topAlbumsSales) {
        this.topAlbumsSales = topAlbumsSales;
    }

    public List<Albums> getTopAlbumsSales() {
        return topAlbumsSales;
    }

    public int getAlbum_number() {
        return album_number;
    }

    public String getAlbum_title() {
        return album_title;
    }

    public Date getRelease_date() {
        return release_date;
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

    public void setAlbum_number(int album_number) {
        this.album_number = album_number;
    }

    public void setAlbum_title(String album_title) {
        this.album_title = album_title;
    }

    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
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
    
}
