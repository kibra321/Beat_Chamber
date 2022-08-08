package com.beatchamber.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author kibra
 */
@Entity
@Table(name = "provinces", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Provinces.findAll", query = "SELECT p FROM Provinces p"),
    @NamedQuery(name = "Provinces.findByProvinceId", query = "SELECT p FROM Provinces p WHERE p.provinceId = :provinceId"),
    @NamedQuery(name = "Provinces.findByChoiceName", query = "SELECT p FROM Provinces p WHERE p.choiceName = :choiceName"),
    @NamedQuery(name = "Provinces.findByPst", query = "SELECT p FROM Provinces p WHERE p.pst = :pst"),
    @NamedQuery(name = "Provinces.findByGst", query = "SELECT p FROM Provinces p WHERE p.gst = :gst"),
    @NamedQuery(name = "Provinces.findByHst", query = "SELECT p FROM Provinces p WHERE p.hst = :hst")})
public class Provinces implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "province_id")
    private Integer provinceId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "choice_name")
    private String choiceName;
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

    public Provinces() {
    }

    public Provinces(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Provinces(Integer provinceId, String choiceName, double pst, double gst, double hst) {
        this.provinceId = provinceId;
        this.choiceName = choiceName;
        this.pst = pst;
        this.gst = gst;
        this.hst = hst;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getChoiceName() {
        return choiceName;
    }

    public void setChoiceName(String choiceName) {
        this.choiceName = choiceName;
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
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (provinceId != null ? provinceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Provinces)) {
            return false;
        }
        Provinces other = (Provinces) object;
        if ((this.provinceId == null && other.provinceId != null) || (this.provinceId != null && !this.provinceId.equals(other.provinceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.Provinces[ provinceId=" + provinceId + " ]";
    }
    
}
