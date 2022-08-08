/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.manager.report;

import java.util.Date;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author Yan Tang
 */
@Named
@RequestScoped
public class OrderAlbumInfo {

    private int albumid;

    private String albumTitle;

    private double listPrice;

    private double salePrice;

    private Date orderDate;

    /**
     * constructor
     */
    public OrderAlbumInfo() {

    }

    /**
     * constructor
     */
    public OrderAlbumInfo(int albumid, String albumTitle, double listPrice, double salePrice, Date orderDate) {
        this.albumid = albumid;
        this.albumTitle = albumTitle;
        this.listPrice = listPrice;
        this.salePrice = salePrice;
        this.orderDate = orderDate;
    }

    /**
     * get the album id
     *
     * @return
     */
    public int getAlbumId() {
        return albumid;
    }

    /**
     * get the list price
     *
     * @return
     */
    public double getListPrice() {
        return listPrice;
    }

    /**
     * get the sale price
     *
     * @return
     */
    public double getSalePrice() {
        return salePrice;
    }

    /**
     * get the album title
     *
     * @return
     */
    public String getAlbumTitle() {
        return albumTitle;
    }

    /**
     * get the order date
     *
     * @return
     */
    public Date getOrderDate() {
        return orderDate;
    }

    /**
     * set the album id
     *
     * @param albumid
     */
    public void setAlbumid(int albumid) {
        this.albumid = albumid;
    }

    /**
     * set the sales price
     *
     * @param salePrice
     */
    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    /**
     * set the list price
     *
     * @param listPrice
     */
    public void setListPrice(double listPrice) {
        this.listPrice = listPrice;
    }

    /**
     * set the album title
     *
     * @param albumTitle
     */
    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    /**
     * set the order date
     *
     * @param orderDate
     */
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

}
