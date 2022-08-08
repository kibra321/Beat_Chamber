/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.manager.report;

import com.beatchamber.entities.Albums;
import com.beatchamber.entities.OrderAlbum;
import com.beatchamber.entities.OrderTrack;
import com.beatchamber.entities.Tracks;
import com.beatchamber.jpacontroller.AlbumsJpaController;
import com.beatchamber.jpacontroller.TracksJpaController;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Yan Tang
 */
@Named("SalesByTrack")
@ViewScoped
public class SalesByTrackBackingBean implements Serializable {

    private String trackId;
    private Date saleStartDate;
    private Date saleEndDate;
    private List<OrderTrackInfo> trackSalesInfos = new ArrayList<>();
    private List<OrderTrackInfo> ordersTrackInfos = new ArrayList<>();
    ;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    TracksJpaController tracksJpaController;

    /**
     * get the track id
     *
     * @return
     */
    public String getTrackId() {
        return trackId;
    }

    /**
     * Set the track id
     *
     * @param trackId
     */
    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    /**
     * Get the start date of the search
     *
     * @return
     */
    public Date getSaleStartDate() {
        return this.saleStartDate;
    }

    /**
     * Set the start date of the search
     *
     * @param date
     */
    public void setSaleStartDate(Date date) {
        this.saleStartDate = date;
    }

    /**
     * Get the end date of the search
     *
     * @return
     */
    public Date getSaleEndDate() {
        return saleEndDate;
    }

    /**
     * Set the end date of the search
     *
     * @param date
     */
    public void setSaleEndDate(Date date) {
        this.saleEndDate = date;
    }

    /**
     * get TotalSales of the track
     *
     * @return the track order info
     */
    public List<OrderTrackInfo> getTrackSalesInfos() {
        return this.trackSalesInfos;
    }

    /**
     * Retrieve all the client orders.
     */
    public void retrieveAllOrders() {
        trackSalesInfos = new ArrayList<>();
        this.getAllTracksByID(saleStartDate, saleEndDate, Integer.parseInt(trackId));

    }

    /**
     * Get all the orders by using the track id.
     *
     * @param saleStartDate the start date
     * @param saleEndDate the end date
     * @param trackId the client id
     */
    private void getAllTracksByID(Date saleStartDate, Date saleEndDate, int trackId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderTrackInfo> cq = cb.createQuery(OrderTrackInfo.class);
        List<Predicate> predicates = new ArrayList<>();

        Root<OrderTrack> ordertrack = cq.from(OrderTrack.class);
        Join tracks = ordertrack.join("trackId");
        Join orders = ordertrack.join("orderId");
        Join albums = tracks.join("albumNumber");

        if (saleStartDate != null && saleEndDate != null) {
            predicates.add(cb.between(orders.get("orderDate"), this.saleStartDate, this.saleEndDate));
        }
        predicates.add(cb.equal(tracks.get("trackId"), trackId));
        cq.where(predicates.toArray(new Predicate[]{}));
        try {
            cq.select(cb.construct(OrderTrackInfo.class,
                    tracks.get("trackId"),
                    albums.get("albumNumber"),
                    albums.get("albumTitle"),
                    tracks.get("trackTitle"),
                    tracks.get("listPrice"),
                    tracks.get("salePrice"),
                    orders.get("orderDate")
            ));
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(OrdersByClientsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        final TypedQuery<OrderTrackInfo> typedQuery = entityManager.createQuery(cq);
        List<OrderTrackInfo> value = typedQuery.getResultList();
        if (!value.isEmpty()) {
            for (OrderTrackInfo orderTrackInfo : value) {
                OrderTrackInfo temp = new OrderTrackInfo(orderTrackInfo.getTrackId(),
                        orderTrackInfo.getAlbumNumber(), orderTrackInfo.getAlbumTitle(),
                        orderTrackInfo.getTrackTitle(), orderTrackInfo.getListPrice(),
                        orderTrackInfo.getSalePrice(), orderTrackInfo.getOrderDate());
                this.trackSalesInfos.add(temp);
            }
        }
    }
    
    /**
     * Get the total value of the track orders
     *
     * @return
     */
    public double getTotalTrackSales() {
        double amount = 0;
        for (OrderTrackInfo ordertrackInfo : this.trackSalesInfos) {
            if (ordertrackInfo.getSalePrice() != 0) {
                amount += ordertrackInfo.getSalePrice();
            } else {
                amount += ordertrackInfo.getListPrice();
            }
        }
        return amount;
    }
}
