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
@Table(name = "rss_feeds", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "RssFeeds.findAll", query = "SELECT r FROM RssFeeds r"),
    @NamedQuery(name = "RssFeeds.findByRssId", query = "SELECT r FROM RssFeeds r WHERE r.rssId = :rssId"),
    @NamedQuery(name = "RssFeeds.findByLink", query = "SELECT r FROM RssFeeds r WHERE r.link = :link"),
    @NamedQuery(name = "RssFeeds.findByEnabled", query = "SELECT r FROM RssFeeds r WHERE r.enabled = :enabled")})
public class RssFeeds implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "rss_id")
    private Integer rssId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "link")
    private String link;
    @Column(name = "enabled")
    private boolean enabled;

    public RssFeeds() {
    }

    public RssFeeds(Integer rssId) {
        this.rssId = rssId;
    }

    public RssFeeds(Integer rssId, String link) {
        this.rssId = rssId;
        this.link = link;
    }

    public Integer getRssId() {
        return rssId;
    }

    public void setRssId(Integer rssId) {
        this.rssId = rssId;
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
        hash += (rssId != null ? rssId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RssFeeds)) {
            return false;
        }
        RssFeeds other = (RssFeeds) object;
        if ((this.rssId == null && other.rssId != null) || (this.rssId != null && !this.rssId.equals(other.rssId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.RssFeeds[ rssId=" + rssId + " ]";
    }
}
