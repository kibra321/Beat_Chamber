package com.beatchamber.entities;

import com.beatchamber.jpacontroller.OrdersJpaController;
import java.io.Serializable;
import javax.inject.Inject;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author kibra
 */
@Entity
@Table(name = "order_track", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "OrderTrack.findAll", query = "SELECT o FROM OrderTrack o"),
    @NamedQuery(name = "OrderTrack.findByOrderId", query = "SELECT o FROM OrderTrack o WHERE o.orderId = :orderId")})




public class OrderTrack implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "price_during_order")
    private double priceDuringOrder;


    /*@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "tablekey")
    private Integer tablekey;
    @Basic(optional = false)
    @NotNull
    @Column(name = "order_id")
    private int orderId;*/
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "tablekey")
    private Integer tablekey;
    
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    @ManyToOne(optional = false)
    private Orders orderId;

    private static final long serialVersionUID = 1L;
    @JoinColumn(name = "track_id", referencedColumnName = "track_id")
    @ManyToOne(optional = false)
    private Tracks trackId;

    
    
    
    public OrderTrack() {
    }

    public OrderTrack(Orders orderId) {
        this.orderId = orderId;
    }

    public Orders getOrderId() {
        return orderId;
    }

    public void setOrderId(Orders orderId) {
        this.orderId = orderId;
    }

    public Tracks getTrackId() {
        return trackId;
    }
    
    public Integer getTablekey() {
        return tablekey;
    }

    public void setTablekey(Integer tablekey) {
        this.tablekey = tablekey;
    }

    public void setTrackId(Tracks trackId) {
        this.trackId = trackId;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.OrderTrack[ orderId=" + orderId + " ]";
    }

    public double getPriceDuringOrder() {
        return priceDuringOrder;
    }

    public void setPriceDuringOrder(double priceDuringOrder) {
        this.priceDuringOrder = priceDuringOrder;
    }




    
}
