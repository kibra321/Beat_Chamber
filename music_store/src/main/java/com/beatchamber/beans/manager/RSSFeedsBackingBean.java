/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.manager;

import com.beatchamber.entities.RssFeeds;
import com.beatchamber.exceptions.IllegalOrphanException;
import com.beatchamber.exceptions.NonexistentEntityException;
import com.beatchamber.exceptions.RollbackFailureException;
import com.beatchamber.jpacontroller.RssFeedsJpaController;
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
 * This backing bean is used for RSS Feeds management page
 *
 * @author 1733570 Yan Tang
 */
@Named("theRssFeeds")
@ViewScoped
public class RSSFeedsBackingBean implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(RSSFeedsBackingBean.class);

    @Inject
    private RssFeedsJpaController rssfeedsJpaController;

    private List<RssFeeds> rssfeeds;

    private RssFeeds selectedRssFeed;

    private String link;

    /**
     * Initialization.
     */
    @PostConstruct
    public void init() {
        this.rssfeeds = rssfeedsJpaController.findRssFeedsEntities();
    }

    /**
     * Get all the RssFeeds.
     *
     * @return a list of RssFeeds in the database.
     */
    public List<RssFeeds> getRssFeeds() {
        return rssfeeds;

    }

    /**
     * Get the selected the rss feed.
     *
     * @return the selected rss feed.
     */
    public RssFeeds getSelectedRssFeed() {
        return selectedRssFeed;
    }

    /**
     * Set the selected rss feed.
     *
     * @param selectedRssFeed the selected rss feed.
     */
    public void setSelectedRssFeed(RssFeeds selectedRssFeed) {
        this.selectedRssFeed = selectedRssFeed;
    }

    /**
     * Get the RSS link.
     *
     * @return the RSS link.
     */
    public String getLink() {
        return link;
    }

    /**
     * Set the RSS link.
     *
     * @param link the Rss link.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Initialize the selectRssFeed fields when opening the add new dialog.
     */
    public void openNew() {
        this.selectedRssFeed = new RssFeeds();
        this.setLink("");
    }

    /**
     * Initialize the selectRssFeed fields when editing an existing RSS Feeds.
     *
     * @param rssfeed RssFeeds
     */
    public void openEdit(RssFeeds rssfeed) {
        String[] linkArr = rssfeed.getLink().split("https://");
        this.setLink(linkArr[1]);
    }

    /**
     * Add or save the changes to the RSS feed table.
     */
    public void saveRssFeed() {
        String linkStr = "https://" + this.link;
        this.selectedRssFeed.setLink(linkStr);
        try {
            //when add a new rssfeed
            if (this.selectedRssFeed.getRssId() == null) {
                rssfeedsJpaController.create(this.selectedRssFeed);
                this.rssfeeds.add(this.selectedRssFeed);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("RssFeed Added"));
            } else {
                //when edit a existing rssfeed
                rssfeedsJpaController.edit(this.selectedRssFeed);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("RssFeed Updated"));
            }
        } catch (RollbackFailureException | IllegalOrphanException | NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(RSSFeedsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(RSSFeedsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        PrimeFaces.current().executeScript("PF('manageRssFeedDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-rssfeeds");
    }

    /**
     * The method verifies that the RssFeed is not already in the database.
     *
     * @param context
     * @param component
     * @param value
     */
    public void validateUniqueRssFeedLink(FacesContext context, UIComponent component,
            Object value) {

        LOG.debug("validateUniqueRssFeedLink");

        // Retrieve the value passed to this method
        String rssfeedLink = "https://" + (String) value;

        LOG.debug("validateUniqueRssFeedLink: " + rssfeedLink);
        List<RssFeeds> rssfeedsList = rssfeedsJpaController.findRssFeedsEntities();

        for (RssFeeds rssfeed : rssfeedsList) {
            if (rssfeed.getLink().toLowerCase().equals(rssfeedLink.toLowerCase())
                    && this.selectedRssFeed.getRssId() != rssfeed.getRssId()) {
                String message = context.getApplication().evaluateExpressionGet(context, "#{msgs['duplicateRssFeedLink']}", String.class);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message);
                throw new ValidatorException(msg);
            }
        }
    }

    /**
     * Update the enabled button in the ad table.
     *
     * @param rssfeed
     */
    public void updateEnabled(RssFeeds rssfeed) {
        try {
            rssfeedsJpaController.edit(rssfeed);
        } catch (RollbackFailureException | IllegalOrphanException | NonexistentEntityException ex) {
            java.util.logging.Logger.getLogger(RSSFeedsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(RSSFeedsBackingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (rssfeed.getEnabled()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This RSS Enabled"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This RSS Disabled"));
        }
        PrimeFaces.current().ajax().update("form:messages", "form:dt-rssfeeds");
    }
}
