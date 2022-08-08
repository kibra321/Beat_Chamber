package com.beatchamber.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author kibra
 */
@Entity
@Table(name = "orders", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Orders.findAll", query = "SELECT o FROM Orders o"),
    @NamedQuery(name = "Orders.findByOrderId", query = "SELECT o FROM Orders o WHERE o.orderId = :orderId"),
    @NamedQuery(name = "Orders.findByOrderDate", query = "SELECT o FROM Orders o WHERE o.orderDate = :orderDate")})
public class Orders implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "order_total")
    private double orderTotal;
    @Basic(optional = false)
    @NotNull
    @Column(name = "order_gross_total")
    private double orderGrossTotal;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "pst")
    private Double pst;
    @Column(name = "gst")
    private Double gst;
    @Column(name = "hst")
    private Double hst;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderId")
    private Collection<OrderTrack> orderTrackCollection;
    @OneToMany(mappedBy = "orderId")
    private Collection<OrderAlbum> orderAlbumCollection;

    /*@Basic(optional = false)
    @NotNull
    @Column(name = "order_id")
    private int orderId;*/
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "visible")
    private Boolean visible;

    private static final long serialVersionUID = 1L;
    @Column(name = "order_date")
    @Temporal(TemporalType.DATE)
    private Date orderDate;
    @JoinColumn(name = "client_number", referencedColumnName = "client_number")
    @ManyToOne(optional = false)
    private Clients clientNumber;

    public Orders() {
    }

    public Orders(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Clients getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(Clients clientNumber) {
        this.clientNumber = clientNumber;
    }


    @Override
    public String toString() {
        return "com.beatchamber.entities.Orders[ orderId=" + orderId + " ]";
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }



    /*public Orders(Integer tablekey, int orderId) {
        this.tablekey = tablekey;
        this.orderId = orderId;
    }

    public Integer getTablekey() {
        return tablekey;
    }

    public void setTablekey(Integer tablekey) {
        this.tablekey = tablekey;
    }*/


    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Collection<OrderTrack> getOrderTrackCollection() {
        return orderTrackCollection;
    }

    public void setOrderTrackCollection(Collection<OrderTrack> orderTrackCollection) {
        this.orderTrackCollection = orderTrackCollection;
    }

    public Collection<OrderAlbum> getOrderAlbumCollection() {
        return orderAlbumCollection;
    }

    public void setOrderAlbumCollection(Collection<OrderAlbum> orderAlbumCollection) {
        this.orderAlbumCollection = orderAlbumCollection;
    }

    public Double getPst() {
        return pst;
    }

    public void setPst(Double pst) {
        this.pst = pst;
    }

    public Double getGst() {
        return gst;
    }

    public void setGst(Double gst) {
        this.gst = gst;
    }

    public Double getHst() {
        return hst;
    }

    public void setHst(Double hst) {
        this.hst = hst;
    }

    public double getOrderGrossTotal() {
        return orderGrossTotal;
    }

    public void setOrderGrossTotal(double orderGrossTotal) {
        this.orderGrossTotal = orderGrossTotal;
    }




    
}
