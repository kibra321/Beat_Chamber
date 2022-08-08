/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.manager;

import com.beatchamber.entities.Artists;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.jpacontroller.ArtistsJpaController;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This backing bean is used for artist management page
 *
 * @author 1733570 Yan Tang
 */
@Named("theArtists")
@ViewScoped
public class ArtistsBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ArtistsBackingBean.class);

    @Inject
    private ArtistsJpaController artistsJpaController;

    private List<Artists> artists;

    private Artists selectedArtist;

    /**
     * Initialization.
     */
    @PostConstruct
    public void init() {
        this.artists = artistsJpaController.findArtistsEntities();
    }

    /**
     * Get all the artists.
     *
     * @return a list of artists in the database.
     */
    public List<Artists> getArtists() {
        return artists;

    }

    /**
     * Get the selected the artist.
     *
     * @return the selected artist.
     */
    public Artists getSelectedArtist() {
        return selectedArtist;
    }

    /**
     * Set the selected artist.
     *
     * @param selectedArtist the selected artist.
     */
    public void setSelectedArtist(Artists selectedArtist) {
        this.selectedArtist = selectedArtist;
    }

    /**
     * Initialize the selectArtist field when opening the add new dialog.
     */
    public void openNew() {
        this.selectedArtist = new Artists();
    }

    /**
     * Add or save the changes to the artist table.
     */
    public void saveArtist() {
        try {
            //when add a new artist
            if (this.selectedArtist.getArtistId() == null) {
                artistsJpaController.create(this.selectedArtist);
                this.artists.add(this.selectedArtist);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Artist Added"));
            } else {
                //when edit a existing artist
                artistsJpaController.edit(this.selectedArtist);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Artist Updated"));
            }
        } catch (RollbackFailureException | IllegalOrphanException | NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(ArtistsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ArtistsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        PrimeFaces.current().executeScript("PF('manageArtistDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-artists");
    }

    /**
     * The method verifies that the Artist is not already in the database.
     *
     * @param context
     * @param component
     * @param value
     */
    public void validateUniqueArtistName(FacesContext context, UIComponent component,
            Object value) {

        LOG.debug("validateUniqueArtistName");

        // Retrieve the value passed to this method
        String artistName = (String) value;

        LOG.debug("validateUniqueArtistName: " + artistName);
        List<Artists> artistsList = artistsJpaController.findArtistsEntities();

        for (Artists artist : artistsList) {
            if (artist.getArtistName().toLowerCase().equals(artistName.toLowerCase())
                    && this.selectedArtist.getArtistId() != artist.getArtistId()) {
                String message = context.getApplication().evaluateExpressionGet(context, "#{msgs['duplicateArtistName']}", String.class);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                throw new ValidatorException(msg);
            }
        }
    }
}
