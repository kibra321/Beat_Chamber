/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beatchamber.beans.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author 1733570 Yan Tang
 */
@FacesValidator("SalePriceValidator")
public class SalePriceValidator implements Validator{

    @Override
    public void validate(FacesContext context, UIComponent component, Object valueObj) throws ValidatorException {
        Object listedPriceObj = component.getAttributes().get("listedPrice");
        HtmlInputText htmlInputText = (HtmlInputText) context.getViewRoot().findComponent(listedPriceObj.toString());
        if (htmlInputText != null) {
            listedPriceObj = htmlInputText.getValue();
        }
        if (valueObj == null && listedPriceObj == null ) {
            return;
        }

        if ((valueObj == null && listedPriceObj != null) || (valueObj != null && listedPriceObj == null)) {
            FacesMessage message = com.beatchamber.util.Messages.getMessage(
                    "com.beatchamber.bundles.messages", "badPrices", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
        
        double salePrice = (double)valueObj;
        double listedPrice = 0;
        if (listedPriceObj != null) {
            listedPrice = (double)listedPriceObj;
        }
        if (salePrice >= listedPrice) {
            FacesMessage message = com.beatchamber.util.Messages.getMessage(
                    "com.beatchamber.bundles.messages", "badPrices", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }
}
