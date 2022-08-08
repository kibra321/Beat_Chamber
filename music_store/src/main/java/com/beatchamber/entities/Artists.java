/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.entities;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author kibra
 */
@Entity
@Table(name = "artists", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Artists.findAll", query = "SELECT a FROM Artists a"),
    @NamedQuery(name = "Artists.findByArtistId", query = "SELECT a FROM Artists a WHERE a.artistId = :artistId"),
    @NamedQuery(name = "Artists.findByArtistName", query = "SELECT a FROM Artists a WHERE a.artistName = :artistName")})
public class Artists implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "artist_name")
    private String artistName;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "artist_id")
    private Integer artistId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "artistId")
    private List<ArtistAlbums> artistAlbumsList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "artistId")
    private List<ArtistsToTracks> artistsToTracksList;

    public Artists() {
    }

    public Artists(Integer artistId) {
        this.artistId = artistId;
    }

    public Artists(Integer artistId, String artistName) {
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public List<ArtistAlbums> getArtistAlbumsList() {
        return artistAlbumsList;
    }

    public void setArtistAlbumsList(List<ArtistAlbums> artistAlbumsList) {
        this.artistAlbumsList = artistAlbumsList;
    }

    public List<ArtistsToTracks> getArtistsToTracksList() {
        return artistsToTracksList;
    }

    public void setArtistsToTracksList(List<ArtistsToTracks> artistsToTracksList) {
        this.artistsToTracksList = artistsToTracksList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (artistId != null ? artistId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Artists)) {
            return false;
        }
        Artists other = (Artists) object;
        if ((this.artistId == null && other.artistId != null) || (this.artistId != null && !this.artistId.equals(other.artistId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.Artists[ artistId=" + artistId + " ]";
    }
    
}