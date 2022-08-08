package com.beatchamber.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
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
@Table(name = "genres", catalog = "CSgb1w21", schema = "")
@NamedQueries({
    @NamedQuery(name = "Genres.findAll", query = "SELECT g FROM Genres g"),
    @NamedQuery(name = "Genres.findByGenreId", query = "SELECT g FROM Genres g WHERE g.genreId = :genreId"),
    @NamedQuery(name = "Genres.findByGenreName", query = "SELECT g FROM Genres g WHERE g.genreName = :genreName")})
public class Genres implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "genre_id")
    private Integer genreId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "genre_name")
    private String genreName;
    @OneToMany(mappedBy = "genreId")
    private List<GenreToTracks> genreToTracksList;
    @OneToMany(mappedBy = "genreId")
    private List<GenreToAlbum> genreToAlbumList;

    public Genres() {
    }

    public Genres(Integer genreId) {
        this.genreId = genreId;
    }

    public Genres(Integer genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public List<GenreToTracks> getGenreToTracksList() {
        return genreToTracksList;
    }

    public void setGenreToTracksList(List<GenreToTracks> genreToTracksList) {
        this.genreToTracksList = genreToTracksList;
    }

    public List<GenreToAlbum> getGenreToAlbumList() {
        return genreToAlbumList;
    }

    public void setGenreToAlbumList(List<GenreToAlbum> genreToAlbumList) {
        this.genreToAlbumList = genreToAlbumList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (genreId != null ? genreId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Genres)) {
            return false;
        }
        Genres other = (Genres) object;
        if ((this.genreId == null && other.genreId != null) || (this.genreId != null && !this.genreId.equals(other.genreId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.beatchamber.entities.Genres[ genreId=" + genreId + " ]";
    }
    
}
