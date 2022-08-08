package com.beatchamber.beans.manager.report;

import com.beatchamber.entities.Orders;
import com.beatchamber.jpacontroller.OrderAlbumJpaController;
import com.beatchamber.jpacontroller.OrderTrackJpaController;
import com.beatchamber.jpacontroller.OrdersJpaController;
import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *This is the backing bean for the top seller tab in the report page
 * @author Massimo Di Girolamo
 */
@Named("TopSellersBean")
@ViewScoped
public class TopSellerBackingBean implements Serializable {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    OrdersJpaController ordersJpaController;

    @Inject
    OrderTrackJpaController orderTrackJpaController;

    @Inject
    OrderAlbumJpaController orderAlbumJpaController;
    
    private List<Orders> topOrders = new ArrayList<>();
    
    private int order_id;
    private double order_total;
    private Date order_date;

    
    private java.util.Date saleStartDate;
    private java.util.Date saleEndDate;

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
    
    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_total(double order_total) {
        this.order_total = order_total;
    }
    
    public double getOrder_total() {
        return order_total;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }
    
    public Date getOrder_date() {
        return order_date;
    }

    public java.util.Date getSaleStartDate() {
        return saleStartDate;
    }
    
    public void setSaleStartDate(java.util.Date saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    public java.util.Date getSaleEndDate() {
        return saleEndDate;
    }

    public void setSaleEndDate(java.util.Date saleEndDate) {
        this.saleEndDate = saleEndDate;
    }
    
    public List<Orders> getTopOrders(){
        return this.topOrders;
    }
    
    public void setTopOrders(List<Orders> ordList){
        this.topOrders = ordList;
    }
    
    public void findAllTopSellers(){
        this.topOrders = new ArrayList<>();
        
        List<Orders> orders = ordersJpaController.findOrdersEntities();
        for (Orders order : orders) {
            int orderId = order.getOrderId();
            this.findAllOrdersInBetweenDates(orderId, this.saleStartDate, this.saleEndDate);
        }
    }
    
    private void findAllOrdersInBetweenDates(int orderId, Date saleStartDate, Date saleEndDate){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Orders> cq = cb.createQuery(Orders.class);
        Root<Orders> rt = cq.from(Orders.class);
        cq.where(cb.between(rt.get("orderDate"), this.saleStartDate, this.saleEndDate));
        cq.orderBy(cb.desc(rt.get("orderTotal")));
        TypedQuery<Orders> query = entityManager.createQuery(cq);

        this.topOrders = query.getResultList();
    }
    
}
