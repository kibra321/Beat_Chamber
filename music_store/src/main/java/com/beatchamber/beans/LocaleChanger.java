package com.beatchamber.beans;

import java.io.Serializable;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * Class to change the language of the website. Using JSFi18nChanger example
 * from Ken
 * 
 * @author Susan Vuu - 1735488
 */
@Named
@SessionScoped
public class LocaleChanger implements Serializable {
    
    private Locale locale;
    private boolean currentEnglish;
    
    /**
     * Default constructor
     */
    public LocaleChanger() {
        this.currentEnglish = true;
    }
    
    /**
     * Set the locale before the class is being used
     */
    @PostConstruct
    public void init() {
        locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    }
    
    /**
     * @return The current locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @return If the current locale is English.
     */
    public boolean isCurrentEnglish() {
        return currentEnglish;
    }

    /**
     * @return The current language of the locale
     */
    public String getLanguage() {
        return locale.getLanguage();
    }

    /**
     * Change the language to the one requested
     * @param language
     * @param country 
     */
    public void setLanguage(String language, String country) {
        locale = new Locale(language, country);
        
        //Used for rendering a specific link that will change for either English or French
        this.currentEnglish = language.equals("en");
        
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }
}
