package com.beatchamber.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author kibra
 */
@Entity
@Table(name = "customer_reviews", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "CustomerReviews.findAll", query = "SELECT c FROM CustomerReviews c"),
    @NamedQuery(name = "CustomerReviews.findByReviewNumber", query = "SELECT c FROM CustomerReviews c WHERE c.reviewNumber = :reviewNumber"),
    @NamedQuery(name = "CustomerReviews.findByReviewDate", query = "SELECT c FROM CustomerReviews c WHERE c.reviewDate = :reviewDate"),
    @NamedQuery(name = "CustomerReviews.findByRating", query = "SELECT c FROM CustomerReviews c WHERE c.rating = :rating"),
    @NamedQuery(name = "CustomerReviews.findByReviewText", query = "SELECT c FROM CustomerReviews c WHERE c.reviewText = :reviewText"),
    @NamedQuery(name = "CustomerReviews.findByApprovalStatus", query = "SELECT c FROM CustomerReviews c WHERE c.approvalStatus = :approvalStatus")})
public class CustomerReviews implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "review_number")
    private Integer reviewNumber;
    @Basic(optional = false)
    @NotNull
    @Column(name = "review_date")
    @Temporal(TemporalType.DATE)
    private Date reviewDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "rating")
    private int rating;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "review_text")
    private String reviewText;
    @Basic(optional = false)
    @NotNull
    @Column(name = "approval_status")
    private boolean approvalStatus;
    @JoinColumn(name = "client_number", referencedColumnName = "client_number")
    @ManyToOne(optional = false)
    private Clients clientNumber;
    @JoinColumn(name = "track_id", referencedColumnName = "track_id")
    @ManyToOne(optional = false)
    private Tracks trackId;

    public CustomerReviews() {
    }

    public CustomerReviews(Integer reviewNumber) {
        this.reviewNumber = reviewNumber;
    }

    public CustomerReviews(Integer reviewNumber, Date reviewDate, int rating, String reviewText, boolean approvalStatus) {
        this.reviewNumber = reviewNumber;
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.reviewText = reviewText;
        this.approvalStatus = approvalStatus;
    }

    public Integer getReviewNumber() {
        return reviewNumber;
    }

    public void setReviewNumber(Integer reviewNumber) {
        this.reviewNumber = reviewNumber;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public boolean getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(boolean approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Clients getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(Clients clientNumber) {
        this.clientNumber = clientNumber;
    }

    public Tracks getTrackId() {
        return trackId;
    }

    public void setTrackId(Tracks trackId) {
        this.trackId = trackId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reviewNumber != null ? reviewNumber.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CustomerReviews)) {
            return false;
        }
        CustomerReviews other = (CustomerReviews) object;
        if ((this.reviewNumber == null && other.reviewNumber != null) || (this.reviewNumber != null && !this.reviewNumber.equals(other.reviewNumber))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.CustomerReviews[ reviewNumber=" + reviewNumber + " ]";
    }
    
}
