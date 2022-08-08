/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.manager;

import com.beatchamber.entities.Ads;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.jpacontroller.AdsJpaController;
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
 * This backing bean is used for Ads management page
 *
 * @author 1733570 Yan Tang
 */
@Named("theAds")
@ViewScoped
public class AdsBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(AdsBackingBean.class);

    @Inject
    private AdsJpaController adsJpaController;

    private List<Ads> ads;

    private Ads selectedAd;

    private String link;

    /**
     * Initialization.
     */
    @PostConstruct
    public void init() {
        this.ads = adsJpaController.findAdsEntities();
    }

    /**
     * Get all the Ads.
     *
     * @return a list of Ads in the database.
     */
    public List<Ads> getAds() {
        return ads;

    }

    /**
     * Get the selected ad.
     *
     * @return the selected ad.
     */
    public Ads getSelectedAd() {
        return selectedAd;
    }

    /**
     * Set the selected ad.
     *
     * @param selectedAd the selected ad.
     */
    public void setSelectedAd(Ads selectedAd) {
        this.selectedAd = selectedAd;
    }

    /**
     * Get the ad link.
     *
     * @return the ad link.
     */
    public String getLink() {
        return link;
    }

    /**
     * Set the ad link.
     *
     * @param link the ad link.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Initialize the selectAd fields when opening the add new dialog.
     */
    public void openNew() {
        this.selectedAd = new Ads();
        this.setLink("");
    }

    /**
     * Initialize the selectAd fields when editing an existing ad Feeds.
     *
     * @param ad Ads
     */
    public void openEdit(Ads ad) {
        String[] linkArr = ad.getLink().split("https://");
        this.setLink(linkArr[1]);
    }

    /**
     * Add or save the changes to the ad feed table.
     */
    public void saveAd() {
        String linkStr = "https://" + this.link;
        this.selectedAd.setLink(linkStr);
        try {
            //when add a new ad
            if (this.selectedAd.getAdId() == null) {
                adsJpaController.create(this.selectedAd);
                this.ads.add(this.selectedAd);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Ad Added"));
            } else {
                //when edit a existing ad
                adsJpaController.edit(this.selectedAd);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Ad Updated"));
            }
        } catch (RollbackFailureException | IllegalOrphanException | NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(AdsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AdsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        PrimeFaces.current().executeScript("PF('manageAdDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-ads");
    }

    /**
     * The method verifies that the Ad Name is not already in the database.
     *
     * @param context
     * @param component
     * @param value
     */
    public void validateUniqueAdName(FacesContext context, UIComponent component,
            Object value) {

        LOG.debug("validateUniqueAdName");

        // Retrieve the value passed to this method
        String adName = (String) value;

        LOG.debug("validateUniqueAdLink: " + adName);
        List<Ads> adsList = adsJpaController.findAdsEntities();

        for (Ads ad : adsList) {
            if (ad.getFileName().toLowerCase().equals(adName.toLowerCase())
                    && this.selectedAd.getAdId() != ad.getAdId()) {
                String message = context.getApplication().evaluateExpressionGet(context, "#{msgs['duplicateAdName']}", String.class);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                throw new ValidatorException(msg);
            }
        }
    }

    /**
     * The method verifies that the Ad Link is not already in the database.
     *
     * @param context
     * @param component
     * @param value
     */
    public void validateUniqueAdLink(FacesContext context, UIComponent component,
            Object value) {

        LOG.debug("validateUniqueAdLink");

        // Retrieve the value passed to this method
        String adLink = "https://"+(String) value;

        LOG.debug("validateUniqueAdLink: " + adLink);
        List<Ads> adsList = adsJpaController.findAdsEntities();

        for (Ads ad : adsList) {
            if (ad.getLink().toLowerCase().equals(adLink.toLowerCase())
                    && this.selectedAd.getAdId() != ad.getAdId()) {
                String message = context.getApplication().evaluateExpressionGet(context, "#{msgs['duplicateAdLink']}", String.class);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                throw new ValidatorException(msg);
            }
        }
    }
    
    /**
     * Update the enabled button in the ad table.
     * @param ad 
     */
    public void updateEnabled(Ads ad) {
        try {
            this.adsJpaController.edit(ad);
        } catch (RollbackFailureException | IllegalOrphanException | NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(AdsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AdsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (ad.getEnabled()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This AD Enabled"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This AD Disabled"));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-ads");
    }
}
