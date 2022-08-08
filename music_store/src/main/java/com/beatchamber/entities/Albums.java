/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author kibra
 */
@Entity
@Table(name = "albums", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Albums.findAll", query = "SELECT a FROM Albums a"),
    @NamedQuery(name = "Albums.findByAlbumNumber", query = "SELECT a FROM Albums a WHERE a.albumNumber = :albumNumber"),
    @NamedQuery(name = "Albums.findByAlbumTitle", query = "SELECT a FROM Albums a WHERE a.albumTitle = :albumTitle"),
    @NamedQuery(name = "Albums.findByReleaseDate", query = "SELECT a FROM Albums a WHERE a.releaseDate = :releaseDate"),
    @NamedQuery(name = "Albums.findByRecordingLabel", query = "SELECT a FROM Albums a WHERE a.recordingLabel = :recordingLabel"),
    @NamedQuery(name = "Albums.findByTotalTracks", query = "SELECT a FROM Albums a WHERE a.totalTracks = :totalTracks"),
    @NamedQuery(name = "Albums.findByEntryDate", query = "SELECT a FROM Albums a WHERE a.entryDate = :entryDate"),
    @NamedQuery(name = "Albums.findByCostPrice", query = "SELECT a FROM Albums a WHERE a.costPrice = :costPrice"),
    @NamedQuery(name = "Albums.findByListPrice", query = "SELECT a FROM Albums a WHERE a.listPrice = :listPrice"),
    @NamedQuery(name = "Albums.findBySalePrice", query = "SELECT a FROM Albums a WHERE a.salePrice = :salePrice"),
    @NamedQuery(name = "Albums.findByRemovalStatus", query = "SELECT a FROM Albums a WHERE a.removalStatus = :removalStatus"),
    @NamedQuery(name = "Albums.findByRemovalDate", query = "SELECT a FROM Albums a WHERE a.removalDate = :removalDate")})
public class Albums implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "album_title")
    private String albumTitle;
    @Basic(optional = false)
    @NotNull()
    @Column(name = "release_date")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "recording_label")
    private String recordingLabel;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_tracks")
    private int totalTracks;
    @Basic(optional = false)
    @NotNull
    @Column(name = "entry_date")
    @Temporal(TemporalType.DATE)
    private Date entryDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cost_price")
    private double costPrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "list_price")
    private double listPrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sale_price")
    private double salePrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "removal_status")
    private boolean removalStatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "albumId")
    private Collection<OrderAlbum> orderAlbumCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "album_number")
    private Integer albumNumber;
    @Column(name = "removal_date")
    @Temporal(TemporalType.DATE)
    private Date removalDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "albumNumber")
    private List<ArtistAlbums> artistAlbumsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "albumNumber")
    private List<Tracks> tracksList;
    @OneToMany(mappedBy = "albumNumber")
    private List<GenreToAlbum> genreToAlbumList;

    public Albums() {
    }

    public Albums(Integer albumNumber) {
        this.albumNumber = albumNumber;
    }

    public Albums(Integer albumNumber, String albumTitle, Date releaseDate, String recordingLabel, int totalTracks, Date entryDate, double costPrice, double listPrice, double salePrice, boolean removalStatus) {
        this.albumNumber = albumNumber;
        this.albumTitle = albumTitle;
        this.releaseDate = releaseDate;
        this.recordingLabel = recordingLabel;
        this.totalTracks = totalTracks;
        this.entryDate = entryDate;
        this.costPrice = costPrice;
        this.listPrice = listPrice;
        this.salePrice = salePrice;
        this.removalStatus = removalStatus;
    }

    public Integer getAlbumNumber() {
        return albumNumber;
    }

    public void setAlbumNumber(Integer albumNumber) {
        this.albumNumber = albumNumber;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRecordingLabel() {
        return recordingLabel;
    }

    public void setRecordingLabel(String recordingLabel) {
        this.recordingLabel = recordingLabel;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(int totalTracks) {
        this.totalTracks = totalTracks;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getListPrice() {
        return listPrice;
    }

    public void setListPrice(double listPrice) {
        this.listPrice = listPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public boolean getRemovalStatus() {
        return removalStatus;
    }

    public void setRemovalStatus(boolean removalStatus) {
        this.removalStatus = removalStatus;
    }

    public Date getRemovalDate() {
        return removalDate;
    }

    public void setRemovalDate(Date removalDate) {
        this.removalDate = removalDate;
    }

    public List<ArtistAlbums> getArtistAlbumsList() {
        return artistAlbumsList;
    }

    public void setArtistAlbumsList(List<ArtistAlbums> artistAlbumsList) {
        this.artistAlbumsList = artistAlbumsList;
    }

    public List<Tracks> getTracksList() {
        return tracksList;
    }

    public void setTracksList(List<Tracks> tracksList) {
        this.tracksList = tracksList;
    }

    public List<GenreToAlbum> getGenreToAlbumList() {
        return genreToAlbumList;
    }

    public void setGenreToAlbumList(List<GenreToAlbum> genreToAlbumList) {
        this.genreToAlbumList = genreToAlbumList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (albumNumber != null ? albumNumber.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Albums)) {
            return false;
        }
        Albums other = (Albums) object;
        if ((this.albumNumber == null && other.albumNumber != null) || (this.albumNumber != null && !this.albumNumber.equals(other.albumNumber))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.Albums[ albumNumber=" + albumNumber + " ]";
    }

    public Collection<OrderAlbum> getOrderAlbumCollection() {
        return orderAlbumCollection;
    }

    public void setOrderAlbumCollection(Collection<OrderAlbum> orderAlbumCollection) {
        this.orderAlbumCollection = orderAlbumCollection;
    }
    
    public String getFormatedDate(){
        return this.releaseDate.toInstant().toString().split("T")[0];
    }
    
    public String getCookieId(){
        return "a"+this.albumNumber;
    }
    
}