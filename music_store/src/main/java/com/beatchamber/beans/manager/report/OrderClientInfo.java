/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.manager.report;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author Yan Tang
 */
@Named
@RequestScoped
public class OrderClientInfo {

    private int clientNumber;

    private double orderTotal;

    private String username;

    /**
     * constructor
     */
    public OrderClientInfo() {

    }

    /**
     * constructor
     */
    public OrderClientInfo(int clientID, String userName, double orderTotal) {
        this.clientNumber = clientID;
        this.orderTotal = orderTotal;
        this.username = userName;
    }

    /**
     * get the client number
     *
     * @return
     */
    public int getClientNumber() {
        return clientNumber;
    }

    /**
     * get the order total
     *
     * @return
     */
    public double getOrderTotal() {
        return orderTotal;
    }

    /**
     * get the username
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * set the client number
     *
     * @param clientNumber
     */
    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }

    /**
     * set the order total
     *
     * @param orderTotal
     */
    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }

    /**
     * set the username
     *
     * @param userName
     */
    public void setUsername(String userName) {
        this.username = userName;
    }

}
