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
@Table(name = "genre_to_album", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "GenreToAlbum.findAll", query = "SELECT g FROM GenreToAlbum g"),
    @NamedQuery(name = "GenreToAlbum.findByTablekey", query = "SELECT g FROM GenreToAlbum g WHERE g.tablekey = :tablekey")})
public class GenreToAlbum implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "tablekey")
    private Integer tablekey;
    @JoinColumn(name = "album_number", referencedColumnName = "album_number")
    @ManyToOne
    private Albums albumNumber;
    @JoinColumn(name = "genre_id", referencedColumnName = "genre_id")
    @ManyToOne
    private Genres genreId;

    public GenreToAlbum() {
    }

    public GenreToAlbum(Integer tablekey) {
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

    public Genres getGenreId() {
        return genreId;
    }

    public void setGenreId(Genres genreId) {
        this.genreId = genreId;
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
        if (!(object instanceof GenreToAlbum)) {
            return false;
        }
        GenreToAlbum other = (GenreToAlbum) object;
        if ((this.tablekey == null && other.tablekey != null) || (this.tablekey != null && !this.tablekey.equals(other.tablekey))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.GenreToAlbum[ tablekey=" + tablekey + " ]";
    }
    
}
