/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
@Table(name = "ads", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Ads.findAll", query = "SELECT a FROM Ads a"),
    @NamedQuery(name = "Ads.findByAdId", query = "SELECT a FROM Ads a WHERE a.adId = :adId"),
    @NamedQuery(name = "Ads.findByFileName", query = "SELECT a FROM Ads a WHERE a.fileName = :fileName"),
    @NamedQuery(name = "Ads.findByLink", query = "SELECT a FROM Ads a WHERE a.link = :link"),
    @NamedQuery(name = "Ads.findByEnabled", query = "SELECT a FROM Ads a WHERE a.enabled = :enabled")})
public class Ads implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ad_id")
    private Integer adId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "file_name")
    private String fileName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "link")
    private String link;
    @Column(name = "enabled")
    private boolean enabled;

    public Ads() {
    }

    public Ads(Integer adId) {
        this.adId = adId;
    }

    public Ads(Integer adId, String fileName, String link) {
        this.adId = adId;
        this.fileName = fileName;
        this.link = link;
    }

    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (adId != null ? adId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ads)) {
            return false;
        }
        Ads other = (Ads) object;
        if ((this.adId == null && other.adId != null) || (this.adId != null && !this.adId.equals(other.adId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.Ads[ adId=" + adId + " ]";
    }

}
