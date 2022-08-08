package com.beatchamber.entities;

import com.beatchamber.jpacontroller.OrderAlbumJpaController;
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
@Table(name = "order_album", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "OrderAlbum.findAll", query = "SELECT o FROM OrderAlbum o"),
    @NamedQuery(name = "OrderAlbum.findByOrderId", query = "SELECT o FROM OrderAlbum o WHERE o.orderId = :orderId")})
public class OrderAlbum implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "price_during_order")
    private double priceDuringOrder;

    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    @ManyToOne(optional = false)
    private Orders orderId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "tablekey")
    private Integer tablekey;

    private static final long serialVersionUID = 1L;
    

    /*@Basic(optional = false)
    @NotNull
    @Column(name = "order_id")
    private Integer orderId;*/
    @JoinColumn(name = "album_id", referencedColumnName = "album_number")
    @ManyToOne(optional = false)
    private Albums albumId;

    public OrderAlbum() {
    }

    public OrderAlbum(Orders orderId) {
        this.orderId = orderId;
    }

    
    public Integer getTablekey() {
        return tablekey;
    }

    public void setTablekey(Integer tablekey) {
        this.tablekey = tablekey;
    }
    
    public Orders getOrderId() {
        return orderId;
    }

    public void setOrderId(Orders orderId) {
        this.orderId = orderId;
    }

    public Albums getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Albums albumId) {
        this.albumId = albumId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderId != null ? orderId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrderAlbum)) {
            return false;
        }
        OrderAlbum other = (OrderAlbum) object;
        if ((this.orderId == null && other.orderId != null) || (this.orderId != null && !this.orderId.equals(other.orderId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.OrderAlbum[ orderId=" + orderId + " ]";
    }

    public double getPriceDuringOrder() {
        return priceDuringOrder;
    }

    public void setPriceDuringOrder(double priceDuringOrder) {
        this.priceDuringOrder = priceDuringOrder;
    }



    
}
