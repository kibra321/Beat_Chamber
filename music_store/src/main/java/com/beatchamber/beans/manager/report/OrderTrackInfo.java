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
public class OrderTrackInfo {

    private int trackid;

    private int albumNumber;

    private String albumTitle;

    private String trackTitle;

    private double listPrice;

    private double salePrice;

    private Date orderDate;

    /**
     * constructor
     */
    public OrderTrackInfo() {

    }

    /**
     * constructor
     */
    public OrderTrackInfo(int trackid, int albumNumber, String albumTitle, String trackTitle, double listPrice, double salePrice, Date orderDate) {
        this.trackid = trackid;
        this.albumNumber = albumNumber;
        this.albumTitle = albumTitle;
        this.trackTitle = trackTitle;
        this.listPrice = listPrice;
        this.salePrice = salePrice;
        this.orderDate = orderDate;
    }

    /**
     * get the track id
     *
     * @return
     */
    public int getTrackId() {
        return trackid;
    }

    /**
     * get the album id
     *
     * @return
     */
    public int getAlbumNumber() {
        return albumNumber;
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
     * get the sales price
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
     * get the track title
     *
     * @return
     */
    public String getTrackTitle() {
        return trackTitle;
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
     * set the track id
     *
     * @param trackid
     */
    public void setTrackid(int trackid) {
        this.trackid = trackid;
    }

    /**
     * set the album id
     *
     * @param albumNumber
     */
    public void setAlbumNumber(int albumNumber) {
        this.albumNumber = albumNumber;
    }

    /**
     * set the sale price
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
     * set the track title
     *
     * @param trackTitle
     */
    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
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
