/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.manager;

import com.beatchamber.entities.Albums;
import com.beatchamber.entities.Artists;
import com.beatchamber.entities.Tracks;
import com.beatchamber.entities.Genres;
import com.beatchamber.entities.GenreToAlbum;
import com.beatchamber.entities.ArtistAlbums;
import com.beatchamber.entities.GenreToTracks;
import com.beatchamber.entities.ArtistsToTracks;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.jpacontroller.AlbumsJpaController;
import com.beatchamber.jpacontroller.ArtistAlbumsJpaController;
import com.beatchamber.jpacontroller.ArtistsJpaController;
import com.beatchamber.jpacontroller.ArtistsToTracksJpaController;
import com.beatchamber.jpacontroller.GenreToAlbumJpaController;
import com.beatchamber.jpacontroller.GenreToTracksJpaController;
import com.beatchamber.jpacontroller.TracksJpaController;
import com.beatchamber.jpacontroller.GenresJpaController;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to show and manage the album information in the album
 * management page.
 *
 * @author 1733570 Yan Tang
 */
@Named("theAlbums")
@ViewScoped
public class AlbumsBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(AlbumsBackingBean.class);

    @Inject
    private AlbumsJpaController albumsJpaController;

    @Inject
    private TracksJpaController tracksJpaController;

    @Inject
    private GenresJpaController genresJpaController;

    @Inject
    private ArtistsJpaController artistsJpaController;

    @Inject
    private GenreToAlbumJpaController genreToAlbumJpaController;

    @Inject
    private ArtistAlbumsJpaController artistAlbumsJpaController;

    @Inject
    private GenreToTracksJpaController genreToTracksJpaController;

    @Inject
    private ArtistsToTracksJpaController artistsToTracksJpaController;

    private List<Albums> albums;
    private Albums selectedAlbum;
    private Tracks selectedTrack;

    private boolean isAddTrack;
    private boolean isRemoved;

    private Integer[] selectedGenres;
    private Integer[] selectedArtists;

    /**
     * Initialization.
     */
    @PostConstruct
    public void init() {
        this.albums = albumsJpaController.findAlbumsEntities();
    }

    /**
     * Get the list of Albums.
     *
     * @return a list of albums
     */
    public List<Albums> getAlbums() {
        return albums;
    }

    /**
     * Get the selected album.
     *
     * @return the selected album.
     */
    public Albums getSelectedAlbum() {
        if (this.selectedAlbum == null) {
            this.selectedAlbum = new Albums();
        }
        return selectedAlbum;
    }

    /**
     * Set the selected album.
     *
     * @param selectedAlbum
     */
    public void setSelectedAlbum(Albums selectedAlbum) {
        this.selectedAlbum = selectedAlbum;
    }

    /**
     * Get the selected track.
     *
     * @return the selected track.
     */
    public Tracks getSelectedTrack() {
        if (this.selectedTrack == null) {
            this.selectedTrack = new Tracks();
        }
        return selectedTrack;
    }

    /**
     * Set the selected track.
     *
     * @param selectedTrack
     */
    public void setSelectedTrack(Tracks selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    /**
     * Get the tag of adding new track.
     *
     * @return true if it is adding new track, otherwise false;
     */
    public boolean getIsAddTrack() {
        return isAddTrack;
    }

    /**
     * Set the tag of adding new track.
     *
     * @param isAddTrack
     */
    public void setIsAddTrack(boolean isAddTrack) {
        this.isAddTrack = isAddTrack;
    }

    /**
     * Get the tag of removal status of the album.
     *
     * @return true if the album is identified as removed, otherwise, false.
     */
    public boolean getIsRemoved() {
        return this.isRemoved;
    }

    /**
     * Set the tag of removal status of the album.
     *
     * @param isRemoved
     */
    public void setIsRemoved(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    /**
     * Get the selected Genre's id.
     *
     * @return an array of all the Genres selected.
     */
    public Integer[] getSelectedGenres() {
        return selectedGenres;
    }

    /**
     * Set the selected Genres.
     *
     * @param genreList a array of genre's id
     */
    public void setSelectedGenres(Integer[] genreList) {
        this.selectedGenres = genreList;
    }

    /**
     * Get the selected Artist's id.
     *
     * @return an array of all the Artists selected.
     */
    public Integer[] getSelectedArtists() {
        return selectedArtists;
    }

    /**
     * Set the selected Artist
     *
     * @param artistsList a array of artist's id
     */
    public void setSelectedArtists(Integer[] artistsList) {
        this.selectedArtists = artistsList;
    }

    /**
     * Get all the genres.
     *
     * @return a list of all the genres in the database
     */
    public List<Genres> getAllGenres() {
        List<Genres> allGenresList = new ArrayList<>();
        this.genresJpaController.findGenresEntities().forEach(item -> allGenresList.add(item));
        return allGenresList;
    }

    /**
     * Get a String of all the genre of the album, and join them with commas.
     *
     * @param album1 the selected album
     * @return a string of the all the genres of the album
     */
    public String getGenresListStr(Albums album1) {
        List<String> selectedList = new ArrayList<>();
        album1.getGenreToAlbumList().forEach(item -> selectedList.add(item.getGenreId().getGenreName()));
        String listStr = String.join(",", selectedList.toArray(new String[selectedList.size()]));
        return listStr;
    }

    /**
     * Get all the artists.
     *
     * @return a list of all the artists in the database.
     */
    public List<Artists> getAllArtists() {
        List<Artists> allArtistsList = new ArrayList<>();
        this.artistsJpaController.findArtistsEntities().forEach(item -> allArtistsList.add(item));
        return allArtistsList;
    }

    /**
     * Get a String of all the artists of the album, and join them with commas.
     *
     * @param album the selected album
     * @return a string of the all the artists of the album
     */
    public String getArtistsListStr(Albums album) {
        List<String> selectedList = new ArrayList<>();
        album.getArtistAlbumsList().forEach(item -> selectedList.add(item.getArtistId().getArtistName()));
        String listStr = String.join(",", selectedList.toArray(new String[selectedList.size()]));
        return listStr;
    }

    /**
     * Get all the tracks of the album.
     *
     * @param albumIdStr the album number
     * @return a list of tracks that in the album
     */
    public List<Tracks> getTrackListInAlbum(String albumIdStr) {
        int albumId = Integer.parseInt(albumIdStr);
        return this.albumsJpaController.findAlbums(albumId).getTracksList();
    }

    /**
     * Initialize the fields when the add new album button is clicked.
     */
    public void openNew() {
        this.selectedAlbum = new Albums();
        List<Integer> selectedList = new ArrayList<>();
        this.setSelectedGenres(selectedList.toArray(new Integer[selectedList.size()]));
        this.setSelectedArtists(selectedList.toArray(new Integer[selectedList.size()]));
        this.setIsRemoved(false);
    }

    /**
     * Set the selected genres and artists and removal status for the selected.
     * album when the edit button is clicked.
     *
     * @param selectedAlbum the selected album
     */
    public void openEdit(Albums selectedAlbum) {

        List<Integer> selectedGenresList = new ArrayList<>();
        selectedAlbum.getGenreToAlbumList().forEach(item -> selectedGenresList.add(item.getGenreId().getGenreId()));
        this.setSelectedGenres(selectedGenresList.toArray(new Integer[selectedGenresList.size()]));

        List<Integer> selectedArtistsList = new ArrayList<>();
        selectedAlbum.getArtistAlbumsList().forEach(item -> selectedArtistsList.add(item.getArtistId().getArtistId()));
        this.setSelectedArtists(selectedArtistsList.toArray(new Integer[selectedArtistsList.size()]));

        this.setIsRemoved(selectedAlbum.getRemovalStatus());

    }

    /**
     * Initialize the fields when the add new track button is clicked.
     *
     * @param selectedAlbum the selected album
     */
    public void openAddNewTrack(Albums selectedAlbum) {
        this.selectedTrack = new Tracks();
        List<Integer> selectedList = new ArrayList<>();
        this.setSelectedGenres(selectedList.toArray(new Integer[selectedList.size()]));
        this.setSelectedArtists(selectedList.toArray(new Integer[selectedList.size()]));
    }

    /**
     * Create a new album or save the changes of the selected album.
     */
    public void saveAlbum() {

        try {
            if (this.selectedAlbum.getAlbumNumber() == null) {
                this.setRemovalStatus();
                this.setTotalTracksToAlbum();
                this.albumsJpaController.create(this.selectedAlbum);
                this.saveGenreToAlbum();
                this.saveArtistsToAlbum();
                this.albums.add(this.selectedAlbum);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Album Added"));
            } else {
                this.setRemovalStatus();
                this.setTotalTracksToAlbum();
                this.deleteGenreToAlbum();
                this.saveGenreToAlbum();
                this.deleteArtistsToAlbum();
                this.saveArtistsToAlbum();
                this.albumsJpaController.edit(this.selectedAlbum);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Album Updated"));

            }

        } catch (RollbackFailureException | IllegalOrphanException | NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        PrimeFaces.current().executeScript("PF('manageAlbumDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-albums");
    }

    /**
     * Set the album to a removed status when remove button is clicked.
     */
    public void removeAlbum() {
        //Set the album to a removed status
        try {
            this.selectedAlbum.setRemovalStatus(true);
            this.selectedAlbum.setRemovalDate(new Date());
            this.albumsJpaController.edit(this.selectedAlbum);
        } catch (NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Set all the tracks in the album to a removed status when the album is removed
        if (!this.selectedAlbum.getTracksList().isEmpty()) {
            for (Tracks track : this.selectedAlbum.getTracksList()) {
                try {
                    track.setRemoved(true);
                    this.tracksJpaController.edit(track);
                } catch (NonexistentEntityException ex) {
                    java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Album Removed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-albums");

    }

    /**
     * Remove the selected track from the selected album.
     */
    public void removeTrackFromAlbum() {
        //remove the selected track
        try {
            this.selectedTrack.setRemoved(true);
            this.tracksJpaController.edit(this.selectedTrack);
        } catch (NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Track Removed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-albums");

    }

    /**
     * Add a new track to the selected album.
     */
    public void addTrackToAlbum() {
        //Add a new track to the album
        if (this.selectedTrack.getTrackId() == null) {
            try {
                this.setTrackFields();
                this.tracksJpaController.create(this.selectedTrack);
                this.saveGenreToTrack();
                this.saveArtistsToTrack();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Track Added"));
            } catch (RollbackFailureException ex) {
                java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            PrimeFaces.current().executeScript("PF('addTrackDialog').hide()");
            PrimeFaces.current().ajax().update("form:messages", "form:dt-albums");
        }

        //Increase the number of totalTracks in the album table by one.
        try {
            this.selectedAlbum = this.selectedTrack.getAlbumNumber();
            this.selectedAlbum.setTotalTracks(this.selectedAlbum.getTracksList().size());
            this.selectedTrack.setAlbumNumber(this.selectedAlbum);
            this.tracksJpaController.edit(selectedTrack);
            this.albumsJpaController.edit(selectedAlbum);
        } catch (NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Set all the necessary fields when add a new track to the selected album.
     */
    private void setTrackFields() {
        this.selectedTrack.setSelectionNumber(this.selectedAlbum.getTracksList().size() + 1);
        this.selectedTrack.setPlayLength("1:00");
        this.selectedTrack.setEntryDate(new Date());
        this.selectedTrack.setAlbumNumber(this.selectedAlbum);

        for (Genres genre : this.getAllGenres()) {
            if (this.selectedGenres[0] != genre.getGenreId()) {
                this.selectedTrack.setMusicCategory(genre.getGenreName());
            }
        }
    }

    /**
     * Set the removal status when edit the selected album.
     */
    private void setRemovalStatus() {
        this.selectedAlbum.setRemovalStatus(this.isRemoved);

        if (this.isRemoved) {
            this.selectedAlbum.setRemovalDate(new Date());
        } else {
            this.selectedAlbum.setRemovalDate(null);
        }

        //Change all the tracks removal status when the album's status is changed.
        if (this.selectedAlbum.getAlbumNumber() != null) {
            for (Tracks track : this.selectedAlbum.getTracksList()) {
                try {
                    track.setRemoved(this.isRemoved);
                    this.tracksJpaController.edit(track);
                } catch (NonexistentEntityException ex) {
                    java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Set the number of the total tracks.
     */
    private void setTotalTracksToAlbum() {
        int numOfTracks = 0;
        if (this.selectedAlbum.getAlbumNumber() != null) {
            numOfTracks = this.selectedAlbum.getTracksList().size();
        }
        this.selectedAlbum.setTotalTracks(numOfTracks);
    }

    /**
     * Save the selected genres to the GenreToAlbum list.
     */
    private void saveGenreToAlbum() {

        List<GenreToAlbum> genreAlbumsList = new ArrayList<>();
        for (Integer item : this.selectedGenres) {
            try {
                GenreToAlbum genreToAlbum = new GenreToAlbum();
                genreToAlbum.setAlbumNumber(this.selectedAlbum);
                genreToAlbum.setGenreId(this.genresJpaController.findGenres(item));
                this.genreToAlbumJpaController.create(genreToAlbum);
                genreAlbumsList.add(genreToAlbum);
            } catch (RollbackFailureException ex) {
                java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.selectedAlbum.setGenreToAlbumList(genreAlbumsList);
    }

    /**
     * Delete all the existing genres in the selected album.
     */
    private void deleteGenreToAlbum() {

        if (this.selectedAlbum.getAlbumNumber() != null) {
            this.selectedAlbum.getGenreToAlbumList().forEach(genreToAlbum -> {
                try {
                    this.genreToAlbumJpaController.destroy(genreToAlbum.getTablekey());
                } catch (IllegalOrphanException | NonexistentEntityException | NotSupportedException | SystemException | RollbackFailureException | RollbackException | HeuristicMixedException | HeuristicRollbackException ex) {
                    java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    /**
     * Save the selected artists to the ArtistsToAlbum list.
     */
    private void saveArtistsToAlbum() {

        List<ArtistAlbums> artistAlbumsList = new ArrayList<>();
        for (Integer item : this.selectedArtists) {
            try {
                ArtistAlbums artistAlbums = new ArtistAlbums();
                artistAlbums.setAlbumNumber(this.selectedAlbum);
                artistAlbums.setArtistId(this.artistsJpaController.findArtists(item));
                this.artistAlbumsJpaController.create(artistAlbums);
                artistAlbumsList.add(artistAlbums);
            } catch (RollbackFailureException ex) {
                java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.selectedAlbum.setArtistAlbumsList(artistAlbumsList);
    }

    /**
     * Delete all the existing artists in the selected album.
     */
    private void deleteArtistsToAlbum() {

        if (this.selectedAlbum.getAlbumNumber() != null) {
            this.selectedAlbum.getArtistAlbumsList().forEach(artistAlbum -> {
                try {
                    this.artistAlbumsJpaController.destroy(artistAlbum.getTablekey());
                } catch (NonexistentEntityException | NotSupportedException | SystemException | RollbackFailureException | RollbackException | HeuristicMixedException | HeuristicRollbackException ex) {
                    java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    /**
     * Save the selected genres to the GenreToTrack list.
     */
    private void saveGenreToTrack() {

        List<GenreToTracks> genreTrackList = new ArrayList<>();
        for (Integer item : this.selectedGenres) {
            try {
                GenreToTracks genreToTracks = new GenreToTracks();
                genreToTracks.setTrackId(this.selectedTrack);
                genreToTracks.setGenreId(this.genresJpaController.findGenres(item));
                this.genreToTracksJpaController.create(genreToTracks);
                genreTrackList.add(genreToTracks);
            } catch (RollbackFailureException ex) {
                java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.selectedTrack.setGenreToTracksList(genreTrackList);
    }

    /**
     * Save the selected artist to the ArtistsToTrack list
     */
    private void saveArtistsToTrack() {

        List<ArtistsToTracks> artistTrackList = new ArrayList<>();
        for (Integer item : this.selectedArtists) {
            try {
                ArtistsToTracks artistTrack = new ArtistsToTracks();
                artistTrack.setTrackId(this.selectedTrack);
                artistTrack.setArtistId(this.artistsJpaController.findArtists(item));
                this.artistsToTracksJpaController.create(artistTrack);
                artistTrackList.add(artistTrack);
            } catch (RollbackFailureException ex) {
                java.util.logging.Logger.getLogger(AlbumsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.selectedTrack.setArtistsToTracksCollection(artistTrackList);
    }

    /**
     * Get a total sales number for a album
     *
     * @param album the album need to show the total sales
     * @return the number of total sales
     */
    public int getTotalSales(Albums album) {
        int totalSales = 0;
        if (album.getOrderAlbumCollection() != null) {
            totalSales = album.getOrderAlbumCollection().size();
        }
        return totalSales;
    }
}
