/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.manager.report;

import com.beatchamber.entities.Clients;
import com.beatchamber.entities.Orders;
import com.beatchamber.jpacontroller.ClientsJpaController;
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
 * This class is used to show and topClient and zeroClient report in the report
 * management page.
 *
 * @author Yan Tang
 */
@Named("OrdersByClients")
@ViewScoped
public class OrdersByClientsBackingBean implements Serializable {

    private int clientID;
    private Date saleStartDate;
    private Date saleEndDate;
    private List<OrderClientInfo> ordersInfos;
    private List<TotalSaleAndClientsInfo> totalSaleAndClientsInfos = new ArrayList<>();
    private List<TotalSaleAndClientsInfo> zeroSaleAndClientsInfos = new ArrayList<>();
    private List<TotalSaleAndClientsInfo> saleByClientsInfos = new ArrayList<>();

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    ClientsJpaController clientsJpaController;

    /**
     * Get the client id
     *
     * @return
     */
    public int getClientID() {
        return this.clientID;
    }

    /**
     * Set the client id
     *
     * @param id
     */
    public void setClientId(int id) {
        this.clientID = id;
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
     * get TotalSales of the client
     *
     * @return the client info
     */
    public List<TotalSaleAndClientsInfo> getTotalSales() {
        return this.totalSaleAndClientsInfos;
    }

    /**
     * get Zero value purchased client
     *
     * @return the client info
     */
    public List<TotalSaleAndClientsInfo> getZeroTotalSales() {
        return this.zeroSaleAndClientsInfos;
    }

    /**
     * get all the info and items purchased by the client
     *
     * @return the client info
     */
    public List<TotalSaleAndClientsInfo> getSalesByClient() {
        return this.saleByClientsInfos;
    }

    /**
     * Retrieve all the client orders.
     */
    public void retrieveAllOrdersByClient() {
        totalSaleAndClientsInfos = new ArrayList<>();
        zeroSaleAndClientsInfos = new ArrayList<>();

        List<Clients> clients = clientsJpaController.findClientsEntities();
        for (Clients client : clients) {
            int id = client.getClientNumber();
            String username = client.getUsername();
            this.getAllOrdersByClientID(saleStartDate, saleEndDate, id);
            this.calculateTotalSalesEachClient(id, username);
        }

    }

    /**
     * Get all the orders by using the client id.
     *
     * @param saleStartDate the start date
     * @param saleEndDate the end date
     * @param clientID the client id
     */
    private void getAllOrdersByClientID(Date saleStartDate, Date saleEndDate, int clientID) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderClientInfo> cq = cb.createQuery(OrderClientInfo.class);
        List<Predicate> predicates = new ArrayList<>();

        Root<Orders> orders = cq.from(Orders.class);
        Join clients = orders.join("clientNumber");
        if (saleStartDate != null && saleEndDate != null) {
            predicates.add(cb.between(orders.get("orderDate"), this.saleStartDate, this.saleEndDate));
        }
        predicates.add(cb.equal(clients.get("clientNumber"), clientID));
        cq.where(predicates.toArray(new Predicate[]{}));
        try {
            cq.select(cb.construct(OrderClientInfo.class,
                    clients.get("clientNumber"),
                    clients.get("username"),
                    orders.get("orderTotal")
            ));
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(OrdersByClientsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        final TypedQuery<OrderClientInfo> typedQuery = entityManager.createQuery(cq);
        List<OrderClientInfo> value = typedQuery.getResultList();
        this.ordersInfos = value;
    }

    /**
     * Get the order total value of the order
     *
     * @param id
     * @param username
     */
    private void calculateTotalSalesEachClient(int id, String username) {
        if (this.ordersInfos.isEmpty()) {
            zeroSaleAndClientsInfos.add(new TotalSaleAndClientsInfo(0, username, id));
            return;
        }
        double amount = 0;
        for (OrderClientInfo ordersInfo : this.ordersInfos) {
            amount += ordersInfo.getOrderTotal();
        }
        TotalSaleAndClientsInfo temp = new TotalSaleAndClientsInfo(amount, username, id);
        totalSaleAndClientsInfos.add(temp);
    }
}
