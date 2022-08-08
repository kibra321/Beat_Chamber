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
@Table(name = "genre_to_tracks", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "GenreToTracks.findAll", query = "SELECT g FROM GenreToTracks g"),
    @NamedQuery(name = "GenreToTracks.findByTablekey", query = "SELECT g FROM GenreToTracks g WHERE g.tablekey = :tablekey")})
public class GenreToTracks implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "tablekey")
    private Integer tablekey;
    @JoinColumn(name = "genre_id", referencedColumnName = "genre_id")
    @ManyToOne
    private Genres genreId;
    @JoinColumn(name = "track_id", referencedColumnName = "track_id")
    @ManyToOne
    private Tracks trackId;

    public GenreToTracks() {
    }

    public GenreToTracks(Integer tablekey) {
        this.tablekey = tablekey;
    }

    public Integer getTablekey() {
        return tablekey;
    }

    public void setTablekey(Integer tablekey) {
        this.tablekey = tablekey;
    }

    public Genres getGenreId() {
        return genreId;
    }

    public void setGenreId(Genres genreId) {
        this.genreId = genreId;
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
        hash += (tablekey != null ? tablekey.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GenreToTracks)) {
            return false;
        }
        GenreToTracks other = (GenreToTracks) object;
        if ((this.tablekey == null && other.tablekey != null) || (this.tablekey != null && !this.tablekey.equals(other.tablekey))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.GenreToTracks[ tablekey=" + tablekey + " ]";
    }
    
}
