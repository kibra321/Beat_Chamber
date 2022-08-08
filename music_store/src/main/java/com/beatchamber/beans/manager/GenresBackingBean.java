/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.manager;

import com.beatchamber.entities.Genres;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.jpacontroller.GenresJpaController;
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
 * This backing bean is used for genre management page
 *
 * @author 1733570 Yan Tang
 */
@Named("theGenres")
@ViewScoped
public class GenresBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(GenresBackingBean.class);

    @Inject
    private GenresJpaController genresJpaController;

    private List<Genres> genres;

    private Genres selectedGenre;

    /**
     * Initialization.
     */
    @PostConstruct
    public void init() {
        this.genres = genresJpaController.findGenresEntities();
    }

    /**
     * Get all the genres.
     *
     * @return a list of genres in the database.
     */
    public List<Genres> getGenres() {
        return genres;

    }

    /**
     * Get the selected the genre.
     *
     * @return the selected genre.
     */
    public Genres getSelectedGenre() {
        return selectedGenre;
    }

    /**
     * Set the selected genre.
     *
     * @param selectedGenre the selected genre.
     */
    public void setSelectedGenre(Genres selectedGenre) {
        this.selectedGenre = selectedGenre;
    }

    /**
     * Initialize the selectGenre field when opening the add new dialog.
     */
    public void openNew() {
        this.selectedGenre = new Genres();
    }

    /**
     * Add or save the changes to the genre table.
     */
    public void saveGenre() {
        try {
            //when add a new genre
            if (this.selectedGenre.getGenreId() == null) {
                genresJpaController.create(this.selectedGenre);
                this.genres.add(this.selectedGenre);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Genre Added"));
            } else {
                //when edit a existing genre
                genresJpaController.edit(this.selectedGenre);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Genre Updated"));
            }
        } catch (RollbackFailureException | IllegalOrphanException | NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(GenresBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(GenresBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        PrimeFaces.current().executeScript("PF('manageGenreDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-genres");
    }

    /**
     * The method verifies that the Genre is not already in the database.
     *
     * @param context
     * @param component
     * @param value
     */
    public void validateUniqueGenreName(FacesContext context, UIComponent component,
            Object value) {

        LOG.debug("validateUniqueGenreName");

        // Retrieve the value passed to this method
        String genreName = (String) value;

        LOG.debug("validateUniqueGenreName: " + genreName);
        List<Genres> genresList = genresJpaController.findGenresEntities();

        for (Genres genre : genresList) {
            if (genre.getGenreName().toLowerCase().equals(genreName.toLowerCase())
                    && this.selectedGenre.getGenreId() != genre.getGenreId()) {
                String message = context.getApplication().evaluateExpressionGet(context, "#{msgs['duplicateGenreName']}", String.class);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                throw new ValidatorException(msg);
            }
        }
    }
}
