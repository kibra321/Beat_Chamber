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
import com.beatchamber.entities.GenreToTracks;
import com.beatchamber.entities.ArtistsToTracks;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.jpacontroller.AlbumsJpaController;
import com.beatchamber.jpacontroller.ArtistsJpaController;
import com.beatchamber.jpacontroller.ArtistsToTracksJpaController;
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
 * This class is used to show and manage the track information in the track
 * management page.
 *
 * @author 1733570 Yan Tang
 */
@Named("theTracks")
@ViewScoped
public class TracksBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(TracksBackingBean.class);

    @Inject
    private AlbumsJpaController albumsJpaController;

    @Inject
    private TracksJpaController tracksJpaController;

    @Inject
    private GenresJpaController genresJpaController;

    @Inject
    private ArtistsJpaController artistsJpaController;

    @Inject
    private GenreToTracksJpaController genreToTracksJpaController;

    @Inject
    private ArtistsToTracksJpaController artistsToTracksJpaController;

    private List<Tracks> tracks;
    private Tracks selectedTrack;
    private Albums selectedAlbum;
    private Albums oldSelectedTrack;

    private boolean isRemoved;

    private Integer[] selectedGenres;
    private Integer[] selectedArtists;

    /**
     * Initialization.
     */
    @PostConstruct
    public void init() {
        this.tracks = this.tracksJpaController.findTracksEntities();
    }

    /**
     * Get the list of Tracks.
     *
     * @return a list of tracks
     */
    public List<Tracks> getTracks() {
        return this.tracks;
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
     * Get the selected album.
     *
     * @return the selected track.
     */
    public Albums getSelectedAlbum() {
        if (this.selectedAlbum == null) {
            this.selectedAlbum = new Albums();
        }
        return selectedAlbum;
    }

    /**
     * Set the selected Album.
     *
     * @param selectedAlbum
     */
    public void setSelectedAlbum(Albums selectedAlbum) {
        this.selectedAlbum = selectedAlbum;
        PrimeFaces.current().ajax().update("form:messages", "form:manage-image-content");
    }

    /**
     * Get the tag of removal status of the track.
     *
     * @return true if the track is identified as removed, otherwise, false.
     */
    public boolean getIsRemoved() {
        return this.isRemoved;
    }

    /**
     * Set the tag of removal status of the track.
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
     * Get all the albums.
     *
     * @return a list of all the albums in the database
     */
    public List<Albums> getAllAlbums() {
        List<Albums> allAlbumsList = new ArrayList<>();
        this.albumsJpaController.findAlbumsEntities().forEach(item -> allAlbumsList.add(item));
        return allAlbumsList;
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
     * Get a String of all the genre of the track, and join them with commas.
     *
     * @param track the selected track
     * @return a string of the all the genres of the track
     */
    public String getGenresListStr(Tracks track) {
        List<String> selectedList = new ArrayList<>();
        track.getGenreToTracksList().forEach(item -> selectedList.add(item.getGenreId().getGenreName()));
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
     * Get a String of all the artists of the track, and join them with commas.
     *
     * @param track the selected track
     * @return a string of the all the artists of the track
     */
    public String getArtistsListStr(Tracks track) {
        List<String> selectedList = new ArrayList<>();
        track.getArtistsToTracksCollection().forEach(item -> selectedList.add(item.getArtistId().getArtistName()));
        String listStr = String.join(",", selectedList.toArray(new String[selectedList.size()]));
        return listStr;
    }

    /**
     * Initialize the fields when the add new track button is clicked.
     */
    public void openNew() {
        this.selectedTrack = new Tracks();
        this.selectedAlbum = new Albums();
        this.oldSelectedTrack = new Albums();
        List<Integer> selectedList = new ArrayList<>();
        this.setSelectedGenres(selectedList.toArray(new Integer[selectedList.size()]));
        this.setSelectedArtists(selectedList.toArray(new Integer[selectedList.size()]));
        this.setIsRemoved(false);
    }

    /**
     * Set the selected genres and artists and removal status for the selected
     * track when the edit button is clicked.
     *
     * @param selectedTrack the selected track
     */
    public void openEdit(Tracks selectedTrack) {

        List<Integer> selectedGenresList = new ArrayList<>();
        selectedTrack.getGenreToTracksList().forEach(item -> selectedGenresList.add(item.getGenreId().getGenreId()));
        this.setSelectedGenres(selectedGenresList.toArray(new Integer[selectedGenresList.size()]));

        List<Integer> selectedArtistsList = new ArrayList<>();
        selectedTrack.getArtistsToTracksCollection().forEach(item -> selectedArtistsList.add(item.getArtistId().getArtistId()));
        this.setSelectedArtists(selectedArtistsList.toArray(new Integer[selectedArtistsList.size()]));

        this.setIsRemoved(selectedTrack.getRemoved());
        this.setSelectedAlbum(selectedTrack.getAlbumNumber());
        this.oldSelectedTrack = new Albums();
    }

    /**
     * Create a new Track or save the changes of the selected Track.
     */
    public void saveTrack() {

        try {
            if (this.selectedTrack.getTrackId() == null) {
                this.setTrackFields();
                this.tracksJpaController.create(this.selectedTrack);
                this.setTotalTracksToAlbum();
                this.saveGenreToTrack();
                this.saveArtistsToTrack();
                this.tracks.add(this.selectedTrack);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Track Added"));
            } else {
                this.setTrackFields();
                this.deleteGenreToTrack();
                this.saveGenreToTrack();
                this.deleteArtistsToTrack();
                this.saveArtistsToTrack();
                this.setTotalTracksToAlbum();
                this.tracksJpaController.edit(this.selectedTrack);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Track Updated"));

            }

        } catch (RollbackFailureException | IllegalOrphanException | NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(TracksBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(TracksBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        PrimeFaces.current().executeScript("PF('manageTrackDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-tracks");
    }

    /**
     * Set the track to a removed status when remove button is clicked.
     */
    public void removeTrack() {
        //Set the track to a removed status
        try {
            this.selectedTrack.setRemoved(true);
            this.tracksJpaController.edit(this.selectedTrack);
        } catch (NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(TracksBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(TracksBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Track Removed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-tracks");

    }

    /**
     * Set all the necessary fields when add a new track to the selected track.
     */
    private void setTrackFields() {
        if (this.selectedTrack.getTrackId() == null) {
            this.selectedTrack.setPlayLength("1:00");
            this.selectedTrack.setEntryDate(new Date());
        }

        if (this.selectedTrack.getTrackId() != null
                && this.selectedTrack.getAlbumNumber().getAlbumNumber().intValue() != this.selectedAlbum.getAlbumNumber().intValue()) {
            this.oldSelectedTrack = this.selectedTrack.getAlbumNumber();
        }

        if (this.selectedTrack.getTrackId() == null
                || this.selectedTrack.getAlbumNumber().getAlbumNumber().intValue() != this.selectedAlbum.getAlbumNumber().intValue()) {
            this.selectedTrack.setSelectionNumber(this.selectedAlbum.getTracksList().size() + 1);
            this.selectedTrack.setAlbumNumber(this.selectedAlbum);
        }

        this.selectedTrack.setRemoved(this.isRemoved);

        for (Genres genre : this.getAllGenres()) {
            if (this.selectedGenres[0] != genre.getGenreId()) {
                this.selectedTrack.setMusicCategory(genre.getGenreName());
            }
        }
    }

    /**
     * Set the number of the total tracks.
     */
    private void setTotalTracksToAlbum() {
        if (this.selectedTrack.getAlbumNumber() != null) {
            int numOfTracks = this.selectedTrack.getAlbumNumber().getTracksList().size();
            this.selectedTrack.getAlbumNumber().setTotalTracks(numOfTracks);

            try {
                Albums album = this.selectedTrack.getAlbumNumber();
                this.albumsJpaController.edit(album);
            } catch (NonexistentEntityException ex) {
                java.util.logging.Logger.getLogger(TracksBackingBean.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(TracksBackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (this.oldSelectedTrack.getAlbumNumber() != null) {
            int numOfTracks = this.oldSelectedTrack.getTracksList().size();
            this.oldSelectedTrack.setTotalTracks(numOfTracks);

            try {
                this.albumsJpaController.edit(this.oldSelectedTrack);
            } catch (NonexistentEntityException ex) {
                java.util.logging.Logger.getLogger(TracksBackingBean.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(TracksBackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Save the selected genres to the GenreToTrack list.
     */
    private void saveGenreToTrack() {

        List<GenreToTracks> genreTracksList = new ArrayList<>();
        for (Integer item : this.selectedGenres) {
            try {
                GenreToTracks genreToTracks = new GenreToTracks();
                genreToTracks.setTrackId(this.selectedTrack);
                genreToTracks.setGenreId(this.genresJpaController.findGenres(item));
                this.genreToTracksJpaController.create(genreToTracks);
                genreTracksList.add(genreToTracks);
            } catch (RollbackFailureException ex) {
                java.util.logging.Logger.getLogger(TracksBackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.selectedTrack.setGenreToTracksList(genreTracksList);
    }

    /**
     * Delete all the existing genres in the selected track.
     */
    private void deleteGenreToTrack() {

        if (this.selectedTrack.getTrackId() != null) {
            this.selectedTrack.getGenreToTracksList().forEach(genreToTrack -> {
                try {
                    this.genreToTracksJpaController.destroy(genreToTrack.getTablekey());
                } catch (IllegalOrphanException | NonexistentEntityException | NotSupportedException | SystemException | RollbackFailureException | RollbackException | HeuristicMixedException | HeuristicRollbackException ex) {
                    java.util.logging.Logger.getLogger(TracksBackingBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    /**
     * Save the selected artists to the ArtistsToTrack list.
     */
    private void saveArtistsToTrack() {

        List<ArtistsToTracks> artistTracksList = new ArrayList<>();
        for (Integer item : this.selectedArtists) {
            try {
                ArtistsToTracks artistTracks = new ArtistsToTracks();
                artistTracks.setTrackId(this.selectedTrack);
                artistTracks.setArtistId(this.artistsJpaController.findArtists(item));
                this.artistsToTracksJpaController.create(artistTracks);
                artistTracksList.add(artistTracks);
            } catch (RollbackFailureException ex) {
                java.util.logging.Logger.getLogger(TracksBackingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.selectedTrack.setArtistsToTracksCollection(artistTracksList);
    }

    /**
     * Delete all the existing artists in the selected track.
     */
    private void deleteArtistsToTrack() {

        if (this.selectedTrack.getTrackId() != null) {
            this.selectedTrack.getArtistsToTracksCollection().forEach(artistTrack -> {
                try {
                    this.artistsToTracksJpaController.destroy(artistTrack.getTablekey());
                } catch (NonexistentEntityException | NotSupportedException | SystemException | RollbackFailureException | RollbackException | HeuristicMixedException | HeuristicRollbackException | IllegalOrphanException ex) {
                    java.util.logging.Logger.getLogger(TracksBackingBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }
    
    /**
     * Get a total sales number for a track.
     *
     * @param track the track need to show the total sales
     * @return the number of total sales
     */
    public int getTotalSales(Tracks track) {
        int totalSales = 0;
        if (track.getOrderTrackCollection() != null) {
            totalSales = track.getOrderTrackCollection().size();
        }
        return totalSales;
    }

}
