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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Yan Tang
 */
@Named("TotalSales")
@ViewScoped
public class TotalSalesBackingBean implements Serializable {

    private Date saleStartDate;
    private Date saleEndDate;
    private List<OrderAlbumInfo> totalAlbumSalesInfos = new ArrayList<>();
    private List<OrderTrackInfo> totalTrackSalesInfos = new ArrayList<>();
    private final String[] category = {"Album", "Track"};

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    AlbumsJpaController albumsJpaController;

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

    public String[] getCategory() {
        return category;
    }

    /**
     * get TotalSales of the albums
     *
     * @return the album order info
     */
    public List<OrderAlbumInfo> getTotalAlbumSalesInfos() {
        return this.totalAlbumSalesInfos;
    }

    /**
     * get TotalSales of the track
     *
     * @return the track order info
     */
    public List<OrderTrackInfo> getTotalTrackSalesInfos() {
        return this.totalTrackSalesInfos;
    }

    /**
     * Retrieve all the client orders.
     */
    public void retrieveAllOrders() {
        totalAlbumSalesInfos = new ArrayList<>();
        totalTrackSalesInfos = new ArrayList<>();

        List<Albums> albums = this.albumsJpaController.findAlbumsEntities();
        for (Albums album : albums) {
            int id = album.getAlbumNumber();
            String albumtitle = album.getAlbumTitle();
            this.getAllAlbumsByID(saleStartDate, saleEndDate, id);
        }

        List<Tracks> tracks = this.tracksJpaController.findTracksEntities();
        for (Tracks track : tracks) {
            int id = track.getTrackId();
            String tracktitle = track.getTrackTitle();
            this.getAllTracksByID(saleStartDate, saleEndDate, id);
        }

    }

    /**
     * Get all the orders by using the album id.
     *
     * @param saleStartDate the start date
     * @param saleEndDate the end date
     * @param AlbumNumber the client id
     */
    private void getAllAlbumsByID(Date saleStartDate, Date saleEndDate, int AlbumNumber) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderAlbumInfo> cq = cb.createQuery(OrderAlbumInfo.class);
        List<Predicate> predicates = new ArrayList<>();

        Root<OrderAlbum> orderalbum = cq.from(OrderAlbum.class);
        Join albums = orderalbum.join("albumId");
        Join orders = orderalbum.join("orderId");
        if (saleStartDate != null && saleEndDate != null) {
            predicates.add(cb.between(orders.get("orderDate"), this.saleStartDate, this.saleEndDate));
        }
        predicates.add(cb.equal(albums.get("albumNumber"), AlbumNumber));
        cq.where(predicates.toArray(new Predicate[]{}));
        try {
            cq.select(cb.construct(OrderAlbumInfo.class,
                    albums.get("albumNumber"),
                    albums.get("albumTitle"),
                    albums.get("listPrice"),
                    albums.get("salePrice"),
                    orders.get("orderDate")
            ));
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(TotalSalesBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        final TypedQuery<OrderAlbumInfo> typedQuery = entityManager.createQuery(cq);
        List<OrderAlbumInfo> value = typedQuery.getResultList();
        if (!value.isEmpty()) {
            for (OrderAlbumInfo orderAlbumInfo : value) {
                OrderAlbumInfo temp = new OrderAlbumInfo(orderAlbumInfo.getAlbumId(),
                        orderAlbumInfo.getAlbumTitle(), orderAlbumInfo.getListPrice(),
                        orderAlbumInfo.getSalePrice(), orderAlbumInfo.getOrderDate());
                this.totalAlbumSalesInfos.add(temp);
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
            java.util.logging.Logger.getLogger(TotalSalesBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        final TypedQuery<OrderTrackInfo> typedQuery = entityManager.createQuery(cq);
        List<OrderTrackInfo> value = typedQuery.getResultList();
        if (!value.isEmpty()) {
            for (OrderTrackInfo orderTrackInfo : value) {
                OrderTrackInfo temp = new OrderTrackInfo(orderTrackInfo.getTrackId(),
                        orderTrackInfo.getAlbumNumber(), orderTrackInfo.getAlbumTitle(),
                        orderTrackInfo.getTrackTitle(), orderTrackInfo.getListPrice(),
                        orderTrackInfo.getSalePrice(), orderTrackInfo.getOrderDate());
                this.totalTrackSalesInfos.add(temp);
            }
        }
    }

    /**
     * Get the total value of the album orders
     *
     * @return
     */
    public double getTotalAlbumSales() {
        double amount = 0;
        for (OrderAlbumInfo orderalbumInfo : this.totalAlbumSalesInfos) {
            if (orderalbumInfo.getSalePrice() != 0) {
                amount += orderalbumInfo.getSalePrice();
            } else {
                amount += orderalbumInfo.getListPrice();
            }
        }
        return amount;
    }

    /**
     * Get the total value of the track orders
     *
     * @return
     */
    public double getTotalTrackSales() {
        double amount = 0;
        for (OrderTrackInfo ordertrackInfo : this.totalTrackSalesInfos) {
            if (ordertrackInfo.getSalePrice() != 0) {
                amount += ordertrackInfo.getSalePrice();
            } else {
                amount += ordertrackInfo.getListPrice();
            }
        }
        return amount;
    }
}
