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
@Named("ZeroTrackSales")
@ViewScoped
public class ZeroTracksBackingBean implements Serializable {

    private Date saleStartDate;
    private Date saleEndDate;
    private List<Tracks> zeroTrackSalesInfos = new ArrayList<>();
    private List<Tracks> ordersInfos;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    TracksJpaController tracksJpaController;

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
    public List<Tracks> getZeroTrackSalesInfos() {
        return this.zeroTrackSalesInfos;
    }

    /**
     * Retrieve all the client orders.
     */
    public void retrieveAllOrders() {
        zeroTrackSalesInfos = new ArrayList<>();
        List<Tracks> tracks = this.tracksJpaController.findTracksEntities();
        for (Tracks track : tracks) {
            int id = track.getTrackId();
            this.getAllTracksByID(saleStartDate, saleEndDate, id);
            if (this.ordersInfos.isEmpty()) {
                this.zeroTrackSalesInfos.add(track);
            }
        }

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
        CriteriaQuery<Tracks> cq = cb.createQuery(Tracks.class);
        List<Predicate> predicates = new ArrayList<>();

        Root<OrderTrack> ordertrack = cq.from(OrderTrack.class);
        Join tracks = ordertrack.join("trackId");
        Join orders = ordertrack.join("orderId");

        if (saleStartDate != null && saleEndDate != null) {
            predicates.add(cb.between(orders.get("orderDate"), this.saleStartDate, this.saleEndDate));
        }
        predicates.add(cb.equal(tracks.get("trackId"), trackId));
        cq.where(predicates.toArray(new Predicate[]{}));
        try {
            cq.select(cb.construct(Tracks.class,
                    tracks.get("trackId")
            ));
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(OrdersByClientsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        final TypedQuery<Tracks> typedQuery = entityManager.createQuery(cq);
        List<Tracks> value = typedQuery.getResultList();
        this.ordersInfos = value;
    }
}
