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
@Table(name = "artist_albums", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "ArtistAlbums.findAll", query = "SELECT a FROM ArtistAlbums a"),
    @NamedQuery(name = "ArtistAlbums.findByTablekey", query = "SELECT a FROM ArtistAlbums a WHERE a.tablekey = :tablekey")})
public class ArtistAlbums implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "tablekey")
    private Integer tablekey;
    @JoinColumn(name = "album_number", referencedColumnName = "album_number")
    @ManyToOne(optional = false)
    private Albums albumNumber;
    @JoinColumn(name = "artist_id", referencedColumnName = "artist_id")
    @ManyToOne(optional = false)
    private Artists artistId;

    public ArtistAlbums() {
    }

    public ArtistAlbums(Integer tablekey) {
        this.tablekey = tablekey;
    }

    public Integer getTablekey() {
        return tablekey;
    }

    public void setTablekey(Integer tablekey) {
        this.tablekey = tablekey;
    }

    public Albums getAlbumNumber() {
        return albumNumber;
    }

    public void setAlbumNumber(Albums albumNumber) {
        this.albumNumber = albumNumber;
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
        if (!(object instanceof ArtistAlbums)) {
            return false;
        }
        ArtistAlbums other = (ArtistAlbums) object;
        if ((this.tablekey == null && other.tablekey != null) || (this.tablekey != null && !this.tablekey.equals(other.tablekey))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.ArtistAlbums[ tablekey=" + tablekey + " ]";
    }
    
}
