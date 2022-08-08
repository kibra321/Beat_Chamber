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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "tracks", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Tracks.findAll", query = "SELECT t FROM Tracks t"),
    @NamedQuery(name = "Tracks.findByTrackId", query = "SELECT t FROM Tracks t WHERE t.trackId = :trackId"),
    @NamedQuery(name = "Tracks.findByTrackTitle", query = "SELECT t FROM Tracks t WHERE t.trackTitle = :trackTitle"),
    @NamedQuery(name = "Tracks.findByPlayLength", query = "SELECT t FROM Tracks t WHERE t.playLength = :playLength"),
    @NamedQuery(name = "Tracks.findBySelectionNumber", query = "SELECT t FROM Tracks t WHERE t.selectionNumber = :selectionNumber"),
    @NamedQuery(name = "Tracks.findByMusicCategory", query = "SELECT t FROM Tracks t WHERE t.musicCategory = :musicCategory"),
    @NamedQuery(name = "Tracks.findByCostPrice", query = "SELECT t FROM Tracks t WHERE t.costPrice = :costPrice"),
    @NamedQuery(name = "Tracks.findByListPrice", query = "SELECT t FROM Tracks t WHERE t.listPrice = :listPrice"),
    @NamedQuery(name = "Tracks.findBySalePrice", query = "SELECT t FROM Tracks t WHERE t.salePrice = :salePrice"),
    @NamedQuery(name = "Tracks.findByEntryDate", query = "SELECT t FROM Tracks t WHERE t.entryDate = :entryDate"),
    @NamedQuery(name = "Tracks.findByRemoved", query = "SELECT t FROM Tracks t WHERE t.removed = :removed"),
    @NamedQuery(name = "Tracks.findByPst", query = "SELECT t FROM Tracks t WHERE t.pst = :pst"),
    @NamedQuery(name = "Tracks.findByGst", query = "SELECT t FROM Tracks t WHERE t.gst = :gst"),
    @NamedQuery(name = "Tracks.findByHst", query = "SELECT t FROM Tracks t WHERE t.hst = :hst")})
public class Tracks implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "track_title")
    private String trackTitle;
    @Basic(optional = false)
    @NotNull()
    @Size(min = 1, max = 10)
    @Column(name = "play_length")
    private String playLength;
    @Basic(optional = false)
    @NotNull
    @Column(name = "selection_number")
    private int selectionNumber;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "music_category")
    private String musicCategory;
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
    @Column(name = "entry_date")
    @Temporal(TemporalType.DATE)
    private Date entryDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "removed")
    private boolean removed;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pst")
    private double pst;
    @Basic(optional = false)
    @NotNull
    @Column(name = "gst")
    private double gst;
    @Basic(optional = false)
    @NotNull
    @Column(name = "hst")
    private double hst;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "trackId")
    private Collection<ArtistsToTracks> artistsToTracksCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "trackId")
    private Collection<OrderTrack> orderTrackCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "track_id")
    private Integer trackId;
    @JoinColumn(name = "album_number", referencedColumnName = "album_number")
    @ManyToOne(optional = false)
    private Albums albumNumber;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "trackId")
    private List<CustomerReviews> customerReviewsList;
    @OneToMany(mappedBy = "trackId")
    private List<GenreToTracks> genreToTracksList;

    public Tracks() {
    }

    public Tracks(Integer trackId) {
        this.trackId = trackId;
    }

    public Tracks(Integer trackId, String trackTitle, String playLength, int selectionNumber, String musicCategory, double costPrice, double listPrice, double salePrice, Date entryDate, boolean removed, double pst, double gst, double hst) {
        this.trackId = trackId;
        this.trackTitle = trackTitle;
        this.playLength = playLength;
        this.selectionNumber = selectionNumber;
        this.musicCategory = musicCategory;
        this.costPrice = costPrice;
        this.listPrice = listPrice;
        this.salePrice = salePrice;
        this.entryDate = entryDate;
        this.removed = removed;
        this.pst = pst;
        this.gst = gst;
        this.hst = hst;
    }

    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getPlayLength() {
        return playLength;
    }

    public void setPlayLength(String playLength) {
        this.playLength = playLength;
    }

    public int getSelectionNumber() {
        return selectionNumber;
    }

    public void setSelectionNumber(int selectionNumber) {
        this.selectionNumber = selectionNumber;
    }

    public String getMusicCategory() {
        return musicCategory;
    }

    public void setMusicCategory(String musicCategory) {
        this.musicCategory = musicCategory;
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

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Albums getAlbumNumber() {
        return albumNumber;
    }

    public void setAlbumNumber(Albums albumNumber) {
        this.albumNumber = albumNumber;
    }

    public List<CustomerReviews> getCustomerReviewsList() {
        return customerReviewsList;
    }

    public void setCustomerReviewsList(List<CustomerReviews> customerReviewsList) {
        this.customerReviewsList = customerReviewsList;
    }

    public List<GenreToTracks> getGenreToTracksList() {
        return genreToTracksList;
    }

    public void setGenreToTracksList(List<GenreToTracks> genreToTracksList) {
        this.genreToTracksList = genreToTracksList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (trackId != null ? trackId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tracks)) {
            return false;
        }
        Tracks other = (Tracks) object;
        if ((this.trackId == null && other.trackId != null) || (this.trackId != null && !this.trackId.equals(other.trackId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.Tracks[ trackId=" + trackId + " ]";
    }
    public Collection<OrderTrack> getOrderTrackCollection() {
        return orderTrackCollection;
    }
    public void setOrderTrackCollection(Collection<OrderTrack> orderTrackCollection) {
        this.orderTrackCollection = orderTrackCollection;
    }
    public Collection<ArtistsToTracks> getArtistsToTracksCollection() {
        return artistsToTracksCollection;
    }
    public void setArtistsToTracksCollection(Collection<ArtistsToTracks> artistsToTracksCollection) {
        this.artistsToTracksCollection = artistsToTracksCollection;
    }

    public boolean getRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public double getPst() {
        return pst;
    }

    public void setPst(double pst) {
        this.pst = pst;
    }

    public double getGst() {
        return gst;
    }

    public void setGst(double gst) {
        this.gst = gst;
    }

    public double getHst() {
        return hst;
    }

    public void setHst(double hst) {
        this.hst = hst;
    }
    
}