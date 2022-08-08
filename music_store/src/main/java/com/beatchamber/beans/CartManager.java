/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author kibra
 */
public class CartManager {
    CookieManager cookies= new CookieManager();
    
    
    
    /**
     * This method should reload the page
     * found on https://stackoverflow.com/questions/32947472/how-to-reload-page-when-a-button-is-clicked
     * @throws IOException 
     */
    public void reload() throws IOException {
    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
    ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
}
    
    /**
     * This method will remove the selection from the cart and will reload the page so we can see the updated version of the page
     * @param IdOfItemToRemove
     * @throws IOException 
     */
    public void removeSelection(String IdOfItemToRemove) throws InterruptedException {
        this.cookies.removeItemFromCart(IdOfItemToRemove);
        try {
            //This is used to update the page
            reload();
        } catch (IOException ex) {
            Logger.getLogger(CartManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
