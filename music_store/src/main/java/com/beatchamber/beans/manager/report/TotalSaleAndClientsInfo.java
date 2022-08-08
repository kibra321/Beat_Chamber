/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.manager.report;

/**
 *
 * @author Yan Tang
 */
public class TotalSaleAndClientsInfo {

    private double total;
    private String username;
    private int clientID;

    /**
     * constructor
     */
    public TotalSaleAndClientsInfo(double total, String username, int clientID) {
        this.total = total;
        this.username = username;
        this.clientID = clientID;
    }

    /**
     * get the total amount
     *
     * @return
     */
    public double getTotal() {
        return total;
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
     * get the client id
     *
     * @return
     */
    public int getClientID() {
        return clientID;
    }

    /**
     * set the total amount
     *
     * @param total
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * set the username
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * set the client id
     *
     * @param clientID
     */
    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

}
