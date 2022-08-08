package com.beatchamber.entities;

import java.io.Serializable;
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

/**
 *
 * @author kibra
 */
@Entity
@Table(name = "artists_to_tracks", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "ArtistsToTracks.findAll", query = "SELECT a FROM ArtistsToTracks a"),
    @NamedQuery(name = "ArtistsToTracks.findByTablekey", query = "SELECT a FROM ArtistsToTracks a WHERE a.tablekey = :tablekey"),
    @NamedQuery(name = "ArtistsToTracks.findByTrackId", query = "SELECT a FROM ArtistsToTracks a WHERE a.trackId = :trackId")})
public class ArtistsToTracks implements Serializable {

    @JoinColumn(name = "track_id", referencedColumnName = "track_id")
    @ManyToOne(optional = false)
    private Tracks trackId;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "tablekey")
    private Integer tablekey;
    @JoinColumn(name = "artist_id", referencedColumnName = "artist_id")
    @ManyToOne(optional = false)
    private Artists artistId;

    public ArtistsToTracks() {
    }

    public ArtistsToTracks(Integer tablekey) {
        this.tablekey = tablekey;
    }

    public Integer getTablekey() {
        return tablekey;
    }

    public void setTablekey(Integer tablekey) {
        this.tablekey = tablekey;
    }

    public Tracks getTrackId() {
        return trackId;
    }

    public void setTrackId(Tracks trackId) {
        this.trackId = trackId;
    }

    public Artists getArtistId() {
        return artistId;
    }

    public void setArtistId(Artists artistId) {
        this.artistId = artistId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tablekey != null ? tablekey.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ArtistsToTracks)) {
            return false;
        }
        ArtistsToTracks other = (ArtistsToTracks) object;
        if ((this.tablekey == null && other.tablekey != null) || (this.tablekey != null && !this.tablekey.equals(other.tablekey))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.ArtistsToTracks[ tablekey=" + tablekey + " ]";
    }
    
}